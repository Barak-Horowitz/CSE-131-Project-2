package compiler;

import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;

public class FunctionContext {
    public List<BlockContext> blocks;
    public List<IRInstruction> allInstructions;
    public Map<String, BlockContext> labelLinks;

    private IRFunction function;

    public FunctionContext(IRFunction function) throws IRException {
        this.function = function;

        interpretBlocks();
        linkBlocks();
    }

    private void interpretBlocks() {
        blocks = new java.util.ArrayList<>();

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

    public void linkBlocks() throws IRException {
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
}
