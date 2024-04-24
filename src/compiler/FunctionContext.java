package compiler;

import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;

public class FunctionContext {
    public List<BlockContext> blocks;

    public FunctionContext(IRFunction fnc) {
        blocks = new java.util.ArrayList<>();

        List<IRInstruction> instructions = new java.util.ArrayList<>();
        Collection<String> labels = new java.util.ArrayList<>();
        labels.add(fnc.name);

        for(IRInstruction inst : fnc.instructions) {
            if(inst.opCode == OpCode.LABEL) {
                if(instructions.size() > 0) {
                    blocks.add(new BlockContext(labels, instructions));
                    labels.clear();
                    instructions.clear();
                }

                labels.add(((IRLabelOperand) inst.operands[0]).getName());
            } else {
                instructions.add(inst);

                if(inst.isLogicalJump()) {
                    blocks.add(new BlockContext(labels, instructions));
                    labels.clear();
                    instructions.clear();
                }
            }
        }

        if(instructions.size() > 0 || labels.size() > 0)
            blocks.add(new BlockContext(labels, instructions));
    }
}
