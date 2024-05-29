import ir.*;
import ir.datatype.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;
import java.io.*;
import main.java.mips.*;
import main.java.mips.operand.*;

public class RegInstructionPair {
    public List<MIPSInstruction> instructions;
    public Register register;
    public RegInstructionPair() {
        instructions = null;
        register = null;
    }

}