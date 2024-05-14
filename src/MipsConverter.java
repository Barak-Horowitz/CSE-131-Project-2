import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;
import java.io.*;
import mips.*;

public class MipsConverter {
    public static void main(String[] args) throws Exception {
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
        switch(instruction.opCode) {
            case(ASSIGN):
            case(ADD):
            case(SUB):
            case(MULT):
            case(DIV):
            case(AND):
            case(OR):
                return convertAlgebraicOperation(instruction);
            case(GOTO):
            case(BREQ):
            case(BRNEQ):
            case(BRLT):
            case(BRGT):
            case(BRLEQ):
            case(BRGEQ):
            case(RETURN):
            case(CALL):
            case(CALLR):
                return convertJumpOperation(instruction);
            default:
                return convertDataOperation(instruction);
        }
    }

    private List<MIPSInstruction> convertAlgebraicOperation(IRInstruction instruction) {
        if(iType(instruction)) {

        }
        else {

        }

    }

    private List<MIPSInstruction> convertJumpOperation(IRInstruction instruction) {


    }

    private List<MIPSInstruction> convertDataOperation(IRInstruction instruction) {
        if(iType(instruction)) {

        }
        else {

        }

    }

    private boolean iType (IRInstruction instruction) {

    }

}