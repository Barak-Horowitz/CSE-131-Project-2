import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;
import java.io.*;
import main.java.mips.*;
import main.java.mips.operand.*;


public class ProgramConverter {
    private IRProgram IRProg;
    private HashMap<Integer, MIPSInstruction> instructionSet;

    private final int PCCounterSize = 4;


    public ProgramConverter(IRProgram IRProg) {
        this.IRProg = IRProg;
        instructionSet = new HashMap<>();
    }

    public MIPSProgram convertIRProg() {
        System.out.println("CONVERTING INSTRUCTIONS");
        convertInstructions();
        System.out.println("CONVERTED INSTRUCTIONS");
        System.out.println("PRINTING INSTRUCTIONS");
        printInstructions();
        System.out.println("PRINTED ALL INSTRUCTIONS");
        convertRegisters();
        return null;
    }

    private void convertInstructions() {
        InstructionConverter instructionConverter = new InstructionConverter();
        int currIndex = 0;

        for(IRFunction function : IRProg.functions) {
            
            List<MIPSInstruction> headerInstruction = instructionConverter.convertHeader(function);
            
            for (MIPSInstruction mipsInstruction : headerInstruction) {
                instructionSet.put(currIndex * PCCounterSize, mipsInstruction);
                currIndex ++;
            }


            for(IRInstruction instruction : function.instructions) {
                List<MIPSInstruction> mipsInstructions = instructionConverter.convertInstruction(instruction);
                // add all created mips instructions to MIPS instruction set, update indices
                for (MIPSInstruction mipsInstruction : mipsInstructions) {        
                    instructionSet.put(currIndex * PCCounterSize, mipsInstruction);
                    currIndex ++;
                }

            }

            // clear function map after finishing with function conversion
            instructionConverter.clearFuncMap();
     
        }

    }
    
    private void convertRegisters() {
        return;
    }

    private void printInstructions() {
        for(MIPSInstruction instruction : instructionSet.values()) {
            System.out.println(instruction);
        }
    }
}