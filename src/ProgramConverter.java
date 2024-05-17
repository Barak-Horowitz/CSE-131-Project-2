import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;
import java.io.*;
import mips.*;

public class ProgramConverter {
    private IRProgram IRProg;
    private HashMap<Integer, MIPSInstruction> instructionSet;


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
        int begIndex = 0;

        for(IRFunction function : IRProg.functions) {
            
            boolean firstInstruction = true;

            for(IRInstruction instruction : function.instructions) {
        
                List<MIPSInstruction> mipsInstructions;
                
                if(firstInstruction) {
        
                    begIndex = currIndex;
                    convertHeader(instruction);
                    firstInstruction = false;

                } else {
        
                    List<MIPSInstruction> mipsInstructions = instructionConverter.convertInstruction(instruction);
        
                }

                // add all created mips instructions to MIPS instruction set, update indices
                for (MIPSInstruction mipsInstruction : mipsInstructions) {
        
                    mipsInstructionSet.add(currIndex, mipsInstruction)
                    currIndex ++;
        
                }

            }

            List<MIPSInstruction> footerInstructions = convertFooter(begIndex);
            
            // add all footer instructions to instruction set updating indices
            for(MIPSInstruction mipsInstruction : footerInstructions) {

                InstructionSet.add(currIndex, mipsInstruction);
                currIndex ++;

            }
            // clear function map after finishing with function conversion
            instructionConverter.clearFuncMap();

            
        }

    }
    
    private void convertRegisters() {
        return null
    }

    private List<MIPSInstruction> convertHeader(IRInstruction header) {
        return null;
    }

    private List<MIPSInstruction> convertFooter (int index) {
        return null;
    }
}