import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;
import java.io.*;
import main.java.mips.*;
import main.java.mips.operand.*;

public class EnhancedInstructionConverter extends InstructionConverter {
    private HashMap<String, Register> varsInRegs;
    private List<IRInstruction> headers;
    private Queue<Register> totalRegisters;

    EnhancedInstructionConverter(List<IRInstruction> headers) {
        super();
        this.headers = headers;
        varsInRegs = new HashMap<>();
    }

    public List<MIPSInstruction> convertInstruction(IRInstruction instruction) {
        if(headers.contains(instruction)) {
            varsInRegs.clear();
        }
        return super.convertInstruction(instruction);
    }


    protected List<MIPSInstruction> variableToRegister(IROperand operand, Register storeReg) {
        // if the variable is already stored in a register grab it and move it to storeReg
        if(varsInRegs.get(operand.toString()) != null) {
            List<MIPSInstruction> returnList = new LinkedList<>();
            Register oldReg = varsInRegs.get(operand.toString());
            System.out.println("VARIABLE " + operand.toString() + " IS ALREADY STORED IN A REGISTER!");
            System.out.println("PREV REGISTER = " + oldReg.toString());
            System.out.println("NEW REGISTER = " + storeReg.toString());
            returnList.addAll(mipsCreator.createMove(storeReg, oldReg, getLabel()));
            // update varsInRegs to account for variable movement 
            varsInRegs.remove(operand.toString());
            // Iterate over the entries in the map removing the one associated with storeReg
            for (Iterator<Map.Entry<String, Register>> iterator = varsInRegs.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<String, Register> entry = iterator.next();
                if (entry.getValue().equals(storeReg)) {
                    // Remove the entry if its value matches register we are writing to and write it to stack
                    loadValueToStack(storeReg, variableStackOffset.get(entry.getKey()));
                    iterator.remove();
                    break;
                }
            }
            // PLACE NEW VALUE 
            varsInRegs.put(operand.toString(), storeReg);
            return returnList;
        } 
        // else run register allocation algorithm 
        varsInRegs.put(operand.toString(), storeReg);
        return super.variableToRegister(operand, storeReg);
    }






}