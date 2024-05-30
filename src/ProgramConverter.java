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
    private LinkedList<MIPSInstruction> instructions;
    private Map<String, Integer> labels;
    private boolean naive;


    private final int PCCounterSize = 4;


    public ProgramConverter(IRProgram IRProg, boolean naive) {
        this.IRProg = IRProg;
        instructionSet = new HashMap<>();
        instructions = new LinkedList<>();
        labels = new HashMap<>();
        this.naive = naive;
    }

    public LinkedList<MIPSInstruction> convertIRProg() {
        System.out.println("CONVERTING INSTRUCTIONS");
        convertInstructions();
        System.out.println("CONVERTED INSTRUCTIONS");
        System.out.println("PRINTING INSTRUCTIONS");
        printInstructions();
        System.out.println("PRINTED ALL INSTRUCTIONS");

        return instructions;


        // NAIVE ALLOCATION 3 STEPS:
        // 1) assign stack offsets to variables instead of registers (on a function level)
        // 2) modify instruction generation to load in source operands into t1, t2 (store calculated value in t0) and store t0 in stack offset for destination variables
        // 3) increase and decrease stack space 

        // improved algorithm - store variables in registers for their entire lifecycle
        // need to do live analysis 
    }


    private void convertInstructions() {
        InstructionConverter instructionConverter;
        if(naive) {
            instructionConverter = new InstructionConverter();
        } else {
            List<IRInstruction> headers = findHeaders();
            instructionConverter = new EnhancedInstructionConverter(headers);
        }
        int currIndex = 0;

        // add all functions
        for(IRFunction function : IRProg.functions) {
            instructionConverter.addFunction(function);
        }

        for(IRFunction function : IRProg.functions) {
            
            List<MIPSInstruction> headerInstruction = instructionConverter.convertHeader(function);

            for (MIPSInstruction mipsInstruction : headerInstruction) {
                if(!mipsInstruction.label.equals("")) {
                    labels.put(mipsInstruction.label, currIndex * PCCounterSize);
                }
                instructionSet.put(currIndex * PCCounterSize, mipsInstruction);
                instructions.add(mipsInstruction);
                currIndex ++;
            }


            for(IRInstruction instruction : function.instructions) {
                System.out.println("CONVERTING IR INSTRUCTION");
                printIRInstruction(instruction);
                List<MIPSInstruction> mipsInstructions = instructionConverter.convertInstruction(instruction);
                // add all created mips instructions to MIPS instruction set, update indices
                if(mipsInstructions != null) {
                    System.out.println("CONVERTED TO MIPS INSTRUCTIONS");
                    for (MIPSInstruction mipsInstruction : mipsInstructions) {   
                        if(!mipsInstruction.label.equals("")) {
                            labels.put(mipsInstruction.label, currIndex * PCCounterSize);
                        } 
                        System.out.println(mipsInstruction);
                        instructionSet.put(currIndex * PCCounterSize, mipsInstruction);
                        instructions.add(mipsInstruction);
                        currIndex ++;
                    }

                }
            }
            // after finishing converting a function add an exit instruction - if function is main add exit, else add jump return
            instructions.addAll(instructionConverter.addExit(function));
        }
    }

    

    private void printInstructions() {
        for(MIPSInstruction instruction : instructions) {
            System.out.println(instruction);
        }
    }

    private void printIRInstruction(IRInstruction instruction) {
        System.out.print(instruction.opCode);
        for(int i = 0; i < instruction.operands.length; i++) {
            System.out.print(" " + instruction.operands[i].toString());
        }
        System.out.println();
    }

    private List<IRInstruction> findHeaders() {
        List<IRInstruction> headers = new LinkedList<>();
        for(IRFunction function : IRProg.functions) {
            boolean firstInstruction = true;
            boolean prevBranch = false;
            for(IRInstruction instruction : function.instructions) {
                if(firstInstruction || prevBranch || isTarget(instruction)) {
                    headers.add(instruction);
                }
                firstInstruction = false;
                prevBranch = isBranch(instruction);
            }
        }
        return headers;

    }


    private boolean isTarget(IRInstruction instruction) {
        switch(instruction.opCode) {
            case LABEL:
                return true;
            default:
                return false;
        }
    }

    private boolean isBranch(IRInstruction instruction) {
        switch(instruction.opCode) {
            case BREQ:
            case BRNEQ:
            case BRLT:
            case BRGT:
            case BRLEQ:
            case BRGEQ:
                return true;
            default:
                return false;
        }
    }
}