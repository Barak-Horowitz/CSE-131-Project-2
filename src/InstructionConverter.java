import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;
import java.io.*;
import mips.*;


public class InstructionConverter {
    private InstructionCreator mipsCreator;

    private final ARGSREG = 4;
    private final RETURNREG = 2;

    // TODO: ADD REGMAP DATA STRUCTURE TO MAP REGISTERS TO VARIABLES!
    

    public InstructionConverter {
        mipsCreator = new InstructionCreator();
    }

    public List<MIPSInstruction> convertInstruction(IRInstruction instruction) {  
        
        // EDGE CASE: if operation manipulates memory handle seperatly
        switch(instruction.opCode) {
            case ARRAY_STORE:
            case ARRAY_LOAD:
                return convertDataType(instruction)
        }

        if(jType(instruction)) {
            return convertJType(instruction);
        }
        if(iType(instruction)) {
            return convertIType(instruction)
        } // if not i or J type then MUST be R type
        return convertRType(instruction)

    }

    // TODO: write method to convert array operations from tiger to MIPS
    private List<MIPSInstruction> convertDataType(instruction) {
        return null;
    }

    private List<MIPSInstruction> convertJType(IRInstruction instruction) {
        List<MIPSInstruction> returnList = new List<>();
        switch(instruction.opCode) {
            case GOTO:
                String label = instruction.operands[0].getValue();
                Addr labelAddress = new Addr(label);
                return mipsCreator.createJump(labelAddress);
            
            case CALL:
                // load arguments into argument registers
                for(int i = 1; i < instruction.operands.size; i++) {
                    // TODO: - ADD CHECK FOR IF ARGUMENT IS IMMEDIATE OR REGISTER!
                    Register sourceReg = new Register(regmap.get(instruction.operands[i]));
                    Register destReg = new Register(i - 1 + ARGSREG);
                    returnList.addAll(mipsCreator.createMove(destReg, sourceReg));
                } // jump after loading arguments
                String label = instruction.operands[0].getValue();
                Addr labelAddress = new Addr(label);
                returnList.addAll(mipsCreator.createLinkedJump(labelAddress));
                return returnList;
            
            case CALLR:
                // load arguments into argument registers
                for(int i = 2; i < instruction.operands.size; i++) {
                    // TODO: - ADD CHECK FOR IF ARGUMENT IS IMMEDIATE OR REGISTER!
                    Register sourceReg = new Register(regmap.get(instruction.operands[i]));
                    Register destReg = new Register(i - 2 + ARGSREG);
                    returnList.addAll(mipsCreator.createMove(destReg, sourceReg));
                } // jump after loading arguments
                String label = instruction.operands[1].getValue();
                Addr labelAddress = new Addr(label);
                returnList.addAll(mipsCreator.createLinkedJump(labelAddress));
                // move returned value into specified register
                Register destReg = new Register(regMap.get(instruction.operands[0]));
                register sourceReg = new Register(RETURNREG);
                returnList.addAll(mipsCreator.createMove(destReg, sourceReg));
                return returnList;
            
            case LABEL:
                String labelName = instruction.operands[0].getValue()
                return mipsCreator.createLabel(labelName);
            case RETURN: 
                Register sourceReg = regMap.get(instruction.operands[0]);
                Register destReg = new Register(RETURNREG);
                return mipsCreator.createReturn(destReg, sourceReg);
            
            // if none of these cases are hit instruction must be a conditional branch
            default:
                return convertConditionalBranch(instruction);
        
        }
    }
    
    private List<MIPSInstruction> convertConditionalBranch(IRInstruction instruction) {
        String label = instruction.operands[0].getValue();
        Addr labelAddress = new Addr(label);
        Register sourceRegOne = new Register(regmap.get(instructions.operands[1]));
        Register sourceRegTwo = new Register(regmap.get(instructions.operands[2]));
        
        switch(instruction.opCode) {
            case BRNEQ:
                return mipsCreator.createBrneq(labelName, sourceRegOne, sourceRegTwo);
        
            case BREQ:
                return mipsCreator.createBreq(labelName, sourceRegOne, sourceRegTwo);

        
            case BRLT:
                return mipsCreator.createBrlt(labelName, sourceRegOne, sourceRegTwo);
        
            case BRGT:
                return mipsCreator.createBrgt(labelName, sourceRegOne, sourceRegTwo);

            case BRGEQ:
                return mipsCreator.createBrgeq(labelName, sourceRegOne, sourceRegTwo);
            
            case BRLEQ:
                returnmipsCreator.createBrleq(labelName, sourceRegOne, sourceRegTwo);
        }

        System.out.println("ERRROR SHOULD NEVER BE HIT");
        return null;
    }
            
            
    private List<MIPSInstruction> convertRType(IRInstruction instruction) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        Integer destReg = regMap.get(instruction.operands[0]);
        Integer sourceRegOne = regMap.get(instruction.operands[1]);
        Integer sourceRegTwo = regMap.get(instruction.operands[2]);

        // if only two operands exist instruction must be an assign 
        if(sourceRegTwo == null) { 
            returnList.add(mipsCreator.createMove(destReg, sourceRegOne));

        }

        switch(instruction.opCode) {
            
            case ADD:
                return mipsCreator.createAdd(destReg, sourceRegOne, sourceRegTwo);

            case SUB:
                return mipsCreator.createSub(destReg, sourceRegOne, sourceRegTwo);

            case MULT:
                return mipsCreator.createMult(destReg, sourceRegOne, sourceRegTwo);

            case DIV:
                return mipsCreator.createDiv(destReg, sourceRegOne, sourceRegTwo);

            case AND:
                return mipsCreator.createAnd(destReg, sourceRegOne, sourceRegTwo);
            
            case OR:
                return mipsCreator.createOr(destReg, sourceRegOne, sourceRegTwo);
        }
    }

    private List<MIPSInstruction convertIType(IRInstruction instruction) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        Register destReg = new Register(regmap.get(instruction.operands[0]));
        Register zeroReg = new Register(0);
        Integer sourceOne = regmap.get(instruction.operands[1]);
        Integer sourceTwo = regmap.get(instruction.operands[2]);

        switch(instruction.opCode) {
            
            case ADD: 
                if(sourceOne == null && sourceTwo == null) {
                    Imm valOne = new Imm("DEC", Integer.parseInt(instruction.operands[1].toString()));
                    imm valTwo = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()));
                    return mipsCreator.createAdd(destReg, valOne, valTwo);
                } else if (sourceTwo == null) { 
                    Register regOne = new Register(sourceOne);
                    Imm valTwo = new Imm("DEC", Integer.parseInt(instruction.operands[1].toString()));
                    return mipsCreator.createAdd(destReg, regOne, valTwo);
                } else {
                    Imm valOne = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()));
                    Register regTwo = new Register(sourceTwo);
                    return mipsCreator.createAdd(destReg, regTwo, valOne);
                }

            case SUB:
                if(sourceOne == null && sourceTwo == null) {
                    Imm valOne = new Imm("DEC", Integer.parseInt(instruction.operands[1].toString()));
                    Imm valTwo = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()) * - 1);
                    return mipsCreator.createSub(destReg, valOne, valTwo);
                }
                else if (sourceTwo == null) { 
                    Register regOne = new Register(sourceOne);
                    Imm valTwo = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()) * - 1);
                    return mipsCreator.createSub(destReg, regOne, valTwo);
                }
                else {
                    Imm valOne = new Imm("DEC", Integer.parseInt(instruction.operands[1].toString()));
                    Register regTwo = new Register(sourceTwo);
                    return mipsCreator.createSub(destReg, valOne, regTwo);
                }

            // TODO: figure out if MULT or DIV accept immediate values
            case MULT:

            case DIV:

            case AND:
                if(sourceOne == null && sourceTwo == null) {
                    Imm valOne = new Imm("DEC", Integer.parseInt(instruction.operands[1].toString()));
                    Imm valTwo = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()));
                    return mipsCreator.createAnd(destReg, valOne, valTwo);
                } else if (sourceTwo == null) { // else just make sure to put immediate last
                    Register regOne = new Register(sourceOne);
                    Imm valTwo = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()));
                    return mipsCreator.createAnd(destReg, regOne, valTwo);
                } else {
                    Imm valOne = new Imm("DEC", Integer.parseInt(instruction.operands[1].toString()));
                    Register regTwo = new Register(sourceTwo);
                    return mipsCreator.createAnd(destReg, regTwo, valOne);
                }

            case OR:
                if(sourceOne == null && sourceTwo == null) {
                    Imm valOne = new Imm("DEC", Integer.parseInt(instruction.operands[1].toString()));
                    Imm valTwo = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()));
                    return mipsCreator.createOr(destReg, valOne, valTwo);
                } else if (sourceTwo == null) { 
                    Register regOne = new Register(sourceOne);
                    Imm valTwo = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()));
                    return mipsCreator.createOr(destReg, regOne, valTwo);
                } else {
                    Imm valOne = new Imm("DEC", Integer.parseInt(instruction.operands[1].toString()));
                    Register regTwo = new Register(sourceTwo);
                    return mipsCreator.createOr(destReg, regTwo, valOne);
                } 
        }
    }

    private boolean jType (IRInstruction instruction) {
        
        switch(instruction.opCode) {
            case(LABEL): 
            case(GOTO):
            case(BREQ):
            case(BRLT):
            case(BRGT):
            case(BRLEQ):
            case(BRGEQ):
            case(RETURN):
            case(CALL):
            case(CALLR):
                return true;
            default:
                return false;
        }

    }

    // if instruction has any numbers in its operands instead of variables it is an i-type instruction
    private boolean iType (IRInstruction instruction) {
        
        for(int i = 0; i < instruction.operands.size; i++) {
            if(instruction.operands[i] instanceof IRConstantOperand && instruction.operands[i].type instanceof IRIntType
                && Integer.parseInt(instruction.operands[i].getValueString()) <= 0xFFFF && Integer.parseInt(instruction.operands[i].getValueString() > (0xFFFF * -1))) {
                return true;
            }
            // TODO: handle case where immediate is an array
            // TODO: handle case where immediate is a float 
            // TODO: handle case where immediate is smaller then - 1 * 0xFFFF or larger then 0xFFFF
        }
        return false;

    }
}