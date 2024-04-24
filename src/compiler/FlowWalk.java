package compiler;

import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;

public class FlowWalk {
    private Map<BlockContext, Integer> visits;
    private Set<IRInstruction> criticalInstructions;
    private Set<IRVariableOperand> criticalVars;
    public final BlockContext ctx;

    public FlowWalk(FunctionContext ctx) {
        this(ctx.blocks.getLast(), null);
    }

    private FlowWalk(BlockContext ctx, FlowWalk prev) {
        visits = new HashMap<>();
        criticalVars = new HashSet<>();
        if(prev != null) {
            visits.putAll(prev.visits);
            criticalVars.addAll(prev.criticalVars);
        }
        visits.put(ctx, visits.getOrDefault(ctx, 0) + 1);
        this.ctx = ctx;
    }

    public Set<IRInstruction> walk() {
        if(criticalInstructions != null) throw new IllegalStateException("Already walked");

        criticalInstructions = new HashSet<>();
        for(Iterator<IRInstruction> iter = ctx.instructions.descendingIterator(); iter.hasNext();) {
            IRInstruction inst = iter.next();

            boolean critical = inst.isJump();
            for(int i = 0; i < inst.operands.length; i++) {

                if(inst.operands[i] instanceof IRVariableOperand) {
                    IRVariableOperand var = (IRVariableOperand) inst.operands[i];

                    if((i == 0 && inst.isWriteToVar()) || (i == 1 && inst.isWriteToArray())) {
                        critical = critical || criticalVars.remove(var);
                        // System.out.println("WRITE "+critical+" "+inst);
                    } else {
                        if(critical) criticalVars.add(var);
                    }
                }
            }
            if(critical) {
                criticalInstructions.add(inst);
            }
        }
        return criticalInstructions;
    }

    public Collection<FlowWalk> predecessors() {
        if(criticalInstructions == null) throw new IllegalStateException("No walk occurred");

        Collection<FlowWalk> list = new ArrayList<>();
        for(BlockContext pre : ctx.predecessors) {
            if(visits.getOrDefault(pre, 0) < 2)
                list.add(new FlowWalk(pre, this));
        }
        return list;
    }

    @Override
    public String toString() {
        return ctx.toString();
    }
}
