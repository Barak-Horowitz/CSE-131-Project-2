package converter;

import compiler.*;
import ir.*;
import isa.mips.*;
import java.util.*;

public class MIPSConverter {
    private static final String[] caller_saved_regs = {
//      "v0", "v1", "a0", "a1", "a2", "a3",
        "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9"
    };

    private static final String[] callee_saved_regs = {
        "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7"
    };

    public static List<MIPSInstruction> convert(List<MIPSInstruction> list, Map<MIPSInstruction, Set<Register>> criticals) {
        Map<Register, Register> registerAllocations = new HashMap<>();
        List<MIPSInstruction> out = new ArrayList<MIPSInstruction>();

        for(Iterator<MIPSInstruction> inst : list) {
            Set<Register> current_crits = new HashSet<>(criticals.get(inst));
            if(current_crits.size() > caller_saved_regs.length) {
                throw RuntimeException("HELP"); // TODO implement spills
            }

            for(int i = 0; i < inst.operands.size(); i++) {
                if(!(inst.operands.get(i) instanceof Register)) continue;
                Register reg = (Register) inst.operands.get(i);

                for(Map.Entry<Register,Register> e : registerAllocations.entrySet()) {
                    if(reg.equals(e.getValue())) {
                        inst.operands.set(0, e.getKey());
                        break;
                    }
                }
            }

            for(Register val : current_crits.removeAll(registerAllocations.valueSet())) {
                for(int i = 0; i < caller_saved_regs.length; i++) {
                    if(registerAllocations.get(caller_saved_regs[i]) == null) {
                        registerAllocations.put(caller_saved_regs[i], val);
                        break;
                    }
                }
            }

            for(int i = 0; i < inst.operands.size(); i++) {
                if(!(inst.operands.get(i) instanceof Register)) continue;
                Register reg = (Register) inst.operands.get(i);

                for(Map.Entry<Register,Register> e : registerAllocations.entrySet()) {
                    if(reg.equals(e.getValue())) {
                        inst.operands.set(0, e.getKey());
                        break;
                    }
                }
            }
        }

        return out;
    }
}
