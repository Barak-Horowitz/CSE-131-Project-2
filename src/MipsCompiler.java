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

public class MipsCompiler {
    public static void main(String[] args) throws Exception {

        // file reader reads a text file containing text code and generates datastructure for the program
        IRReader file_reader = new IRReader();
        IRProgram program = file_reader.parseIRFile(args[0]);

        // initialize the converter to convert IR PROG to MIPS PROG
        InstructionConverter IRToMips = new ProgramConverter(program);

        // grab the converted MIPS PROG
        MIPSProgram convertedProg = ProgramConverter.convertIRProg();

    }
}