import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;
import java.io.*;
import main.java.mips.*;
import main.java.mips.operand.*;
public class MipsCompiler {
    public static void main(String[] args) throws Exception {
        if(args[1].equals("--naive")) {
            boolean naive = true;
        } else if (args[1].equals("--greedy")){
            boolean naive = false;
        } else {
            System.out.println("ERROR MUST PASS IN EITHER --naive OR --greedy AS FLAG");
        }

        // file reader reads a text file containing text code and generates datastructure for the program
        System.out.println("READING IN IR PROGRAM");
        IRReader file_reader = new IRReader();
        IRProgram program = file_reader.parseIRFile(args[0]);
        System.out.println("SUCCESSFULLY READ IR PROGRAM");

        // initialize the converter to convert IR PROG to MIPS PROG
        System.out.println("CONVERTING IR PROGRAM TO MIPS");
        ProgramConverter IRToMips = new ProgramConverter(program);
        // grab the converted MIPS PROG
        LinkedList<MIPSInstruction> convertedProg = IRToMips.convertIRProg();

        // write the mips prog out to a file
        String fileName = "out.s";
        try {
            FileWriter fileWriter = new FileWriter(fileName, false);
            // truncate out.s
            fileWriter.write(".text\n");
            fileWriter.write("j main\n");
            for(MIPSInstruction instruction : convertedProg) {
                if (!instruction.label.equals("")) {
                    fileWriter.write(instruction.label + ":" + "\n");
                    instruction.label = "";
                } else {
                    fileWriter.write(instruction.toString() + "\n");
                }
            }
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("ERROR WHEN WRITING TO FILE: " + e.getMessage());
        }
        System.out.println("SUCCESSFULLY CONVERTED IR PROGRAM TO MIPS");
    }
}