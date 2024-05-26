import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;
import java.io.*;
import mips.*;


public class InstructionConverter {
    private InstructionCreator mipsCreator;

    private final String ARGSREG = "a";
    private final String TEMPREG = "t"
    private final Register ZEROREG = new Register("0");
    private final Register RETURNREG = new Register ("v0");
    private int largestRegVal;


    // stores which variables have been allocated inside of a program
    private ArrayList<HashMap<String, Register>> regMap;

    private ArrayList<HashMap<String, Integer> arrayMap;

    // stores which variables have already been allocated inside of a function
    private HashMap<String, Register> funcMap;

    private int largestRegVal;
    
    private InstructionCreator mipsCreator;

    

    public InstructionConverter {
        mipsCreator = new InstructionCreator();
        regMap = new ArrayList<>();
        funcMap = new HashMap<>();
        largestRegVal = 0;
    }


    // given the header for a function, allocate space for all of its variables, and 
    // move perameters out of argument registers 
    public List<MIPSInstruction> convertHeader(IRFunction function) {
        List<MIPSInstruction> returnList = convertArguments(function)
        returnList.addAll(allocateVariables(function));
        return returnList;

    }

    public List<MIPSInstruction> convertArguments(IRFunction function) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        for(int arg_num = 0; arg_num < function.parameters.size(); arg_num++) {
            Register sourceReg = new Register(ARGSREG + arg_num);
            Register destReg = variableToRegister(function.parameters.get(arg_num));
            returnList.addAll(mipsCreator.createMove(destReg, sourceReg));
        }
        return returnList;
    }

    public void allocateVariables(IRFunction function) {
        List<MIPSInstruction> returnList = new LinkedList<>(); 
        for(int var_num = 0; var_num < function.variables.size(); i++) {
            // EDGE CASE: arrays can't be stored as registers, need to map into heap 
            if(function.variables.get(var_num).type instanceof IRArrayType) {
                Register arrayPtrReg = variableToRegister(function.variables.get(var_num));
                Imm arraySizeImm = new Imm("DEC", function.variables.get(var_num).getSize());
                returnList.add(mipsCreator.createArray(arraySizeImm, arrayPtrReg));
                
            }
            variableToRegister(function.variables.get(var_num));
        }
    }

    public List<MIPSInstruction> convertInstruction(IRInstruction instruction) {  
        // EDGE CASE: if instruction is an IR function call handle seperatly 
        if(IRFunctionCall(instruction)) {
            return convertIRFunctionCall(instruction);
        }

        // EDGE CASE: if operation manipulates memory handle seperatly
        if(dataOperation(instruction)) {
            return convertDataType(instruction);
        }


        if(jType(instruction)) {
            return convertJType(instruction);
        }
        if(iType(instruction)) {
            return convertIType(instruction);
        } // if not i or J type then MUST be R type
        return convertRType(instruction);

    }


    // TODO: WRITE METHOD TO CONVERT INTRINSIC IR FUNCTION CALLS
    private List<MIPSInstruction> convertIRFunctionCall(IRInstruction instruction) {
        switch(instruction.opCode) {
            case CALL:
                Register printReg = funcMap.get(instruction.operands[1].toString());
                Imm printVal = null;
                if(printReg == null) {
                    printVal = createImmediate(instruction.operands[1]);
                }
                switch(instruction.operands[0].getName()) {
                    case "puti":
                        if(printReg == null) {
                            return mipsCreator.createPUTI(printVal);
                        }
                        return mipsCreator.createPUTI(printReg);
                    
                    case "putf":
                        if(printReg == null) {
                            return mipsCreator.createPUTF(printVal);
                        }
                        return mipsCreator.PUTF(printReg);
                    case "putc":
                        if(printReg == null) {
                            return mipsCreator.createPUTC(printVal);
                        }
                        return mipsCreator.createPUTC(printReg);
                }

            case CALLR: switch(instruction.operands[1].getName()) {
                Register returnReg = variableToRegister(instruction.operands[0]);
                case "geti":
                    return createGETI(returnReg);
                case "getf":
                    return createGETF(returnReg);
                case "getc":
                    return createGETC(returnReg);

            }

    }

    private List<MIPSInstruction> convertDataType(IRInstruction instruction) {
        Register arrayStoreReg = variableToRegister(instruction.operands[0]);
        Register arrayPtrReg = variableToRegister(instruction.operands[1]);
        Register offsetReg = funcMap.get(instruction.operands[2].toString());
        switch(instruction.opCode) {
            // if assign allocate space for array on heap
            case ASSIGN:
                System.out.println("ERROR: ARRAY ASSIGN UNEXPECTED");
                System.exit(0);

            // if load grab array value from memory 
            case ARRAY_LOAD:
                // if immediate grab immediate 
                if(offset == null) {
                    Imm offsetImm = createImmediate(instruction.operands[2]);
                    return InstructionCreator.createArrayLoad(arrayStoreReg, arrayPtrReg, offsetImm);
                }
                return InstructionCreator.createArrayLoad(arrayStoreReg, arrayPtrReg, offsetReg);

            // if store, store reg value in array
            case ARRAY_STORE:
                // if immediate grab immediate
                if(offset == null) {
                    Imm offsetImm = createImmediate(instruction.operands[2]);
                    return InstructionCreator.createArrayStore(arrayStoreReg, arrayPtrReg, offsetImm);
                }
                return InstructionConverter.createArrayStore(arrayStoreReg, arrayPtrReg, offsetImm);
        }
        
    }

    private List<MIPSInstruction> convertJType(IRInstruction instruction) {
        List<MIPSInstruction> returnList = new LinkedList<>();
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
                    Register destReg = new Register(ARGSREG + (i - 1));
                    if(sourceReg == null) {
                        Imm sourceImm = createImmediate(instruction.operands[i]);
                        returnList.addAll(mipsCreator.createAdd(destReg, ZEROREG, sourceImm));
                    } else {
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
                    Register destReg = new Register(ARGSREG + (i - 2));
                    if(sourceReg == null) {
                        Imm sourceImm = createImmediate(instruction.operands[i]);
                        returnList.addAll(mipsCreator.createAdd(destReg, zeroReg, sourceImm));
                    } else {
                        returnList.addAll(mipsCreator.createMove(destReg, sourceReg));
                    }
                } // jump after loading arguments
                String label = instruction.operands[1].getValue();
                Addr labelAddress = new Addr(label);
                returnList.addAll(mipsCreator.createLinkedJump(labelAddress)); 
                Register destReg = variableToRegister(instruction.operands[0]);
                register sourceReg = new Register(RETURNREG);
                returnList.addAll(mipsCreator.createMove(destReg, sourceReg));
                return returnList;
            
            case LABEL:
                String labelName = instruction.operands[0].getValue()
                return mipsCreator.createLabel(labelName);

            case RETURN: 
                Register sourceReg = variableToRegister(instruction.operands[0]);
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
        Imm immValOne = null;
        Imm immValTwo = null;
        if(sourceRegOne == null) {
            immValOne = createImmediate(instruction.operands[1]);
        }
        if(sourceRegTwo == null) {
            immValTwo = createImmediate(instruction.operands[2])
        }
        
        // TODO: HANDLE CASES WHERE CONDITIONAL BRANCHES CONTAIN IMMEDIATE VALUES
        switch(instruction.opCode) {
            case BRNEQ:
                if(sourceRegOne == null && sourceRegTwo == null) {
                    return mipsCreator.createBRNEQ(labelName, immValOne, immvalTwo)

                }
                if(sourceRegOne == null) {
                    return mipsCreator.createBRNEQ(labelName, immValOne, sourceRegTwo);
                }
                if(sourceRegTwo == null) {
                    return mipsCreator.createBRNEQ(labelName, immValTwo, sourceRegOne);
                }
                return mipsCreator.createBrneq(labelName, sourceRegOne, sourceRegTwo);
        
            case BREQ:
                if(sourceRegOne == null && sourceRegTwo == null) {
                    return mipsCreator.createBREQ(labelName, immValOne, immvalTwo);
                }   
                if(sourceRegOne == null) {
                    return mipsCreator.createBREQ(labelName, immValOne, sourceRegTwo);
                }
                if(sourceRegTwo == null) {
                    return mipsCreator.createBREQ(labelName,immValTwo, sourceRegOne);
                }
                return mipsCreator.createBreq(labelName, sourceRegOne, sourceRegTwo);
        
            case BRLT:
                if(sourceRegOne == null && sourceRegTwo == null) {
                    return mipsCreator.createBRLT(labelName, immValOne, immValTwo);
                }
                if(sourceRegOne == null) {
                    return mipsCreator.createBRLT(labelName, immValOne, sourceRegTwo);
                }   
                if(sourceRegTwo == null) {
                    return mipsCreator.createBRLT(labelName, sourceRegOne, immValTwo);
                }
                return mipsCreator.createBRLT(labelName, sourceRegOne, sourceRegTwo);
        
            case BRGT:
                if(sourceRegOne == null && sourceRegTwo == null) {
                    return mipsCreator.createBRGT(labelName, immValOne, immValTwo);

                }
                if(sourceRegOne == null) {
                    return mipsCreator.createBRGT(labelName, immValOne, sourceRegTwo);
                }
                if(sourceRegTwo == null) {
                    return mipsCreator.createBRGT(labelName, sourceRegOne, immValTwo);
                }
                return mipsCreator.createBRGT(labelName, sourceRegOne, sourceRegTwo);

            case BRGEQ:
                if(sourceRegOne == null && sourceRegTwo == null) {
                    return mipsCreator.createBRGEQ(labelName, immValOne, immValTwo);
                }
                if(sourceRegOne == null) {
                    return mipsCreator.createBRGEQ(labelName, immValOne, sourceRegTwo);
                }
                if(sourceRegTwo == null) {
                    return mipsCreator.createBRGEQ(labelName, sourceRegOne, immValTwo);
                }
                return mipsCreator.createBRGEQ(labelName, sourceRegOne, sourceRegTwo);
            
            case BRLEQ:
                if(sourceRegOne == null && sourceRegTwo == null) {
                    return mipsCreator.createBRLEQ(labelName, immValOne, immValTwo);
                }
                if(sourceRegOne == null) {
                    return mipsCreator.createBRLEQ(labelName, immValOne, sourceRegTwo);
                }
                if(sourceRegTwo == null) {
                    return mipsCreator.createBRLEQ(labelName, immValOne, sourceRegTwo);
                }
                return mipsCreator.createBrleq(labelName, sourceRegOne, sourceRegTwo);
        }

        System.out.println("ERRROR SHOULD NEVER BE HIT");
        return null;
    }
            
            
    private List<MIPSInstruction> convertRType(IRInstruction instruction) {
        Register destReg = variableToRegister(instruction.operands[0]);
        Register sourceRegOne = variableToRegister(instruction.operands[1]);
        if(instruction.operands.size == 2) { // if only two operands exist must be an assign
            return mipsCreator.createMove(destReg, sourceRegOne);
        }
        Register sourceRegTwo = variableToRegister(instruction.operands[2]);
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
        Register zeroReg = new Register('0');
        Register regOne = funcMap.get(instruction.operands[1].toString());
        if(instruction.operands.size == 2) { // if only two operands exist must be an assign
            Imm valOne = createImmediate(instruction.operands[1]);
            return createAdd(destReg, regOne, valOne);
        }
        Register regTwo = funcMap.get(instruction.operands[2].toString());
        if(regOne == null) {
            Imm valOne = createImmediate(instruction.operands[1])
        } 
        if(regTwo == null) {
            imm valTwo = createImmediate(instruction.operands[2]);
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
                } else if (regTwo == null) { 
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

    
    private boolean dataOperation (IRInstruction instruction) {
        switch(instruction.opCode) {
            case ARRAY_LOAD:
            case ARRAY_STORE:
                return true;
            case ASSIGN:
                if(instruction.operands.size == 3) {
                    return true;
                }
            default: return false;
        }
    }

    // if instruction has any numbers in its operands instead of variables it is an i-type instruction
    private boolean iType (IRInstruction instruction) {
        
        for(int i = 0; i < instruction.operands.size; i++) {
            if(instruction.operands[i] instanceof IRConstantOperand && instruction.operands[i].type instanceof IRIntType
                && Integer.parseInt(instruction.operands[i].getValueString()) <= 0x7FFF && Integer.parseInt(instruction.operands[i].getValueString() > (0x7FFF * -1))) {
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
        String returnString = TEMPREG + largestRegVal
        largestRegVal ++;
        return returnString;
    }

    private Imm createImmediate(IROperand operand) {
        immVal new Imm("DEC", Integer.parseInt(operand.toString()));
        return immVal;
    }
}