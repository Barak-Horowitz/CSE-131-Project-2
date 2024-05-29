package converter;

import compiler.*;
import ir.*;
import ir.operand.*;
import ir.datatype.*;
import isa.mips.*;
import isa.mips.operand.*;
import java.util.*;

import static isa.mips.MIPSOp.*;
import ir.IRInstruction.OpCode;

import java.io.IOException;

public class MIPSConverter {
    private static final String[] reglist = {
//      "$v0", "$v1", "$a0", "$a1", "$a2", "$a3",
        "$t0", "$t1", "$t2", "$t3", "$t4", "$t5", "$t6", "$t7", "$t8", "$t9"
    };

    public MIPSConverter(FunctionContext fctx, BlockContext ctx, Map<Register, 
            IRVariableOperand> vars, Map<IRVariableOperand, Integer> locs) throws IRException {
        this.fctx = fctx;
        this.ctx = ctx;

        this.vars = new HashMap<>(vars);
        this.locs = new HashMap<>(locs);

        for(IRInstruction inst : ctx.instructions) {
            switch(inst.opCode) {
                case ASSIGN:
                    list.add(new MIPSInstruction(LI, null, getReg((IRVariableOperand) inst.operands[0], false, inst), new Imm(inst.operands[1].toString(), "DEC")));
                    break;
                case GOTO:
                    list.add(new MIPSInstruction(J, null, new Addr(fctx.labelLinks.get(inst.operands[0].toString()).labels.iterator().next() + "_" + fctx.function.name)));
                    break;
                case BREQ:
                case BRNEQ:
                case BRLT:
                case BRGT:
                case BRLEQ:
                case BRGEQ:
                    convertBranchInstruction(inst);
                    break;
                case ARRAY_STORE:
                case ARRAY_LOAD:
                    convertArrayInstruction(inst);
                    break;
                case CALL:
                case CALLR:
                    convertCallInstruction(inst);
                    break;
                case RETURN:
                    returnInstruction();
                    break;
                default: 
                    convertInstruction(inst);
                    break;
            }
        }

        if(ctx.branches.contains(null) || ctx.branches.isEmpty()) {
            returnInstruction();
        }

        if(!ctx.labels.isEmpty() && !list.isEmpty()) {
            list.get(0).label = ctx.labels.iterator().next() + "_" + fctx.function.name;
        }

        // for(MIPSInstruction inst : list) {
        //     System.out.println(inst);
        // }
    }

    // private void resetRegs() {
    //     vars.clear();
    // }

    private FunctionContext fctx;
    private BlockContext ctx;

    public List<MIPSInstruction> list = new LinkedList<>();

    public Map<Register, IRVariableOperand> vars;
    public Map<IRVariableOperand, Integer> locs;

    private static final String[] atregs = { "$at", "$v1", "$v0" };

    private void movReg(Register r, IROperand o) {
        if(o instanceof IRVariableOperand) {
            IRVariableOperand i = (IRVariableOperand) o;
            for(Map.Entry<Register, IRVariableOperand> e : vars.entrySet()) {
                if(i.equals(e.getValue())) {
                    list.add(new MIPSInstruction(MOVE, null, r, e.getKey()));
                    return;
                }
            }

            list.add(new MIPSInstruction(LW, null, r, new Addr(new Imm(locs.get(i).toString(), "DEC"), new Register("$fp"))));
        } else if(o instanceof IRConstantOperand) {
            list.add(new MIPSInstruction(LI, null, r, new Imm(o.toString(), "DEC")));
        }
    }

    private Register getReg(IRVariableOperand i, boolean load, IRInstruction inst) throws IRException {
        for(Map.Entry<Register, IRVariableOperand> e : vars.entrySet()) {
            if(i.equals(e.getValue())) return e.getKey();
        }

        if(!locs.containsKey(i)) {
            if(load) {
                System.out.println(locs);
                System.out.println(inst);
                throw new IRException("load from nonexistent "+i);
            }
            list.add(new MIPSInstruction(ADDI, null, new Register("$sp"), new Register("$sp"), new Imm("-4", "DEC")));
            locs.put(i, -(locs.size() + 1) * 4);
        }

        for(Map.Entry<Register, IRVariableOperand> e : vars.entrySet()) {
            if(e.getValue() == null || !fctx.critMap.get(inst).contains(e.getValue())) {
                Register k = e.getKey();
                vars.put(k, i);
                if(load)
                    list.add(new MIPSInstruction(LW, null, k, new Addr(new Imm(locs.get(i).toString(), "DEC"), new Register("$fp"))));
                return k;
            }
        }

        throw new UnsupportedOperationException("Spilling");
    }

    private void returnInstruction() throws IRException {
        list.add(new MIPSInstruction(ADDI, null, new Register("$sp"), new Register("$sp"), new Imm(String.valueOf(locs.size() * 4), "DEC")));
        list.add(new MIPSInstruction(JR, null, new Register("$ra")));
    }

    private void convertBranchInstruction(IRInstruction inst) throws IRException {
        MIPSOperand[] regs = new MIPSOperand[inst.operands.length];
        regs[2] = new Addr(fctx.labelLinks.get(inst.operands[0].toString()).labels.iterator().next() + "_" + fctx.function.name);

        for(int i = 1, c = 0; i < inst.operands.length; i++) {
            if(inst.operands[i] instanceof IRConstantOperand) {
                regs[i-1] = new Register(atregs[c++]);
                list.add(new MIPSInstruction(LI, null, regs[i], new Imm(inst.operands[i].toString(), "DEC")));
            } else if(inst.operands[i] instanceof IRVariableOperand) {
                regs[i-1] = getReg((IRVariableOperand) inst.operands[i], i > 0, inst);
            } else {
                throw new UnsupportedOperationException();
            }
        }

        MIPSOp op;
        switch(inst.opCode) {
            case BREQ: op = MIPSOp.BEQ; break;
            case BRNEQ: op = MIPSOp.BNE; break;
            case BRLT: op = MIPSOp.BLT; break;
            case BRGT: op = MIPSOp.BGT; break;
            case BRGEQ: op = MIPSOp.BGE; break;
            case BRLEQ: op = MIPSOp.BLE; break;
            default: throw new UnsupportedOperationException();
        }
        list.add(new MIPSInstruction(op, null, regs));

        // System.out.println(new MIPSInstruction(op, null, regs));
    }

    private void convertInstruction(IRInstruction inst) throws IRException {
        MIPSOperand[] regs = new MIPSOperand[inst.operands.length];

        for(int i = 0, c = 0; i < inst.operands.length; i++) {
            if(inst.operands[i] instanceof IRConstantOperand) {
                regs[i] = new Register(atregs[c++]);
                list.add(new MIPSInstruction(LI, null, regs[i], new Imm(inst.operands[i].toString(), "DEC")));
            } else if(inst.operands[i] instanceof IRVariableOperand) {
                regs[i] = getReg((IRVariableOperand) inst.operands[i], i > 0, inst);
            } else {
                throw new UnsupportedOperationException();
            }
        }

        MIPSOp op;
        switch(inst.opCode) {
            case ADD: op = MIPSOp.ADD; break;
            case SUB: op = MIPSOp.SUB; break;
            case MULT: op = MIPSOp.MUL; break;
            case DIV: op = MIPSOp.DIV; break;
            case AND: op = MIPSOp.AND; break;
            case OR: op = MIPSOp.OR; break;
            default: throw new UnsupportedOperationException();
        }

        list.add(new MIPSInstruction(op, null, regs));

        // System.out.println(new MIPSInstruction(op, null, regs));
    }

    private void convertArrayInstruction(IRInstruction inst) throws IRException {
        MIPSOperand value = getReg((IRVariableOperand) inst.operands[0], inst.opCode == OpCode.ARRAY_STORE, inst),
                    array = getReg((IRVariableOperand) inst.operands[1], true, inst);

        if(inst.operands[2] instanceof IRConstantOperand) {
            list.add(new MIPSInstruction(ADDI, null, new Register("$at"), new Addr(new Imm(inst.operands[2].toString(), "DEC"), (Register) array)));
        } else if(inst.operands[2] instanceof IRVariableOperand) {
            list.add(new MIPSInstruction(ADD, null, new Register("$at"), array, getReg((IRVariableOperand) inst.operands[2], true, inst)));
            array = new Register("$at");
        } else {
            throw new UnsupportedOperationException();
        }

        switch(inst.opCode) {
            case ARRAY_LOAD:
                list.add(new MIPSInstruction(LW, null, value, new Register("$at")));
                break;
            case ARRAY_STORE:
                list.add(new MIPSInstruction(SW, null, value, array));
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private static final Map<String, Integer> syscalls = new HashMap<>();
    
    static {
        syscalls.put("puti", 1);
        syscalls.put("putf", 2);
        syscalls.put("putd", 3);
        syscalls.put("geti", 5);
        syscalls.put("getf", 6);
        syscalls.put("getd", 7);
        syscalls.put("sbrk", 9);
        syscalls.put("putc", 11);
    }

    private void convertCallInstruction(IRInstruction inst) throws IRException {
        MIPSOperand value;

        for(int i = (inst.opCode == OpCode.CALLR) ? 2 : 1, j = 0; i < inst.operands.length; i++, j++) {
            movReg(new Register("$a"+j), inst.operands[i]);
        }

        list.add(new MIPSInstruction(SW, null, new Register("$fp"), new Addr(new Register("$sp"))));
        list.add(new MIPSInstruction(ADDI, null, new Register("$sp"), new Register("$sp"), new Imm("-4", "DEC")));
        list.add(new MIPSInstruction(MOVE, null, new Register("$fp"), new Register("$sp")));

        String label = inst.operands[(inst.opCode == OpCode.CALLR) ? 1 : 0].toString();
        if(syscalls.containsKey(label)) {
            list.add(new MIPSInstruction(LI, null, new Register("$v0"), new Imm(String.valueOf(syscalls.get(label)), "DEC")));
            list.add(new MIPSInstruction(SYSCALL, null));
        } else {
            list.add(new MIPSInstruction(JAL, null, new Addr(label + "_" + fctx.function.name)));
        }

        list.add(new MIPSInstruction(ADDI, null, new Register("$sp"), new Register("$sp"), new Imm("4", "DEC")));
        list.add(new MIPSInstruction(LW, null, new Register("$fp"), new Addr(new Register("$sp"))));

        for(String s : reglist) {
            vars.put(new Register(s), null); // TODO SYSCALL
        }
        
        if(inst.opCode == OpCode.CALLR) {
            value = getReg((IRVariableOperand) inst.operands[0], false, inst);
            list.add(new MIPSInstruction(MOVE, null, value, new Register("$v0")));
        }
    }

    public static void main(String[] args) throws IOException, IRException {
        IRProgram program = new IRReader().parseIRFile(args[0]);

        System.out.println(".text");
        for(IRFunction fnc : program.functions) {
            FunctionContext fctx = new FunctionContext(fnc);
            fctx.optimize();

            List<MIPSInstruction> list = new LinkedList<>();

            Map<Register, IRVariableOperand> regs = new HashMap<>();
            Map<IRVariableOperand, Integer> stack = new HashMap<>();
            for(String r : reglist) {
                regs.put(new Register(r), null);
            }
            for(IRVariableOperand r : fnc.parameters) {
                regs.put(new Register("$a"+stack.size()), r);
                stack.put(r, -(stack.size() + 1) * 4);
            }
            for(IRVariableOperand r : fnc.variables) {
                if(fnc.parameters.contains(r)) continue;

                if(r.type instanceof IRArrayType) {
                    stack.put(r, -(stack.size() + 1) * 4);
                    list.add(new MIPSInstruction(LI, null, new Register("$v0"), new Imm("9", "DEC")));
                    list.add(new MIPSInstruction(LI, null, new Register("$a0"), new Imm(String.valueOf(((IRArrayType) r.type).getSize()), "DEC")));
                    list.add(new MIPSInstruction(SYSCALL, null));
                    regs.put(new Register("$t"+regs.size()), r);
                }
            }
            list.add(new MIPSInstruction(ADDI, null, new Register("$sp"), new Register("$sp"), new Imm(String.valueOf(-stack.size() * 4), "DEC")));

            Map<BlockContext, Integer> visits = new HashMap<>();
            Map<BlockContext, List<MIPSInstruction>> converted = new HashMap<>();
            Queue<MIPSConverter> queue = new LinkedList<>();
            for(BlockContext c : fctx.blocks) {
                if(c.predecessors.isEmpty()) queue.add(new MIPSConverter(fctx, c, regs, stack));
            }

            while(!queue.isEmpty()) {
                MIPSConverter c = queue.remove();
                visits.put(c.ctx, visits.getOrDefault(c.ctx, 0) + 1);
                converted.put(c.ctx, c.list);

                for(BlockContext s : c.ctx.branches) {
                    if(s == null) continue;
                    if(visits.getOrDefault(s, 0) < 2) {
                        queue.add(new MIPSConverter(fctx, s, c.vars, c.locs));
                    }
                }
            }

            list.get(0).label = fnc.name;
            for(MIPSInstruction i : list) {
                System.out.println(i);
            }
            for(BlockContext ctx : fctx.blocks) {
                for(MIPSInstruction i : converted.get(ctx)) {
                    System.out.println(i);
                }
            }
        }
    }
}
