import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;
import java.io.*;
import mips.*;


// stores the mapping of tiger-IR variables to MIPS registers
private Map<String, Integer> regMap;

// stores address of first free MIPS register (all registers past this point are free as well)
private int firstFreeReg;




public class MipsConverter {
    public static void main(String[] args) throws Exception {

        regMap = new HashMap<>();
        firstFreeReg = 32;
        // file reader reads a text file containing text code and generates datastructure for the program
        IRReader file_reader = new IRReader();
        IRProgram program = file_reader.parseIRFile(args[0]);

        MipsConverter converter = new MipsConverter();

        // convert the IR program given to a MIPS program
        MIPSProgram mipsProg = converter.IrToMips(program)
    }

    public MIPSProgram IrToMips(IRProgram irProgram) {
        // run an instruction selection algorithm to convert ir instructions to series of MIPS instruction
        Map<Integer, MIPSInstruction> = selectInstructions(irProgram)

    }

    private Map<Integer, MIPSInstruction> selectInstructions(IRProgram irProgram) {
        int curr_index = 0; // stores index of instruction
        Map<Integer, MIPSInstruction> mipsInstructionSet = new HashMap<>(); // stores converted instructions
        // loop over all IR instructions, converting each one to a set of MIPS instructions
        for(IRFunction function : irProgram.functions) {
            regMap.clear() // all functions have independant variable names

            for(IRInstruction instruction : function.instructions) {
                // convert current ir instruction to a set of MIPS instructions
                List<MIPSInstruction> mipsInstructions = convertInstruction(instruction)
                // add all created mips instructions to MIPS instruction set
                for (MIPSInstruction mipsInstruction : mipsInstructions) {
                    mipsInstructionSet.add(curr_index, mipsInstruction)
                    curr_index ++;
                }
            }
        }
    }

    private List<MIPSInstruction> convertInstruction(IRInstruction instruction) {
        
        // EDGE CASE: if operation manipulates memory handle seperatly 
        switch(instruction.opCode) {
            case ARRAY_STORE:
            case ARRAY_LOAD:
                return convertData(instruction)
            default:
                break;
        }

        if(jType(instruction)) {
            return convertJType(instruction);
        }
        if(iType(instruction)) {
            return convertIType(instruction)
        }
        return convertRType(instruction)


    }

    // given an instruction operating on only variables converts it to the appropriate R type MIPS instructions
    // garunteed to take in an instruction with 3 operands, at least 2 of which correspond to previously assigned variables
    private List<MIPSInstruction> convertRType(IRInstruction instruction) {
        // EDGE CASE - ASSIGN ONLY HAS TWO OPERANDS 
        if(instruction.opCode == IRInstruction.opCode.ASSIGN) {
            // TODO: ADD ASSIGN
        }

        List<MIPSInstruction> returnList = new LinkedList<>();
        Integer sourceRegOne = regMap.get(instruction.operands[1]);
        Integer sourceRegTwo = regMap.get(instruction.operands[2]);

        if(sourceRegOne == null || sourceRegTwo == null) {
            System.out.println("ERROR - UNASSIGNED VARIABLE SPOTTED IN R TYPE INSTRUCTION GENERATION")
            exit();
        }

        Integer destReg = regMap.get(instruction.operands[0]);
        if(destReg == null) {
            destReg = getFreeReg();
            regMap.put(instructions.operands[0].value, dest_reg);
        }

        // allocate registers
        Register opZero = new Register(destReg);
        Register opOne = new Register(sourceRegOne);
        Register opTwo = new Register(sourceRegTwo);

        // once registers are constructed simply pass in the correct opcode for the comand
        // TODO: FIGURE OUT IF ALL INSTRUCTIONS NEED A LABEL
        switch(instruction.opCode) {
            case ADD:
                MIPSInstruction convertedInstruction = new MIPSInstruction(MipsOp.ADD, "", opZero, opOne, opTwo);
                returnList.add(convertedInstruction)
                break;
            case SUB:
                MIPSInstruction convertedInstruction = new MIPSInstruction(MipsOp.SUB, "", opZero, opOne, opTwo);
                returnList.add(convertedInstruction)
                break;
            case MULT:
                MIPSInstruction convertedInstruction = new MIPSInstruction(MipsOp.MUL, "", opZero, opOne, opTwo);
                returnList.add(convertedInstruction)
                 break;
            case DIV:
                MIPSInstruction convertedInstruction = new MIPSInstruction(MipsOp.DIV, "", opZero, opOne, opTwo);
                returnList.add(convertedInstruction)
                break;
            case AND:
                MIPSInstruction convertedInstruction = new MIPSInstruction(MipsOp.AND, "", opZero, opOne, opTwo);
                returnList.add(convertedInstruction)
                break;
            case OR:
                MIPSInstruction convertedInstruction = new MIPSInstruction(MipsOp.OR, "", opZero, opOne, opTwo);
                returnList.add(convertedInstruction)
                break;
            
        }
        return returnList;


      

    }

    // given an instruction operating on variables and immediates, converts it to the appropriate I type MIPS instructions
    private List<MIPSInstruction> convertIType(IRInstruction instruction) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        // instruction is garunteed to be an arithmetic instruction (data and jump checked first)
        // TODO: figure out what to do if immediate value is larger then 16 bits!
        Register destReg = new Register(regmap.get(instruction.operands[0]));
        Register zeroReg = new Register(0);
        Integer sourceOne = regmap.get(instruction.operands[1]);
        Integer sourceTwo = regmap.get(instruction.operands[2]);
        switch(instruction.opCode) {
            case ADD:
            // EDGE CASE - if both variables are immediates, two additions are necessary 
                if(sourceOne == null && sourceTwo == null) {
                Imm valOne = new Imm("DEC", Integer.parseInt(instruction.operands[1].toString()));
                imm valTwo = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()));
                MIPSInstruction addFirst = new MIPSInstruction(MipsOp.ADDI, "", destReg, zeroReg, valOne);
                MIPSInstruction addSecond = new MIPSInstruction(mipsOp.ADDI, "", destReg, destReg, valTwo);
                returnList.add(addFirst);
                returnList.add(addSecond);
                // check if immediate is first value or second
                } else if(sourceOne == null) {
                    Imm valOne = new Imm("DEC", Integer.parseInt(instruction.operands[1].toString()));
                    MIPSInstruction add = new MIPSInstruction(mipsOp.ADDI, "", destReg, valOne, sourceTwo);
                    returnList.add(add);
                else {
                    immValTwo = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()));
                    MIPSInstruction add = new MIPSInstruction(mopsOp.ADDI, "", destReg, sourceOne, valTwo);
                    retunList.add(add);
                }
                break;
                
            case SUB:
            // EDGE CASE - if both variables are immediates two subtractions are necessary
                if(sourceOne == null && sourceTwo == null) {
                    Imm valOne = new Imm("DEC", Integer.parseInt(instruction.operands[1].toString()));
                    Imm valTwo = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()) * - 1);
                    MIPSInstruction addFirst = new MIPSInstruction(mipsOp.ADDI, destReg, zeroReg, valOne);
                    MIPSInstruction subSecond = new MIPSInstruction(mipsOP.ADDI, destReg, destReg, valTwo);
                }
                // check if immediate is first value or second
                else if(sourceOne == null) { // if immediate is first value must store it in a register!
                    Imm valOne = new Imm("DEC", Integer.parseInt(instruction.operands[1].toString()));
                    Register sourceTwoReg = new Register(sourceTwo);
                    regVal = getFreeReg();
                    Register sourceOneReg = new Register(regVal());
                    MIPSInstruction addFirst = new MIPSInstruction(mipsOP.ADDI, destReg, zeroReg, valOne);
                    MIPSInstruction subSecond = new MIPSInstruction(mipsOP.sub, destReg, destReg, sourceTwoReg);
                    returnList.add(addFirst);
                    returnList.add(subSecond);
                } else { // if immediate is second value can keep as immediate
                    Register sourceOneReg = new Register(sourceOne);
                    Imm valTwo = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()) *  -1);
                    MIPSInstruction subFirst = new MIPSInstruction(mipsOp.ADDI, destReg, sourceOneReg, valTwo);
                    returnList.add(subFirst);
                }
                break;


            // TODO: FIGURE OUT IF MULT OR DIVIDE CAN HAVE IMMEDIATES!

            case MULT:

            case DIV:
            
            case AND:
            // EDGE CASE - if both variables are immediates, addition following AND is necessary
                if(sourceOne == null && sourceTwo == null) {
                    Imm valOne = new Imm("DEC", Integer.parseInt(instruction.operands[1].toString()));
                    Imm valTwo = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()));
                    MIPSInstruction addFirst = new MIPSInstruction(mipsOp.ADDI, destReg, zeroReg, valOne);
                    MIPSInstruction andSecond = new MIPSInstruction(mipsOP.ANDI, destReg, destReg, valTwo);
                    returnList.add(addFirst);
                    returnList.add(andSecond);
                } else if (sourceOne == null) {
                    Imm valOne = new Imm("DEC", Integer.parseInt(instruction.operands[1].toString()));
                    Register sourceTwoReg = new Register(sourceTwo);
                    MIPSInstruction andFirst = new MIPSInstruction(mipsOp.ANDI, destReg, sourceTwoReg, valOne);
                    returnList.add(andFirst);
                } else {
                    Register sourceOneReg = new Register(sourceOne);
                    Imm valTwo = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()));
                    MIPSInstruction andFirst = new MIPSInstruction(mipsOP.ANDI, destReg, sourceOneReg, valTwo);
                    returnList.add(andFirst);
                }
                break;



                }

            case OR:
            // EDGE CASE - if both variables are immediates, addition following OR is necessary 
                if(sourceOne == null && sourceTwo == null) {
                    Imm valOne = new Imm("DEC", Integer.parseInt(instruction.operands[1].toString()));
                    Imm valTwo = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()));
                    MIPSInstruction addFirst = new MIPSInstruction(mipsOp.ADDI, destReg, zeroReg, valOne);
                    MIPSInstruction orSecond = new MIPSInstruction(mipsOP.ORI, destReg, destReg, valTwo);
                    returnList.add(addFirst);
                    returnList.add(orSecond);

                } else if(sourceOne == null) {
                    Imm valOne = new Imm("DEC", Integer.parseInt(instruction.operands[1].toString()));
                    Register sourceTwoReg = new Register(sourceTwo);
                    MIPSInstruction andFirst = new MIPSInstruction(mipsOp.ORI, destReg, sourceTwoReg, valOne);
                    returnList.add(andFirst);

                } else {
                    Register sourceOneReg = new Register(sourceOne);
                    Imm valTwo = new Imm("DEC", Integer.parseInt(instruction.operands[2].toString()));
                    MIPSInstruction andFirst = new MIPSInstruction(mipsOP.ORI, destReg, sourceOneReg, valTwo);
                    returnList.add(andFirst);
                }

                break;


            case ASSIGN:
                Imm valOne = new Imm("DEC", Integer.parseInt(instruction.operands[1].toString()));
                MIPSInstruction assign = new MIPSInstruction(mipsOP.ADDI, destReg, zeroReg, valOne);
                returnList.add(assign);
                break;

            }



        }

        // TODO: figure out what to do if both operands are immediates!
        return null;

    }

    // given a branch instruction converts it to the appropriate R type MIPS instruction
    private List<MIPSInstruction> convertJType(IRInstruction instruction) {
        List<MIPSInstruction> returnList = new LinkedList<>();

        switch(instruction.opCode) {

            // goto instruction in tigerIR corresponds directly to a jump instruction in MIPS
            case GOTO: 
                String label = instruction.operands[0].getValue();
                Addr labelAddress = new Addr(label);
                MIPSInstruction jump = new MIPSInstruction(MipsOp.j, "", labelAddress);
                break;

            // call instruction in tigerIR corresponds to 2 steps in MIPS
            // 1) loading all arguments onto stack/arguments registers
            // 2) calling the function
            case CALL: // jal instruction
                for(int i = 1; i < instruction.operands.size; i++) {
                    // add first four arguments into a0-a4
                    if((i - 1) < 4) {
                        returnList.add(createMove(regmap.get(instruction.operands[i]), i-1 + 4));
                    }
                    // TODO: HANDLE CASE WHERE WE HAVE MORE THEN 4 ARGUMENTS!
                }
                // add jal instruction after placing all arguments 
                returnList.add(createJumpAndLink(instruction), 0);
                break;
            
            // callr instruction in tigerIR corresponds to three steps in MIPS
            // 1) loading all arguments onto stack/arguments registers
            // 2) calling the function
            // 3) grabbing the value returned by the function and placing it in specified variable/register
            case CALLR: // jal instruction grab return value from link register
                for(int i = 2; i < instructions.operands.size; i++) {
                    // add first four arguments into a0-a4
                    if((i - 2) < 4) {
                        returnList.add(createMove(regmap.get(instruction.operands[i]), i-2 + 4));
                    }
                    // TODO: HANDLE CASE WHERE WE HAVE MORE THEN 4 ARGUMENTS!

                    // add jal instruction after placing arguments 
                    returnList.add(createJumpAndLink(instruction), 1);
                    // move value returned into requested register
                    returnList.add(createMove(2, regmap.get(instruction.operands[0])));
                    break;

                }

            // there is no label instruction in MIPS we spoof it by creating a no-op and assigning it a label 
            case LABEL: 
                // grab the name of the label
                String labelName = instruction.operands[0].getValue()
                // construct a fake no-op with a label name to allow for jumps to this location
                Register noOp = new Register(0);
                Imm zero = New Imm("0", "DEC");
                MIPSInstruction label = new MIPSInstruction(MipsOp.ADDI, labelName, noOp, noOp, zero);
                returnList.add(label)
                break;
            
            // there exists a brneq instruction in mips, we translate it directly
            case BRNEQ:
                String label = instruction.operands[0].getValue();
                Addr labelAddress = new Addr(label);
                Register sourceRegOne = new Register(regmap.get(instructions.operands[1]));
                Register sourceRegTwo = new Register(regmap.get(instructions.operands[2]));
                MIPSInstruction brneq = new MIPSInstruction(MipsOP.BNE, "", sourceRegOne, sourceRegTwo);
                returnList.add(brneq);
                break;
            
            // there exists a breq instruction in mips, we translate it directly
            case BREQ:
                String label = instruction.operands[0].getValue();
                Addr labelAddress = new Addr(label);
                Register sourceRegOne = new Register(regmap.get(instructions.operands[1]));
                Register sourceRegTwo = new Register(regmap.get(instructions.operands[2]));
                MIPSInstruction breq = new MIPSInstruction(MipsOP.BEQ, "", sourceRegOne, sourceRegTwo, labelAddress);
                returnList.add(breq);
                break;
            
            // there does not exist a brlt instruction in mips, we must translate this to a brltz
            // by subtracting the second source operand from the first
            case BRLT:
                String label = instruction.operands[0].getValue();
                Addr labelAddress = new Addr(label);
                returnList.add(createSubtract(1, regmap.get(instruction.operands[1]), instruction.operands[2]));
                Register temp = new Register(1);
                MIPSInstruction brltz = new MIPSInstruction(MIPSOP.brltz. "", temp, labelAddress);
                returnList.add(brltz);
                break;
            
            // there does not exist a brlt instruction in mips we must translate this to a bgtz
            // by subtracting second source operand from the first
            case BRGT:
                String label = instruction.operands[0].getValue();
                Addr labelAddress = new Addr(label);
                returnList.add(createSubtract(1, regmap.get(instruction.operands[1]), instruction.operands[2]));
                Register temp = new Register(1);
                MIPSInstruction bgtz = new MIPSInstruction(MIPSOP.bgtz. "", temp, labelAddress);
                returnList.add(bgtz);
                break;
            
            // there does not exist a brgeq instruction in mips we must translate this to a bgez
            // by subtracting second source operand from first
            case BRGEQ:
                String label = instruction.operands[0].getValue();
                Addr labelAddress = new Addr(label);
                returnList.add(createSubtract(1, regmap.get(instruction.operands[1]), instruction.operands[2]));
                Register temp = new Register(1);
                MIPSInstruction bgez = new MIPSInstruction(MIPSOP.bgez. "", temp, labelAddress);
                returnList.add(bgez);
                break;
            
            case BRLEQ:
                String label = instruction.operands[0].getValue();
                Addr labelAddress = new Addr(label);
                returnList.add(createSubtract(1, regmap.get(instruction.operands[1]), instruction.operands[2]));
                Register temp = new Register(1);
                MIPSInstruction blez = new MIPSInstruction(MIPSOP.blez. "", temp, labelAddress);
                returnList.add(btez);
                break;

            

            // return in tiger IR translates to moving specified variable to return register and calling JR
            CASE RETURN: 
                // move return value into return register
                returnList.add(createMove(regmap.get(instructions.operands[0]), 2));
                // return to PC stored in link register
                MIPSInstruction return = new MIPSInstruction(MIPSOp.JR, "");
                returnList.add(return);
                break;

            

                
        }
        return returnList;


    }

    // given a source and a destination register constructs a move instruction between the two
    private MIPSInstruction createMove(int source, int dest) {
        Register sourceReg = new Register(source);
        Register destReg = new Register(dest);
        MIPSInstruction move = new MIPSInstruction(MIPSOp.MOVE,"", destReg, sourceReg);
        return move;
    }

    // given two source registers and a destination register constructs a subtraction instruction
    private MIPSInstruction createSubtract(int dest, int sourceOne, int sourceTwo) {
        Register sourceRegOne = new Register(sourceOne);
        Register sourceRegTwo = new Register(sourceRegTwo);
        Register destReg = new Register(dest);
        MIPSInstruction subtract = new mipsInstruction(MipsOp.sub, "", destReg, sourceRegOne, sourceRegTwo);
        return subtract;
    }
    
    // given an operand index where a label is stored and its instruction constructs a jump and link to the label
    private mipsInstruction createJumpAndLink(int labelIndex, IRInstruction instruction) {
        String labelName = instruction.operands[labelIndex].getValue();
        Addr labelAddress = new Addr(label);
        MIPSInstruction call = new MIPSInstruction(MipsOp.jal, "", labelAddress);
    }


    // if instruction has any numbers in its operands instead of variables it is an i-type instruction
    private boolean iType (IRInstruction instruction) {
        for(int i = 0; i < instruction.operands.size; i++) {
            if(instruction.operands[i] instanceof IRConstantOperand && instruction.operands[i].type instanceof IRIntType
                && Integer.parseInt(instruction.operands[i].getValueString()) <= 0xFFFF) {
                return true;
            }
            // TODO: handle cases where immediate value is a float, or an array, or is larger then max immediate in mips
        }
        return false;
    }

    private boolean jType (IRInstruction instruction) {
        switch(instruction.opCode) {
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
}

    private int getFreeReg() {
        return firstFreeReg++;
    }