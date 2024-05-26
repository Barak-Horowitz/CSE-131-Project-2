import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;
import java.io.*;
import mips.*;

public class ProgramConverter {
    private IRProgram IRProg;
    private HashMap<Integer, MIPSInstruction> instructionSet;

    private final int PCCounterSize = 4


    public ProgramConverter(IRPRogram IRProg) {
        this.IRProg = IRProg;
        instructionSet = new HashMap<>():
    }

    public MIPSProgram convertIRProg() {
        convertInstructions();
        convertRegisters();
        return;
    }

    convertInstructions() {
        InstructionConverter instructionConverter = new InstructionConverter();
        int currIndex = 0;

        for(IRFunction function : IRProg.functions) {
            
            List<MIPSInstruction> headerInstruction = instructionConverter.convertHeader(function);
            
            for (MIPSInstruction mipsInstruction : headerInstruction) {
                mipsInstructionSet.add(currIndex * PCCounterSize, mipsInstruction)
                currIndex ++;
            }


            for(IRInstruction instruction : function.instructions) {
                List<MIPSInstruction> mipsInstructions = instructionConverter.convertInstruction(instruction);
                // add all created mips instructions to MIPS instruction set, update indices
                for (MIPSInstruction mipsInstruction : mipsInstructions) {        
                    mipsInstructionSet.add(currIndex * PCCounterSize, mipsInstruction)
                    currIndex ++;
                }

            }

            // clear function map after finishing with function conversion
            instructionConverter.clearFuncMap();
     
        }

    }
    
    private void convertRegisters() {
        return null
    }
}