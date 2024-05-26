import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;
import java.io.*;
import main.java.mips.*;
import main.java.mips.operand.*;

public class MipsCompiler {
    public static void main(String[] args) throws Exception {

        // file reader reads a text file containing text code and generates datastructure for the program
        IRReader file_reader = new IRReader();
        IRProgram program = file_reader.parseIRFile(args[0]);

        // initialize the converter to convert IR PROG to MIPS PROG
        ProgramConverter IRToMips = new ProgramConverter(program);

        // grab the converted MIPS PROG
        MIPSProgram convertedProg = IRToMips.convertIRProg();

    }
}