import ir.*;
import ir.datatype.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;
import java.io.*;
import main.java.mips.*;
import main.java.mips.operand.*;


public class InstructionConverter {
    private InstructionCreator mipsCreator;
    private final String argsReg = "a";
    private final String tempReg = "t";
    private final Register zeroReg = new Register("0");
    private final Register returnReg = new Register ("v0");
    private int largestRegVal;
    private ArrayList<HashMap<String, Register>> regMap;     // stores which variables have been allocated inside of a program
    private ArrayList<HashMap<String, Integer>> arrayMap;    // stores which variables have already been allocated inside of a function
    private HashMap<String, Register> funcMap;
    private String functionName;
    private HashSet<String> functions;



    private String globalLabel;
   
    public InstructionConverter() {
        mipsCreator = new InstructionCreator();
        regMap = new ArrayList<>();
        funcMap = new HashMap<>();
        functions = new HashSet<String>();
        largestRegVal = 0;
        globalLabel = "";
        functionName = "";
    }


    // given the header for a function, allocate space for all of its variables, and 
    // move perameters out of argument registers 
    public List<MIPSInstruction> convertHeader(IRFunction function) {
        functionName = function.name;
        createLabel(function);
        List<MIPSInstruction> returnList = convertArguments(function);
        returnList.addAll(allocateVariables(function));
        return returnList;

    }

    public void createLabel(IRFunction function) {
        globalLabel = function.name;
    }

    public List<MIPSInstruction> convertArguments(IRFunction function) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        for(int arg_num = 0; arg_num < function.parameters.size(); arg_num++) {
            Register sourceReg = new Register(argsReg + arg_num);
            Register destReg = variableToRegister(function.parameters.get(arg_num));
            returnList.addAll(mipsCreator.createMove(destReg, sourceReg, getLabel()));
        }
        return returnList;
    }

    public List<MIPSInstruction> allocateVariables(IRFunction function) {
        List<MIPSInstruction> returnList = new LinkedList<>(); 
        for(int var_num = 0; var_num < function.variables.size(); var_num++) {
            // EDGE CASE: arrays can't be stored as registers, need to map into heap 
            if(function.variables.get(var_num).type instanceof IRArrayType) {
                IRArrayType var = (IRArrayType) function.variables.get(var_num).type;
                Register arrayPtrReg = variableToRegister(function.variables.get(var_num));
                Imm arraySizeImm = new Imm("" + var.getSize(), "DEC");
                returnList.addAll(mipsCreator.createArray(arraySizeImm, arrayPtrReg, getLabel()));
                
            }
            variableToRegister(function.variables.get(var_num));
        }
        return returnList;
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
                switch(instruction.operands[0].toString()) {
                    case "puti":
                        if(printReg == null) {
                            return mipsCreator.createPUTI(printVal, getLabel());
                        }
                        return mipsCreator.createPUTI(printReg, getLabel());
                    

                    case "putc":
                        if(printReg == null) {
                            return mipsCreator.createPUTC(printVal, getLabel());
                        }
                        return mipsCreator.createPUTC(printReg, getLabel());
                }

            case CALLR: 
                Register returnReg = variableToRegister(instruction.operands[0]);
                switch(instruction.operands[1].toString()) {
                case "geti":
                    return mipsCreator.createGETI(returnReg, getLabel());
                case "getc":
                    return mipsCreator.createGETC(returnReg, getLabel());

                }
            
        }
        System.out.println("RETURNING NULL INSTRUCTION IN CREATE IR FUNCTION CALL " + instruction.toString());
        System.exit(0);
        return null;
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
                if(offsetReg == null) {
                    Imm offsetImm = createImmediate(instruction.operands[2]);
                    return mipsCreator.createArrayLoad(arrayStoreReg, arrayPtrReg, offsetImm, getLabel());
                }
                return mipsCreator.createArrayLoad(arrayStoreReg, arrayPtrReg, offsetReg, getLabel());

            // if store, store reg value in array
            case ARRAY_STORE:
                // if immediate grab immediate
                if(offsetReg == null) {
                    Imm offsetImm = createImmediate(instruction.operands[2]);
                    return mipsCreator.createArrayStore(arrayStoreReg, arrayPtrReg, offsetImm, getLabel());
                }
                return mipsCreator.createArrayStore(arrayStoreReg, arrayPtrReg, offsetReg, getLabel());
        }
        System.out.println("RETURNING NULL INSTRUCTION IN CONVERT DATA TYPE " + instruction.toString());
        System.exit(0);
        return null;
        
    }

    private List<MIPSInstruction> convertJType(IRInstruction instruction) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        switch(instruction.opCode) {
            case GOTO: {
                String label = makeLabel(instruction.operands[0].toString());
                Addr labelAddress = new Addr(label);
                return mipsCreator.createJump(labelAddress, getLabel());
            }
            case CALL: {
                // load arguments into argument registers
                for(int i = 1; i < instruction.operands.length; i++) {
                    // CHECK FOR IF ARGUMENT IS IMMEDIATE OR REGISTER!
                    Register sourceReg = (funcMap.get(instruction.operands[i].toString()));
                    Register destReg = new Register(argsReg + (i - 1));
                    if(sourceReg == null) {
                        Imm sourceImm = createImmediate(instruction.operands[i]);
                        returnList.addAll(mipsCreator.createAdd(destReg, zeroReg, sourceImm, getLabel()));
                    } else {
                        returnList.addAll(mipsCreator.createMove(destReg, sourceReg, getLabel()));
                    } 
                } // jump after loading arguments
                String label = makeLabel(instruction.operands[0].toString());
                Addr labelAddress = new Addr(label);
                returnList.addAll(mipsCreator.createLinkedJump(labelAddress, getLabel()));
                return returnList;
            }
            case CALLR: {
                // load arguments into argument registers
                for(int i = 2; i < instruction.operands.length; i++) {
                    // CHECK IF ARGUMENT IS IMMEDIATE OR REGISTER!
                    Register sourceReg = funcMap.get(instruction.operands[i].toString());
                    Register destReg = new Register(argsReg + (i - 2));
                    if(sourceReg == null) {
                        Imm sourceImm = createImmediate(instruction.operands[i]);
                        returnList.addAll(mipsCreator.createAdd(destReg, zeroReg, sourceImm, getLabel()));
                    } else {
                        returnList.addAll(mipsCreator.createMove(destReg, sourceReg, getLabel()));
                    }
                } // jump after loading arguments
                String label = makeLabel(instruction.operands[1].toString());
                Addr labelAddress = new Addr(label);
                returnList.addAll(mipsCreator.createLinkedJump(labelAddress, getLabel()));
                Register destReg = variableToRegister(instruction.operands[0]);
                returnList.addAll(mipsCreator.createMove(destReg, returnReg, getLabel()));
                return returnList;
            }
            case LABEL: {
                globalLabel = functionName + "" + instruction.operands[0].toString();
                return null;
            }
            case RETURN: {
                // check if we are returning an immediate
                Register sourceReg = funcMap.get(instruction.operands[0].toString());
                if(sourceReg == null) {
                    Imm sourceImm = createImmediate(instruction.operands[0]);
                    return mipsCreator.createReturn(returnReg, sourceImm, getLabel());
                }
                return mipsCreator.createReturn(returnReg, sourceReg, getLabel());
            }
            // if none of these cases are hit instruction must be a conditional branch
            default: {
                return convertConditionalBranch(instruction);
            }
        }
    }
    
    private List<MIPSInstruction> convertConditionalBranch(IRInstruction instruction) {
        String label = makeLabel(instruction.operands[0].toString());
        Addr labelAddress = new Addr(label);
        Register sourceRegOne = funcMap.get(instruction.operands[1].toString());
        Register sourceRegTwo = funcMap.get(instruction.operands[2].toString());
        Imm immValOne = null;
        Imm immValTwo = null;
        if(sourceRegOne == null) {
            immValOne = createImmediate(instruction.operands[1]);
        }
        if(sourceRegTwo == null) {
            immValTwo = createImmediate(instruction.operands[2]);
        }
        
        switch(instruction.opCode) {
            case BRNEQ: {
                if(sourceRegOne == null) {
                    return mipsCreator.createBRNEQ(labelAddress, sourceRegTwo, immValOne, getLabel());
                }
                if(sourceRegTwo == null) {
                    return mipsCreator.createBRNEQ(labelAddress, sourceRegOne, immValTwo, getLabel());
                }
                return mipsCreator.createBRNEQ(labelAddress, sourceRegOne, sourceRegTwo, getLabel());
            }
            case BREQ: {
                if(sourceRegOne == null) {
                    return mipsCreator.createBREQ(labelAddress, sourceRegTwo, immValOne, getLabel());
                }
                if(sourceRegTwo == null) {
                    return mipsCreator.createBREQ(labelAddress, sourceRegOne, immValTwo, getLabel());
                }
                return mipsCreator.createBREQ(labelAddress, sourceRegOne, sourceRegTwo, getLabel());
            }
            case BRLT: {
                if(sourceRegOne == null) {
                    return mipsCreator.createBRLT(labelAddress, immValOne, sourceRegTwo, getLabel());
                }   
                if(sourceRegTwo == null) {
                    return mipsCreator.createBRLT(labelAddress, sourceRegOne, immValTwo, getLabel());
                }
                return mipsCreator.createBRLT(labelAddress, sourceRegOne, sourceRegTwo, getLabel());
            }
            case BRGT: {
                if(sourceRegOne == null) {
                    return mipsCreator.createBRGT(labelAddress, immValOne, sourceRegTwo, getLabel());
                }
                if(sourceRegTwo == null) {
                    return mipsCreator.createBRGT(labelAddress, sourceRegOne, immValTwo, getLabel());
                }
                return mipsCreator.createBRGT(labelAddress, sourceRegOne, sourceRegTwo, getLabel());
            }
            case BRGEQ: {
                if(sourceRegOne == null) {
                    return mipsCreator.createBRGEQ(labelAddress, immValOne, sourceRegTwo, getLabel());
                }
                if(sourceRegTwo == null) {
                    return mipsCreator.createBRGEQ(labelAddress, sourceRegOne, immValTwo, getLabel());
                }
                return mipsCreator.createBRGEQ(labelAddress, sourceRegOne, sourceRegTwo, getLabel());
            }
            case BRLEQ: {
                if(sourceRegOne == null) {
                    return mipsCreator.createBRLEQ(labelAddress, immValOne, sourceRegTwo, getLabel());
                }
                if(sourceRegTwo == null) {
                    return mipsCreator.createBRLEQ(labelAddress, immValOne, sourceRegTwo, getLabel());
                }
                return mipsCreator.createBRLEQ(labelAddress, sourceRegOne, sourceRegTwo, getLabel());
            }
        }

        System.out.println("RETURNING NULL INSTRUCTION IN CONVERT CONDITIONAL BRANCH" + instruction.toString());
        System.exit(0);
        return null;
    }
            
            
    private List<MIPSInstruction> convertRType(IRInstruction instruction) {
        Register destReg = variableToRegister(instruction.operands[0]);
        Register sourceRegOne = variableToRegister(instruction.operands[1]);
        if(instruction.operands.length == 2) { // if only two operands exist must be an assign
            return mipsCreator.createMove(destReg, sourceRegOne, getLabel());
        }
        Register sourceRegTwo = variableToRegister(instruction.operands[2]);
        switch(instruction.opCode) {
            case ADD: {
                return mipsCreator.createAdd(destReg, sourceRegOne, sourceRegTwo, getLabel());
            }
            case SUB: {
                return mipsCreator.createSub(destReg, sourceRegOne, sourceRegTwo, getLabel());
            }
            case MULT: {
                return mipsCreator.createMult(destReg, sourceRegOne, sourceRegTwo, getLabel());
            }
            case DIV: {
                return mipsCreator.createDiv(destReg, sourceRegOne, sourceRegTwo, getLabel());
            }
            case AND: {
                return mipsCreator.createAnd(destReg, sourceRegOne, sourceRegTwo, getLabel());
            }
            case OR: {
                return mipsCreator.createOr(destReg, sourceRegOne, sourceRegTwo, getLabel());
            }
        }
        System.out.println("RETURNING NULL INSTRUCTION IN CONVERT R TYPE ");
        System.out.print(instruction.opCode);
        for(int i = 0; i < instruction.operands.length; i++) {
            System.out.print(" " + instruction.operands.toString());
        }
        System.out.println();
        System.exit(0);
        return null;
    }

    private List<MIPSInstruction> convertIType(IRInstruction instruction) {
        Imm valOne = null;
        Imm valTwo = null;
        System.out.println("operand 0 = " + instruction.operands[0].toString());
        System.out.println("operand 1 = " + instruction.operands[1].toString());
        Register destReg = variableToRegister(instruction.operands[0]);
        Register regOne = funcMap.get(instruction.operands[1].toString());
        if(instruction.operands.length == 2) { // if only two operands exist must be an assign
            valOne = createImmediate(instruction.operands[1]);
            return mipsCreator.createAdd(destReg, zeroReg, valOne, getLabel());
        }
        Register regTwo = funcMap.get(instruction.operands[2].toString());
        if(regOne == null) {
            valOne = createImmediate(instruction.operands[1]);
        } 
        if(regTwo == null) {
            valTwo = createImmediate(instruction.operands[2]);
        }

        switch(instruction.opCode) {
            // ARITHMETIC INSTRUCTIONS
            case ADD: {
                if(regOne == null && regTwo == null) {
                    return mipsCreator.createAdd(destReg, valOne, valTwo, getLabel());
                } else if (regTwo == null) { 
                    return mipsCreator.createAdd(destReg, regOne, valTwo, getLabel());
                } else {
                    return mipsCreator.createAdd(destReg, regTwo, valOne, getLabel());
                }
            }
            case SUB: {
                if(regOne == null && regTwo == null) {
                    return mipsCreator.createSub(destReg, valOne, valTwo, getLabel());
                }
                else if (regTwo == null) { 
                    return mipsCreator.createSub(destReg, regOne, valTwo, getLabel());
                }
                else {
                    return mipsCreator.createSub(destReg, valOne, regTwo, getLabel());
                }
            }
            case MULT: {
                if(regOne == null && regTwo == null) {
                    return mipsCreator.createMult(destReg, valOne, valTwo, getLabel());
                } else if (regTwo == null) {
                    System.out.println("IMMEDIATE STORED IN LAST OPERAND");
                    return mipsCreator.createMult(destReg, regOne, valTwo, getLabel());
                } else {
                    return mipsCreator.createMult(destReg, regTwo, valOne, getLabel());
                }
            }
            case DIV: {
                if(regOne == null && regTwo == null) {
                    return mipsCreator.createDiv(destReg, valOne, valTwo, getLabel());
                } else if (regTwo == null) {
                    return mipsCreator.createDiv(destReg, regOne, valTwo, getLabel());
                } else {
                    return mipsCreator.createDiv(destReg, valOne, regTwo, getLabel());
                }
            }
            case AND: {
                if(regOne == null && regTwo == null) {
                    return mipsCreator.createAnd(destReg, valOne, valTwo, getLabel());
                } else if (regTwo == null) { 
                    return mipsCreator.createAnd(destReg, regOne, valTwo, getLabel());
                } else {
                    return mipsCreator.createAnd(destReg, regTwo, valOne, getLabel());
                }
            }
            case OR: {
                if(regOne == null && regTwo == null) {
                    return mipsCreator.createOr(destReg, valOne, valTwo, getLabel());
                } else if (regTwo == null) { 
                    return mipsCreator.createOr(destReg, regOne, valTwo, getLabel());
                } else {
                    return mipsCreator.createOr(destReg, regTwo, valOne, getLabel());
                } 
            }
        }
        System.out.println("RETURNING NULL MIPS INSTRUCTION IN CONVERT I TYPE " + instruction.toString());
        System.exit(0);
        return null;
    }

    private boolean jType (IRInstruction instruction) {
        
        switch(instruction.opCode) {
            case LABEL: 
            case GOTO:
            case BREQ:
            case BRLT:
            case BRGT:
            case BRLEQ:
            case BRGEQ:
            case BRNEQ: 
            case RETURN:
            case CALL:
            case CALLR:
                return true;
            default:
                return false;
        }

    }

    private boolean IRFunctionCall(IRInstruction instruction) {
        switch(instruction.opCode) {
            case CALL: {
                switch(instruction.operands[0].toString()) {
                    case "puti":
                    case "putf":
                    case "putc":
                        return true;
                    default:
                        return false;
                }
            }

            case CALLR: {

                switch(instruction.operands[1].toString()) {
                    case "geti": 
                    case "getf": 
                    case "getc":
                        return true;
                    default:
                        return false;
                }
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
                if(instruction.operands.length == 3) {
                    return true;
                }
            default: return false;
        }
    }

    // if instruction has any numbers in its operands instead of variables it is an i-type instruction
    private boolean iType (IRInstruction instruction) {
        
        for(int i = 0; i < instruction.operands.length; i++) {
            if(instruction.operands[i] instanceof IRConstantOperand) {
                IRConstantOperand operand = (IRConstantOperand) instruction.operands[i];
                if(operand.type instanceof IRIntType) {
                    return true;
                }
            }        
        }
        return false;
    }

    // method should be called after every function to wipe values from function mapping!
    public void clearFuncMap() {
        // add funcMap values to regMap
        HashMap<String, Register> hardCopy = new HashMap<>();
        // Iterate over the original HashMap and copy each entry to the new HashMap
        for (Map.Entry<String, Register> entry : funcMap.entrySet()) {
            hardCopy.put(entry.getKey(), entry.getValue());
        }
        regMap.add(hardCopy);
        
        // clear funcMap so it can be used for the next function
        funcMap.clear();
    }


    // method should ONLY be used on destination registers which can either already be in funcMap or need to be allocated,
    // ALL SOURCE REGISTERS SHOULD ALREADY BE IN funcMap!
    private Register variableToRegister(IROperand operand) {
        Register newReg;
        if(funcMap.get(operand.toString()) == null) {
            newReg = new Register(getEmptyRegister());
            System.out.println("assigning register " + newReg.toString() + " to variable " + operand.toString());
            funcMap.put(operand.toString(), newReg);
        } else {
            newReg = funcMap.get(operand.toString());
            System.out.println("grabbed register " + newReg.toString() + " allocated to variable " + operand.toString());
        }

        return newReg;
    }

    private String getEmptyRegister() {
        String returnString = tempReg + largestRegVal;
        largestRegVal ++;
        return returnString;
    }

    private Imm createImmediate(IROperand operand) {
        Imm immVal = new Imm("" + Integer.parseInt(operand.toString()), "DEC");
        return immVal;
    }

    private String getLabel() {
        String returnString = globalLabel;
        globalLabel = "";
        return returnString;
    }

    public void addFunction(IRFunction function) {
        functions.add(function.name);
    }

    private String makeLabel(String labelName) {
        if(functions.contains(labelName)) {
            return labelName;
        }
        else {
            return functionName + "_" + labelName;
        }
    }
}