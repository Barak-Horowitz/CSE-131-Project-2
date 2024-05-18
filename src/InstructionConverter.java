import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;
import java.io.*;
import mips.*;


public class InstructionConverter {
    private InstructionCreator mipsCreator;

    private final String ARGSREG = "4";
    private final String RETURNREG = "2";
    private final int numPrivateRegisters = 8;

    // stores which variables have been allocated inside of a program
    private ArrayList<HashMap<String, Register>> regMap;

    // stores which variables have already been allocated inside of a function
    private HashMap<String, Register> funcMap;

    private int largestRegVal;

    

    public InstructionConverter {
        mipsCreator = new InstructionCreator();
        regMap = new ArrayList<>();
        funcMap = new HashMap<>();
        largestRegVal = numPrivateRegisters; // first numPrivateRegisters are not available to user
    }

    public List<MIPSInstruction> convertInstruction(IRInstruction instruction) {  
        // EDGE CASE: if instruction is an IR function call handle seperatly 
        if(IRFunctionCall(instruction)) {
            return convertIRFunctionCall(instruction);
        }

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
                    Register sourceReg = (funcMap.get(instruction.operands[i].toString()));
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
                    Register sourceReg = funcMap.get(instruction.operands[i].toString());
                    // Argument registers do NOT need to be tracked - always assumed free!
                    Register destReg = new Register(i - 2 + ARGSREG);
                    returnList.addAll(mipsCreator.createMove(destReg, sourceReg));
                } // jump after loading arguments
                String label = instruction.operands[1].getValue();
                Addr labelAddress = new Addr(label);
                returnList.addAll(mipsCreator.createLinkedJump(labelAddress)); 
                Register destReg = variableToRegister(instruction.operands[0]);
                // return registers do NOT need to be tracked  always assumed free!
                register sourceReg = new Register(RETURNREG);
                returnList.addAll(mipsCreator.createMove(destReg, sourceReg));
                return returnList;
            
            case LABEL:
                String labelName = instruction.operands[0].getValue()
                return mipsCreator.createLabel(labelName);

            case RETURN: 
                Register sourceReg = funcMap.get(instruction.operands[0].toString());
                // return registers do NOT need to be tracked - always assumed to be free!
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
        Register sourceRegOne = funcMap.get(instructions.operands[1].toString());
        Register sourceRegTwo = funcMap.get(instructions.operands[2].toString());
        
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
        Register destReg = variableToRegister(instruction.operands[0]);
        Register sourceRegOne = funcMap.get(instruction.operands[1].toString());
        Register sourceRegTwo = funcMap.get(instruction.operands[2].toString());
        // if only two operands exist instruction must be an assign 
        if(sourceRegTwo == null) { 
            return mipsCreator.createMove(destReg, sourceRegOne);

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
        Imm valOne = null;
        Imm valTwo = null;
        Register destReg = variableToRegister(instruction.operands[0]);
        Register zeroReg = new Register(0);
        Register regOne = funcMap.get(instruction.operands[1].toString());
        Register regTwo = funcMap.get(instruction.operands[2].toString());
        if(regOne == null) {
            Imm valOne = new Imm("DEC", Integer.parseInt(instruction.operands[1].toString()));
        } 
        if(regTwo == null) {
            imm valTwo = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()));
        }

        switch(instruction.opCode) {
            
            case ADD: 
                if(regOne == null && regTwo == null) {
                    return mipsCreator.createAdd(destReg, valOne, valTwo);
                } else if (regTwo == null) { 
                    return mipsCreator.createAdd(destReg, regOne, valTwo);
                } else {
                    return mipsCreator.createAdd(destReg, regTwo, valOne);
                }

            case SUB:
                if(regOne == null && regTwo == null) {
                    valTwo = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()) * - 1);
                    return mipsCreator.createSub(destReg, valOne, valTwo);
                }
                else if (regTwo == null) { 
                    valTwo = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()) * - 1);
                    return mipsCreator.createSub(destReg, regOne, valTwo);
                }
                else {
                    return mipsCreator.createSub(destReg, valOne, regTwo);
                }

            case MULT:
                if(regOne == null && regTwo == null) {
                    // need two registers to make an immediate multiplication
                    return mipsCreator.createMult(destReg, tempReg valOne, valTwo);
                } else if (regTwo == null) {
                    return mipsCreator.createMult(destReg, regOne, valTwo);
                } else {
                    return mipsCreator.createMove(destReg, regTwo, valOne);
                }
            case DIV:
                if(regOne == null && regTwo == null) {
                    return mipsCreator.createMult(destReg, valOne, valTwo);
                } else if (regTwo == null) {
                    return mipsCreator.createMult(destReg, regOne, valTwo);
                } else {
                    return mipsCreator.createMove(destReg, valOne, regTwo);
                }

            case AND:
                if(regOne == null && regTwo == null) {
                    return mipsCreator.createAnd(destReg, valOne, valTwo);
                } else if (regTwo == null) { // else just make sure to put immediate last
                    return mipsCreator.createAnd(destReg, regOne, valTwo);
                } else {
                    return mipsCreator.createAnd(destReg, regTwo, valOne);
                }

            case OR:
                if(regOne == null && regTwo == null) {
                    return mipsCreator.createOr(destReg, valOne, valTwo);
                } else if (regTwo == null) { 
                    return mipsCreator.createOr(destReg, regOne, valTwo);
                } else {
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

    private boolean IRFunctionCall(IRInstruction instruction) {
        switch(instruction.opCode) {
            case CALL:
                switch(instruction.operands[0].getName()) {
                    case "puti":
                    case "putf":
                    case "putc":
                        return true;
                    default:
                        return false;
                }

            case CALLR: switch(instruction.operands[1].getName()) {
                case "geti":
                case "getf":
                case "getc":
                    return true;
                default:
                    return false;
            }

            default: return false;
        }

    }

    // if instruction has any numbers in its operands instead of variables it is an i-type instruction
    private boolean iType (IRInstruction instruction) {
        
        for(int i = 0; i < instruction.operands.size; i++) {
            if(instruction.operands[i] instanceof IRConstantOperand && instruction.operands[i].type instanceof IRIntType
                && Integer.parseInt(instruction.operands[i].getValueString()) <= 0xFFFF && Integer.parseInt(instruction.operands[i].getValueString() > (0xFFFF * -1))) {
                return true;
            }
            // TODO: handle case where immediate is smaller then - 1 * 0xFFFF or larger then 0xFFFF
        }
        return false;

    }

    // method should be called after every function to wipe values from function mapping!
    public void clearFuncMap() {
        // add funcMap values to regMap
        HashMap<String, Integer> hardCopy = new HashMap<>();
        // Iterate over the original HashMap and copy each entry to the new HashMap
        for (Map.Entry<Integer, String> entry : funcMap.entrySet()) {
            hardCopy.put(entry.getKey(), entry.getValue());
        }
        regMap.add(hardCopy);
        
        // clear funcMap so it can be used for the next function
        funcMap.clear();
    }


    // method should ONLY be used on destination registers which can either already be in funcMap or need to be allocated,
    // ALL SOURCE REGISTERS SHOULD ALREADY BE IN funcMap!
    private Register variableToRegister(IROperand operand) {
        Register returnReg;
        if(funcMap.get(operand.toString()) == null) {
            returnReg = new Register(getEmptyRegister);
            funcMap.put(operand.toString(), returnReg);
        } else {
            returnReg = funcMap.get(operand.toString());
        }
        return returnReg;
    }

    private String getEmptyRegister() {
        String returnString = largestRegVal + ""
        largestRegVal ++;
        return returnString;
    }
}