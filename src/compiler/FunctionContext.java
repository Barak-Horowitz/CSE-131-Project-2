package compiler;

import java.io.PrintStream;
import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;

public class FunctionContext {
    public LinkedList<BlockContext> blocks;
    public List<IRInstruction> allInstructions;
    public Map<String, BlockContext> labelLinks;

    public Map<IRVariableOperand, Set<IRVariableOperand>> intersections = new HashMap<>();

    private IRFunction function;

    public FunctionContext(IRFunction function) throws IRException {
        this.function = function;
    }

    private void interpretBlocks() throws IRException {
        blocks = new LinkedList<>();

        List<IRInstruction> instructions = new java.util.ArrayList<>();
        Collection<String> labels = new java.util.ArrayList<>();
        labels.add(function.name);

        for(IRInstruction inst : function.instructions) {
            if(inst.opCode == OpCode.LABEL) {
                if(instructions.size() > 0) {
                    blocks.add(new BlockContext(labels, instructions));
                    labels.clear();
                    instructions.clear();
                }

                labels.add(((IRLabelOperand) inst.operands[0]).getName());
            } else {
                instructions.add(inst);

                if(inst.isInternalJump() || inst.opCode == OpCode.RETURN) {
                    blocks.add(new BlockContext(labels, instructions));
                    labels.clear();
                    instructions.clear();
                }
            }
        }
        if(instructions.size() > 0 || labels.size() > 0)
            blocks.add(new BlockContext(labels, instructions));
    }

    private void linkBlocks() throws IRException {
        labelLinks = new java.util.HashMap<>();
        for(BlockContext ctx : blocks) {
            for(String str : ctx.labels) {
                if(labelLinks.put(str, ctx) != null)
                    throw new IRException("Duplicate use of label '"+str+"'");
            }
        }

        {
            Iterator<BlockContext> iter = blocks.iterator();
            BlockContext ctx, next = (iter.hasNext() ? iter.next() : null);
            while(next != null) {
                ctx = next;
                next = (iter.hasNext() ? iter.next() : null);

                if(ctx.instructions.isEmpty()) {
                    if(next != null) ctx.branches.add(next);
                    continue;
                }
                IRInstruction last = ctx.instructions.getLast();

                if(last.opCode == OpCode.RETURN) continue;

                if(last.isInternalJump()) {
                    String jmpLabel = ((IRLabelOperand) last.operands[0]).getName();
                    BlockContext jmp = labelLinks.get(jmpLabel);
                    if(jmp == null) throw new IRException("Invalid jump to label '"+jmpLabel+"'", last.irLineNumber);
                    ctx.branches.add(jmp);
                }

                if((last.isBranch() || !last.isInternalJump()) && next != null) {
                    ctx.branches.add(next);
                }
            }
        }

        for(BlockContext ctx : blocks) {
            for(BlockContext branch : ctx.branches) {
                branch.predecessors.add(ctx);
            }
        }
    }

    public void optimize() throws IRException {
        interpretBlocks();
        linkBlocks();

        if(blocks.isEmpty()) return;

        Map<BlockContext, Set<IRInstruction>> criticals = new HashMap<>();
        Queue<FlowWalk> paths = new LinkedList<>();
        for(BlockContext ctx : blocks) {
            criticals.put(ctx, new HashSet<>());

            if(ctx.branches.isEmpty())
                paths.add(new FlowWalk(ctx));
        }

        while(!paths.isEmpty()) {
            FlowWalk walk = paths.remove();
            // System.out.println(walk);
            criticals.get(walk.ctx).addAll(walk.walk());
            paths.addAll(walk.predecessors());

            for(Map.Entry<IRVariableOperand,Set<IRVariableOperand>> e : walk.intersections.entrySet()) {
                if(!intersections.containsKey(e.getKey())) intersections.put(e.getKey(), new java.util.HashSet<>());
                intersections.get(e.getKey()).addAll(e.getValue());
            }
        }

        for(BlockContext ctx : blocks) {
            ctx.instructions.retainAll(criticals.get(ctx));
        }
    }

    public IRFunction getFunction() {
        LinkedList<IRInstruction> ninstr = new LinkedList<>();

        for(BlockContext ctx : blocks) {
            for(String label : ctx.labels) {
                ninstr.add(new IRInstruction(OpCode.LABEL, new IROperand[] { new IRLabelOperand(label, null) }, -1));
            }
            ninstr.addAll(ctx.instructions);
        }

        return new IRFunction(function.name, function.returnType, function.parameters, function.variables, ninstr);
    }
}
