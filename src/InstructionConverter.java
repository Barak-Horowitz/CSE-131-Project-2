import ir.*;
import ir.datatype.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;
import java.io.*;
import main.java.mips.*;
import main.java.mips.operand.*;

public class InstructionConverter {
    protected InstructionCreator mipsCreator;
    // ALLOCATED REGISTERS
    protected final String argsReg = "$a";
    protected final Register zeroReg = new Register("$0");
    protected final Register returnReg = new Register ("$v0");
    protected final Register stackPointer = new Register("$sp");
    protected final Register operandOneRegister = new Register("$s1");
    protected final Register operandTwoRegister = new Register("$s2");
    protected final Register destinationRegister = new Register("$s0");
    protected final Register returnAddressRegister = new Register("$ra");

    // FUNCTION NAME DATA STRUCTURES
    protected String functionName; // stores name of our current function
    protected HashSet<String> functions; // stores name of all functions
    
    // STACK DATA STRUCTURES
    protected HashMap<String, Integer> variableStackOffset; // stores all variables offset from stack within a function

    
    protected int maxStackOffset; // stores the maximum stack offset of our current stack frame
    protected String globalLabel; // stores the label of the current instruction

    public InstructionConverter() {
        mipsCreator = new InstructionCreator();
        functions = new HashSet<String>();
        globalLabel = "";
        functionName = "";
        maxStackOffset = 4;
        variableStackOffset = new HashMap<>();        
    }


    // given the header for a function, allocate space for all of its variables, and 
    // move perameters out of argument registers 
    public List<MIPSInstruction> convertHeader(IRFunction function) {
        // upon entering a new function assign space on the stack to store all of its variables
        // and place all of its arguments on the stack in the correct positions


        System.out.println("\n\n\n CONVERTING A HEADER \n\n\n");
        maxStackOffset = 4;
        variableStackOffset.clear();
        functionName = function.name;
        List<MIPSInstruction> returnList = createLabel(function);
        createStackOffsets(function);
        returnList.addAll(saveReturnAddress());
        returnList.addAll(convertArguments(function));
        returnList.addAll(allocateVariables(function));
        printStackContents(function);
        return returnList;

    }

    protected void printStackContents(IRFunction function) {
        System.out.println("STACK CONTENTS AFTER ENTERING FUNCTION " + function.name);
        printStackContents();
    }

    protected void printStackContents() {
        for(Map.Entry<String, Integer> entry: variableStackOffset.entrySet()) {
            System.out.println("VARIABLE " + entry.getKey() + " STORED AT OFFSET " + entry.getValue());
        }
    }

    protected List<MIPSInstruction> createLabel(IRFunction function) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(mipsCreator.createLabel(function.name));
        return returnList;
    }

    protected List<MIPSInstruction> saveReturnAddress() {
        Imm saveOffset = new Imm("0", "DEC");
        return mipsCreator.createStoreFromRegister(returnAddressRegister, stackPointer, saveOffset, getLabel());

    }

    protected List<MIPSInstruction> restoreReturnAddress() {
        Imm saveOffset = new Imm("0", "DEC");
        return mipsCreator.createLoadToRegister(returnAddressRegister, stackPointer, saveOffset, getLabel());
    }

    // assigns stack offsets to all variables in functions
    public void createStackOffsets(IRFunction function) {
        for(IROperand variable : function.variables) {
            convertVariableToStackOffset(variable);
        }
        for(IROperand variable : function.parameters) {
            convertVariableToStackOffset(variable);
        }
    }

    // moves arguments into their allocated offsets on the stack 
    public List<MIPSInstruction> convertArguments(IRFunction function) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        for(int arg_num = 0; arg_num < function.parameters.size(); arg_num++) {
            // grab variable where argument is stored 
            Register sourceReg = new Register(argsReg + arg_num);
            // move variable to stack
            String varName = function.parameters.get(arg_num).toString();
            returnList.addAll(loadValueToStack(sourceReg, variableStackOffset.get(varName)));
        }
        return returnList;
    }

    public List<MIPSInstruction> allocateVariables(IRFunction function) {
        List<MIPSInstruction> returnList = new LinkedList<>(); 
        for(int var_num = 0; var_num < function.variables.size(); var_num++) {
            if(function.parameters.contains(function.variables.get(var_num))){
                System.out.println("DUPLICATE VARIABLE SPOTTED");
                 continue;
            }
            // EDGE CASE: if variable is an array, must allocate a pointer 
            if(function.variables.get(var_num).type instanceof IRArrayType) {
                // create array pointer
                IRArrayType var = (IRArrayType) function.variables.get(var_num).type;
                Register arrayPtrReg = operandOneRegister;
                Imm arraySizeImm = new Imm("" + (var.getSize() * 4), "DEC"); // CONVERT SIZE FROM WORDS TO BYTES FOR SBRK CALL
                returnList.addAll(mipsCreator.createArray(arraySizeImm, arrayPtrReg, getLabel()));
                // load array pointer onto stack
                String arrName = function.variables.get(var_num).toString();
                returnList.addAll(loadValueToStack(arrayPtrReg, variableStackOffset.get(arrName)));
                
            }
        }
        return returnList;
    }

    public List<MIPSInstruction> convertInstruction(IRInstruction instruction) {  
        List<MIPSInstruction> returnList = new LinkedList<>();
        // EDGE CASE: if instruction is an IR function call handle seperatly 
        if(IRFunctionCall(instruction)) {
            System.out.println("CONVERTING IR FUNCTION CALL");
            returnList.addAll(convertIRFunctionCall(instruction));
            return returnList;
        }

        // EDGE CASE: if operation manipulates memory handle seperatly
        if(dataOperation(instruction)) {
            System.out.println("CONVERTING DATA OP");
            return convertDataType(instruction);
        }

        if(jType(instruction)) {
            System.out.println("CONVERTING JUMP");
            return convertJType(instruction);
        }
        if(iType(instruction)) {
            System.out.println("CONVERTING IMMEDIATE");
            return convertIType(instruction);
        } // if not i or J type then MUST be R type
        System.out.println("CONVERTING R TYPE");
        return convertRType(instruction);
    }


    // TODO: WRITE METHOD TO CONVERT INTRINSIC IR FUNCTION CALLS
    protected List<MIPSInstruction> convertIRFunctionCall(IRInstruction instruction) {
        System.out.println("CONVERTING AN IR FUNCTION CALL");
        List<MIPSInstruction> returnList = new LinkedList<>();
        switch(instruction.opCode) {
            case CALL:
                Register printReg = null;
                Imm printVal = null;
                // if operand doesn't correspond to a variable then it must be an immediate
                if(!variableStackOffset.containsKey(instruction.operands[1].toString())) {
                    printVal = createImmediate(instruction.operands[1]);
                } else { // if operand corresponds to a variable load it into a register 
                    printReg = operandOneRegister;
                    returnList.addAll(variableToRegister(instruction.operands[1], printReg));
                }
                switch(instruction.operands[0].toString()) {
                    case "puti":
                        if(printReg == null) {
                            returnList.addAll(mipsCreator.createPUTI(printVal, getLabel()));
                            return returnList;
                        }
                        returnList.addAll(mipsCreator.createPUTI(printReg, getLabel()));
                        return returnList;

                    case "putc":
                        if(printReg == null) {
                            returnList.addAll(mipsCreator.createPUTC(printVal, getLabel()));
                            return returnList;
                        }
                        returnList.addAll(mipsCreator.createPUTC(printReg, getLabel()));
                        return returnList;
                }

            case CALLR: 
                Register storeReg = destinationRegister;
                returnList.addAll(variableToRegister(instruction.operands[0], storeReg));
                switch(instruction.operands[1].toString()) {
                    case "geti":
                        returnList.addAll(mipsCreator.createGETI(storeReg, getLabel()));
                        break;
                    
                    case "getc":
                        returnList.addAll(mipsCreator.createGETC(storeReg, getLabel()));
                        break;

                    default:
                        System.out.println("UNABLE TO CONVERT INSTRUCTION IN CONVERT IRFUNCTION");
                        System.exit(-1);
                }
        }
        returnList.addAll(registerToStack(instruction.operands[0], destinationRegister));
        return returnList;
    }

    protected List<MIPSInstruction> convertDataType(IRInstruction instruction) {
        List<MIPSInstruction> returnList = new LinkedList<MIPSInstruction>();
        returnList.addAll(variableToRegister(instruction.operands[0], destinationRegister));
        Register arrayStoreReg = destinationRegister;
        returnList.addAll(variableToRegister(instruction.operands[1], operandOneRegister));
        Register arrayPtrReg = operandOneRegister;
        Register offsetReg = null;
        Imm offsetImm = null;
        if(variableStackOffset.containsKey(instruction.operands[2].toString())) {
            returnList.addAll(variableToRegister(instruction.operands[2], operandTwoRegister));
            offsetReg = operandTwoRegister;
        } else {
            offsetImm = createImmediate(instruction.operands[2]);
        }
        switch(instruction.opCode) {
            // if assign allocate space for array on heap
            case ASSIGN:
                System.out.println("ERROR: ARRAY ASSIGN UNEXPECTED");
                System.exit(-1);

            // if load grab array value from memory 
            case ARRAY_LOAD:
                // if immediate grab immediate 
                if(offsetReg == null) {
                    returnList.addAll(mipsCreator.createArrayLoad(arrayStoreReg, arrayPtrReg, offsetImm, getLabel()));;
                } else {
                    returnList.addAll(mipsCreator.createArrayLoad(arrayStoreReg, arrayPtrReg, offsetReg, getLabel()));
                returnList.addAll(registerToStack(instruction.operands[0], arrayStoreReg));
                return returnList;
                }

            // if store, store reg value in array
            case ARRAY_STORE:
                // if immediate grab immediate
                if(offsetReg == null) {
                    returnList.addAll(mipsCreator.createArrayStore(arrayStoreReg, arrayPtrReg, offsetImm, getLabel()));
                } else {
                    returnList.addAll(mipsCreator.createArrayStore(arrayStoreReg, arrayPtrReg, offsetReg, getLabel()));
                }
                returnList.addAll(registerToStack(instruction.operands[0], arrayStoreReg));
                return returnList;
        }
        System.out.println("UNABLE TO CONVERT INSTRUCTION IN CONVERT DATA TYPE");
        System.exit(0);
        return null;
        
    }

    protected List<MIPSInstruction> convertJType(IRInstruction instruction) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        switch(instruction.opCode) {
            case GOTO: {
                String label = makeLabel(instruction.operands[0].toString());
                Addr labelAddress = new Addr(label);
                return mipsCreator.createJump(labelAddress, getLabel());
            }
            case CALL: {
                System.out.println("CONVERTING A CALL INSTRUCTION");
                // load arguments into argument registers
                for(int i = 1; i < instruction.operands.length; i++) {
                    Register destReg = new Register(argsReg + (i - 1));
                    // CHECK FOR IF ARGUMENT IS IMMEDIATE OR VARIABLE!
                    System.out.println("CHECKING IF OPERAND " + instruction.operands[i].toString() + " IS AN IMMEDIATE OR A VARIABLE");
                    if(variableStackOffset.containsKey(instruction.operands[i].toString())) {
                        returnList.addAll(variableToRegister(instruction.operands[i], operandOneRegister));
                        returnList.addAll(mipsCreator.createMove(destReg, operandOneRegister, getLabel()));
                    } else {
                        Imm sourceImm = createImmediate(instruction.operands[i]);
                        returnList.addAll(mipsCreator.createAdd(destReg, zeroReg, sourceImm, getLabel()));
                    }
                }

                 // ADJUST STACK POINTER BEFORE JUMPING!
                 Imm stackInc = new Imm("" + (maxStackOffset), "DEC");
                 returnList.addAll(mipsCreator.createSub(stackPointer, stackPointer, stackInc, getLabel())); // SUB BECAUSE STACK GROWS DOWN

                 // JUMP
                String label = makeLabel(instruction.operands[0].toString());
                Addr labelAddress = new Addr(label);
                returnList.addAll(mipsCreator.createLinkedJump(labelAddress, getLabel()));
                
                // ADJUST STACK POINTER AFTER RETURNING FROM JUMP
                returnList.addAll(mipsCreator.createAdd(stackPointer, stackPointer, stackInc, getLabel())); // ADD BECAUSE STACK GROWS DOWN
                return returnList;
                
                
            }
            case CALLR: {
                // load arguments into argument registers
                for(int i = 2; i < instruction.operands.length; i++) {
                    Register destReg = new Register(argsReg + (i - 2));
                    // CHECK IF ARGUMENT IS IMMEDIATE OR VARIABLE
                    if(variableStackOffset.containsKey(instruction.operands[i].toString())) {
                        returnList.addAll(variableToRegister(instruction.operands[i], operandOneRegister));
                        returnList.addAll(mipsCreator.createMove(destReg, operandOneRegister, getLabel()));
                    } else {
                        Imm sourceImm = createImmediate(instruction.operands[i]);
                        returnList.addAll(mipsCreator.createAdd(destReg, zeroReg, sourceImm, getLabel()));
                    }
                } 

                // ADJUST STACK POINTER BEFORE JUMPING - increment stack pointer by size of our current frame to allow for stack to grow in new frame
                Imm stackInc = new Imm("" + maxStackOffset, "DEC");
                returnList.addAll(mipsCreator.createSub(stackPointer, stackPointer, stackInc, getLabel())); // SUB BECAUSE STACK GROWS DOWN!
                
                // JUMP
                String label = makeLabel(instruction.operands[1].toString());
                Addr labelAddress = new Addr(label);
                returnList.addAll(mipsCreator.createLinkedJump(labelAddress, getLabel()));
                // ADJUST STACK POINTER AFTER RETURNING FROM JUMP
                System.out.println("STACK INC VALUE " + stackInc.val);
                returnList.addAll(mipsCreator.createAdd(stackPointer, stackPointer, stackInc, getLabel())); // ADD BECAUSE STACK GROWS DOWN!

                // after returning from jump v0 stores the return, assign it to the variable stored in operand 0
                returnList.addAll(registerToStack(instruction.operands[0], returnReg));
                return returnList;
            }
            case LABEL: {
                List<MIPSInstruction> list = new LinkedList<>();
                globalLabel = makeLabel(instruction.operands[0].toString());
                System.out.println("GLOBAL LABEL = " + globalLabel );
                list.addAll(mipsCreator.createLabel(getLabel()));
                return list;
            }
            case RETURN: {
                // check if we are returning an immediate
                Register sourceReg = null;
                Imm sourceImm = null;
                if(variableStackOffset.containsKey(instruction.operands[0].toString())) {
                    returnList.addAll(variableToRegister(instruction.operands[0], operandOneRegister));
                    returnList.addAll(restoreReturnAddress());
                    returnList.addAll(mipsCreator.createReturn(returnReg, operandOneRegister, getLabel()));
                    return returnList;
                }                     
                sourceImm = createImmediate(instruction.operands[0]);
                returnList.addAll(mipsCreator.createReturn(returnReg, sourceImm, getLabel()));
                return returnList;
            }
            // if none of these cases are hit instruction must be a conditional branch
            default: {
                return convertConditionalBranch(instruction);
            }
        }
    }
    
    protected List<MIPSInstruction> convertConditionalBranch(IRInstruction instruction) {
        List<MIPSInstruction> returnList = new LinkedList<MIPSInstruction>();
        String label = makeLabel(instruction.operands[0].toString());
        Addr labelAddress = new Addr(label);
        Register sourceRegOne = null;
        Register sourceRegTwo = null;
        Imm immValOne = null;
        Imm immValTwo = null;
        if(variableStackOffset.containsKey(instruction.operands[1].toString())) {
            sourceRegOne = operandOneRegister;
            returnList.addAll(variableToRegister(instruction.operands[1], sourceRegOne));
        } else {
            immValOne = createImmediate(instruction.operands[1]);
        }
        if(variableStackOffset.containsKey(instruction.operands[2].toString())) {
            sourceRegTwo = operandTwoRegister;
            returnList.addAll(variableToRegister(instruction.operands[2], sourceRegTwo));
        } else {
            immValTwo = createImmediate(instruction.operands[2]);
        }
        switch(instruction.opCode) {
            case BRNEQ: {
                if(sourceRegOne == null) {
                    returnList.addAll(mipsCreator.createBRNEQ(labelAddress, sourceRegTwo, immValOne, getLabel()));
                    return returnList;
                }
                if(sourceRegTwo == null) {
                    returnList.addAll(mipsCreator.createBRNEQ(labelAddress, sourceRegOne, immValTwo, getLabel()));
                    return returnList;
                }
                returnList.addAll(mipsCreator.createBRNEQ(labelAddress, sourceRegOne, sourceRegTwo, getLabel()));
                return returnList;
            }
            case BREQ: {
                if(sourceRegOne == null) {
                    returnList.addAll(mipsCreator.createBREQ(labelAddress, sourceRegTwo, immValOne, getLabel()));
                    return returnList;
                }
                if(sourceRegTwo == null) {
                    returnList.addAll(mipsCreator.createBREQ(labelAddress, sourceRegOne, immValTwo, getLabel()));
                    return returnList;
                }
                returnList.addAll(mipsCreator.createBREQ(labelAddress, sourceRegOne, sourceRegTwo, getLabel()));
                return returnList;
            }
            case BRLT: {
                if(sourceRegOne == null) {
                    returnList.addAll(mipsCreator.createBRLT(labelAddress, immValOne, sourceRegTwo, getLabel()));
                    return returnList;
                }   
                if(sourceRegTwo == null) {
                    returnList.addAll(mipsCreator.createBRLT(labelAddress, sourceRegOne, immValTwo, getLabel()));
                    return returnList;
                }
                returnList.addAll(mipsCreator.createBRLT(labelAddress, sourceRegOne, sourceRegTwo, getLabel()));
                return returnList;
            }
            case BRGT: {
                if(sourceRegOne == null) {
                    returnList.addAll(mipsCreator.createBRGT(labelAddress, immValOne, sourceRegTwo, getLabel()));
                    return returnList;
                }
                if(sourceRegTwo == null) {
                    returnList.addAll(mipsCreator.createBRGT(labelAddress, sourceRegOne, immValTwo, getLabel()));
                    return returnList;
                }
                returnList.addAll(mipsCreator.createBRGT(labelAddress, sourceRegOne, sourceRegTwo, getLabel()));
                return returnList;
            }
            case BRGEQ: {
                if(sourceRegOne == null) {
                    returnList.addAll(mipsCreator.createBRGEQ(labelAddress, immValOne, sourceRegTwo, getLabel()));
                    return returnList;
                }
                if(sourceRegTwo == null) {
                    returnList.addAll(mipsCreator.createBRGEQ(labelAddress, sourceRegOne, immValTwo, getLabel()));
                    return returnList;
                }
                returnList.addAll(mipsCreator.createBRGEQ(labelAddress, sourceRegOne, sourceRegTwo, getLabel()));
                return returnList;
            }
            case BRLEQ: {
                if(sourceRegOne == null) {
                    returnList.addAll(mipsCreator.createBRLEQ(labelAddress, immValOne, sourceRegTwo, getLabel()));
                    return returnList;
                }
                if(sourceRegTwo == null) {
                    returnList.addAll(mipsCreator.createBRLEQ(labelAddress, immValOne, sourceRegTwo, getLabel()));
                    return returnList;
                }
                returnList.addAll(mipsCreator.createBRLEQ(labelAddress, sourceRegOne, sourceRegTwo, getLabel()));
                return returnList;
            }
        }
        System.out.println("UNABLE TO CONVERT INSTRUCTION IN CONVERT CONDITIONAL BRANCH");
        System.exit(-1);
        return null;
    }
            
            
    protected List<MIPSInstruction> convertRType(IRInstruction instruction) {
        List<MIPSInstruction> returnList = new LinkedList<MIPSInstruction>();
        // load in operand variables into registers
        returnList.addAll(variableToRegister(instruction.operands[1], operandOneRegister));
        if(instruction.operands.length == 2) { // if only two operands exist must be an assign
            returnList.addAll(mipsCreator.createMove(destinationRegister, operandOneRegister, getLabel()));
            returnList.addAll(registerToStack(instruction.operands[0], destinationRegister));
            return returnList;
        }
        returnList.addAll(variableToRegister(instruction.operands[2], operandTwoRegister));
        switch(instruction.opCode) {
            case ADD: {
                returnList.addAll(mipsCreator.createAdd(destinationRegister, operandOneRegister, operandTwoRegister, getLabel()));
                break;
            }
            case SUB: {
                returnList.addAll(mipsCreator.createSub(destinationRegister, operandOneRegister, operandTwoRegister, getLabel()));
                break;
            }
            case MULT: {
                returnList.addAll(mipsCreator.createMult(destinationRegister, operandOneRegister, operandTwoRegister, getLabel()));
                break;
            }
            case DIV: {
                returnList.addAll(mipsCreator.createDiv(destinationRegister, operandOneRegister, operandTwoRegister, getLabel()));
                break;
            }
            case AND: {
                returnList.addAll(mipsCreator.createAnd(destinationRegister, operandOneRegister, operandTwoRegister, getLabel()));
                break;
            }
            case OR: {
                returnList.addAll(mipsCreator.createOr(destinationRegister, operandOneRegister, operandTwoRegister, getLabel()));
                break;
            }
            default:
                System.out.println("UNABLE TO CONVERT AN INSTRUCTION IN R TYPE");
                System.exit(-1);
        }
        returnList.addAll(registerToStack(instruction.operands[0], destinationRegister));
        return returnList;
    }

    protected List<MIPSInstruction> convertIType(IRInstruction instruction) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        Imm valOne = null;
        Imm valTwo = null;
        Register sourceOneReg = null;
        Register sourceTwoReg = null; 

        System.out.println("operand 0 = " + instruction.operands[0].toString());
        System.out.println("operand 1 = " + instruction.operands[1].toString());
        if(variableStackOffset.containsKey(instruction.operands[1].toString())) {
            System.out.println("OPERAND " + instruction.operands[1].toString() + "IS A VARIABLE");
            returnList.addAll(variableToRegister(instruction.operands[1], operandOneRegister));
            sourceOneReg = operandOneRegister;
        } else {
            System.out.println("OPERAND " + instruction.operands[1].toString() + " IS A NUMBER");
            valOne = createImmediate(instruction.operands[1]);
        }
        if(instruction.operands.length == 2) { // if only two operands exist must be an assign
            if(sourceOneReg == null) {
                returnList.addAll(mipsCreator.createAdd(destinationRegister, zeroReg, valOne, getLabel()));
            } else {
                returnList.addAll(mipsCreator.createMove(destinationRegister, sourceOneReg, getLabel()));
            }
            System.out.println("MOVING VARIABLE " + instruction.operands[0].toString() + " to stack");
            System.out.println("ITS OFFSET = " + variableStackOffset.get(instruction.operands[0].toString()));
            returnList.addAll(registerToStack(instruction.operands[0], destinationRegister));
            return returnList;
        }
        if(variableStackOffset.containsKey(instruction.operands[2].toString())) {
            System.out.println("OPERAND " + instruction.operands[2].toString() + " IS A VARIABLE");
            returnList.addAll(variableToRegister(instruction.operands[2], operandTwoRegister));
            sourceTwoReg = operandTwoRegister;
        } else {
            System.out.println("OPERAND " + instruction.operands[2].toString() + " IS A NUMBER");
            valTwo = createImmediate(instruction.operands[2]);
        }
        switch(instruction.opCode) {
            case ADD: {
                if(sourceOneReg == null && sourceTwoReg == null) {
                    returnList.addAll(mipsCreator.createAdd(destinationRegister, valOne, valTwo, getLabel()));
                } else if (sourceTwoReg == null) { 
                    returnList.addAll(mipsCreator.createAdd(destinationRegister, sourceOneReg, valTwo, getLabel()));
                } else {
                    returnList.addAll(mipsCreator.createAdd(destinationRegister, sourceTwoReg, valOne, getLabel()));
                }
                break;
            }
            case SUB: {
                if(sourceOneReg == null && sourceTwoReg == null) {
                    returnList.addAll(mipsCreator.createSub(destinationRegister, valOne, valTwo, getLabel()));
                }
                else if (sourceTwoReg == null) { 
                    returnList.addAll(mipsCreator.createSub(destinationRegister, sourceOneReg, valTwo, getLabel()));
                }
                else {
                    returnList.addAll(mipsCreator.createSub(destinationRegister, valOne, sourceTwoReg, getLabel()));
                }
                break;
            }
            case MULT: {
                if(sourceOneReg == null && sourceTwoReg == null) {
                    returnList.addAll(mipsCreator.createMult(destinationRegister, valOne, valTwo, getLabel()));
                } else if (sourceTwoReg == null) {
                    returnList.addAll(mipsCreator.createMult(destinationRegister, sourceOneReg, valTwo, getLabel()));
                    return returnList;
                } else {
                    returnList.addAll(mipsCreator.createMult(destinationRegister, sourceTwoReg, valOne, getLabel()));
                }
                break;
            }
            case DIV: {
                if(sourceOneReg == null && sourceTwoReg == null) {
                    returnList.addAll(mipsCreator.createDiv(destinationRegister, valOne, valTwo, getLabel()));
                } else if (sourceTwoReg == null) {
                    returnList.addAll(mipsCreator.createDiv(destinationRegister, sourceOneReg, valTwo, getLabel()));
                } else {
                    returnList.addAll(mipsCreator.createDiv(destinationRegister, valOne, sourceTwoReg, getLabel()));
                }
                break;
            }
            case AND: {
                if(sourceOneReg == null && sourceTwoReg == null) {
                    returnList.addAll(mipsCreator.createAnd(destinationRegister, valOne, valTwo, getLabel()));
                } else if (sourceTwoReg == null) { 
                    returnList.addAll(mipsCreator.createAnd(destinationRegister, sourceOneReg, valTwo, getLabel()));
                } else {
                    returnList.addAll(mipsCreator.createAnd(destinationRegister, sourceTwoReg, valOne, getLabel()));
                }
                break;
            }
            case OR: {
                if(sourceOneReg == null && sourceTwoReg == null) {
                    returnList.addAll(mipsCreator.createOr(destinationRegister, valOne, valTwo, getLabel()));
                } else if (sourceOneReg == null) { 
                    returnList.addAll(mipsCreator.createOr(destinationRegister, sourceOneReg, valTwo, getLabel()));
                } else {
                    returnList.addAll(mipsCreator.createOr(destinationRegister, sourceTwoReg, valOne, getLabel()));
                }
                break; 
            }
            default: {
                System.out.println("UNABLE TO ASSIGN AN INSTRUCTION IN CONVERT I TYPE");
                System.exit(-1);
            }
        }
        returnList.addAll(registerToStack(instruction.operands[0], destinationRegister));
        return returnList;
    }

    protected boolean jType (IRInstruction instruction) {
        
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

    protected boolean IRFunctionCall(IRInstruction instruction) {
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

    
    protected boolean dataOperation (IRInstruction instruction) {
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
    protected boolean iType (IRInstruction instruction) {
        
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



    // given a variable and a register to write it to, write the variable to the register
    protected List<MIPSInstruction> variableToRegister(IROperand operand, Register storeReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        System.out.println("LOADING VARIABLE " + operand.toString() + " INTO A REGISTER");
        // load the value in from the stack and place it in the specified register 
        // grab the stack offset of our variable
        int offset = findOffset(operand);
        // load from the stack offset to register
        returnList.addAll(loadValueToReg(storeReg, offset));
        // return the instructions used to store the variable in a register
        return returnList;
    }

    // given a variable and the register which holds the variable write the variable to the correct location on the stack
    protected List<MIPSInstruction> registerToStack(IROperand operand, Register storeReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        System.out.println("ATTEMPTING TO PLACE VARIABLE " + operand.toString() + " ONTO STACK");
        // grab stack offset of variable
        int offset = findOffset(operand);
        // load from register to stack offset
        returnList.addAll(loadValueToStack(storeReg, offset));
        // return instructions used to store variable on stack
        return returnList;
    }

    // given a variable returns the offset at which the variable is stored on the stack
    protected int findOffset(IROperand operand) {
        if(!variableStackOffset.containsKey(operand.toString())) {
            System.out.println("ATTEMPTED TO FIND OFFSET OF VARIABLE " + operand.toString() + " SO THAT WE COULD LOAD IT INTO A REG BUT NONE WAS FOUND");
            try {
                functions.add(null);
            } catch(Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return(variableStackOffset.get(operand.toString()));
    }

    protected List<MIPSInstruction> loadValueToReg(Register freeReg, int offset) {
        System.out.println("\n\n\n LOADING VALUE INTO A REGISTER \n\n\n");
        Imm offsetImm = new Imm("" + offset, "DEC");
        return mipsCreator.createLoadToRegister(freeReg, stackPointer, offsetImm, getLabel());
    }

    protected List<MIPSInstruction> loadValueToStack(Register dataReg, int offset) {
        System.out.println("\n\n\n LOADING VALUE INTO STACK \n\n\n");
        Imm offsetImm = new Imm("" + offset, "DEC");
        return mipsCreator.createStoreFromRegister(dataReg, stackPointer, offsetImm, getLabel());
    }


    protected void convertVariableToStackOffset(IROperand operand) {
        // if variable is already assigned a stack offset return immediatly
        if(variableStackOffset.containsKey(operand.toString())) {
            return;
        } 
        // else assign it a stack offset and return
        variableStackOffset.put(operand.toString(), maxStackOffset);
        maxStackOffset += 4;
        return;
    }

    protected Imm createImmediate(IROperand operand) {
        Imm immVal = new Imm("" + Integer.parseInt(operand.toString()), "DEC");
        return immVal;
    }

    protected String getLabel() {
        String returnString = globalLabel;
        globalLabel = "";
        return returnString;
    }

    protected void addFunction(IRFunction function) {
        functions.add(function.name);
    }

    protected String makeLabel(String labelName) {
        if(functions.contains(labelName)) {
            return labelName;
        }
        else {
            return functionName + labelName;
        }
    }

    // if function is main exit program else add jr
    public List<MIPSInstruction> addExit(IRFunction function) {
        if(function.name.equals("main")) {
            return mipsCreator.createExit(getLabel());
        } else {
            List<MIPSInstruction> returnList = new LinkedList<>();
            returnList.addAll(restoreReturnAddress());
            returnList.addAll(mipsCreator.createJumpReturn(getLabel()));
            return returnList;
        }
    }
}