package converter;

import compiler.*;
import ir.*;
import isa.mips.*;

import java.util.Map;
import java.util.List;
import java.util.function.Consumer;

public class MIPSConverter extends ISAConverter {
    @Override
    public synchronized void convert(IRProgram prog) {
        registerMapping = new java.util.HashMap<>();
        try {
            for(IRFunction fnc : prog.functions) {
                FunctionContext ctx = new FunctionContext(fnc);
                ctx.optimize();
            }
        } catch(IRException e) {
            throw new RuntimeException(e);
        }
    }

    private final Map<IRInstruction.OpCode, Consumer<IRInstruction>> converter = new java.util.HashMap<>();
    public MIPSConverter() {
        converter.put(IRInstruction.OpCode.ASSIGN, this::assign);
    }

    private Map<String,Object> registerMapping;

    private String getMapped(Object o) {
        for(Map.Entry<String,Object> e : registerMapping.entrySet()) {
            if(o.equals(e.getValue())) return e.getKey();
        }
        return null;
    }

    private static final String[] regs = { "$0", "$at", "$v0", "$v1", "$a0", "$a1", "$a2", "$a3",
        "$t0", "$t1", "$t2", "$t3", "$t4", "$t5", "$t6", "$t7",
        "$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7", "$t8", "$t9", "$k0", "$k1",
        "$gp", "$sp", "$fp", "$ra" };

    private static String allocateIRegister(Object value) {
        return null;
    }

    private static String allocateFPRegister(Object value) {
        return null;
    }

    private void assign(IRInstruction inst) {
        
    }
}
