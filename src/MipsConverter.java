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
        firstFreeReg = 0;
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
            case SUB:
                MIPSInstruction convertedInstruction = new MIPSInstruction(MipsOp.SUB, "", opZero, opOne, opTwo);
                returnList.add(convertedInstruction)
            case MULT:
                MIPSInstruction convertedInstruction = new MIPSInstruction(MipsOp.MUL, "", opZero, opOne, opTwo);
                returnList.add(convertedInstruction)
            case DIV:
                MIPSInstruction convertedInstruction = new MIPSInstruction(MipsOp.DIV, "", opZero, opOne, opTwo);
                returnList.add(convertedInstruction)
            case AND:
                MIPSInstruction convertedInstruction = new MIPSInstruction(MipsOp.AND, "", opZero, opOne, opTwo);
                returnList.add(convertedInstruction)
            case OR:
                MIPSInstruction convertedInstruction = new MIPSInstruction(MipsOp.OR, "", opZero, opOne, opTwo);
                returnList.add(convertedInstruction)
        }
        return returnList;


      

    }

    // given an instruction operating on variables and immediates, converts it to the appropriate I type MIPS instructions
    private List<MIPSInstruction> convertIType(IRInstruction instruction) {
        return null;

    }

    // given a branch instruction converts it to the appropriate R type MIPS instruction
    private List<MIPSInstruction> convertJType(IRInstruction instruction) {
        return null;

    }

    // if instruction has any numbers in its operands instead of variables it is an i-type instruction
    private boolean iType (IRInstruction instruction) {
        for(int i = 0; i < instruction.operands.size; i++) {
            if(instruction.operands[i] instanceof IRConstantOperand) {
                return true;
            }
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