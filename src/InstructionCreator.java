import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;
import java.io.*;
import mips.*;


public class InstructionCreator {
    // TODO: fill in creation methods
    
    // ARITHMETIC OPERATIONS

    public List<IRInstruction> createMove(Register destReg, Register sourceReg) {
        return null;
    }


    // R Type
    public List<IRInstruction> createAdd(Register destReg, Register sourceOneReg, Register sourceTwoReg) {
        return null;
    }

    // I Type
    public List<IRInstruction> createAdd(Register destReg, Register sourceOneReg, Imm valTwo) {
        return null;
    }

    // I Type
    public List<IRInstruction> createAdd(Register destReg, Imm valOne, Imm valTwo) {
        return null;
    }

    // R Type 
    public List<IRInstruction> createSub(Register destReg, Register sourceOneReg, Register sourceTwoReg) {
        return null;
    }

    // I Type
    public List<IRInstruction> createSub(Register destReg, Register sourceOneReg, Imm valTwo) {
        return null;
    }

    // I Type
    public List<IRInstruction> createSub(Register destReg, Imm valOne, Register sourceTwoReg) {
        return null;
    }
    

    // I Type 
    public List<IRInstruction> createSub(Register destReg, Imm valOne, Imm valTwo) {
        return null;

    }

    // R Type
    // TODO: figure out if multiplications can have immediates in tiger!
    public List<IRInstruction> createMult(Register destReg, Register sourceOneReg, Register sourceTwoReg) {
        return null;
    }
    
    // R Type
    // TODO: figure out if divisions can have immediates in tiger!
    public List<IRInstruction> createDiv(Register destReg, Register sourceOneReg, Register sourceTwoReg) {
        return null;
    }

    // R Type
    public List<IRInstruction> createAnd(Register destReg, Register sourceOneReg, Register sourceTwoReg) {
        return null;
    }
    
    // I Type
    public List<IRInstruction> createAnd(Register destReg, Register sourceOneReg, Imm valTwo) {
        return null;
    }

    // I Type
    public List<IRInstruction> createAnd(Register destReg, Imm valOne, Imm valTwo) {
        return null;
    }

    // R Type
    public List<IRInstruction> createOr(Register destReg, Register sourceOneReg, Register sourceTwoReg) {
        return null;
    }

    // I Type
    public List<IRInstruction> createOr(Register destReg, Register sourceOneReg, Imm valTwo) {
        return null;
    }

    // I Type
    public List<IRInstruction> createOr(Register destReg, Imm valOne, Imm valTwo) {
        return null;
    }

    // BRANCH OPERATIONS

    public List<IRInstruction> createLabel(String labelName) {
        return null;
    }

    public List<IRInstruction> createJump(Addr address) {
        return null;
    }

    public List<IRInstruction> createLinkedJump(Addr address) {
        return null;
    }

    public List<IRInstruction> createJumpReturn() {
        return null;
    }

    public List<IRInstruction> createBRNEQ(Addr address, Register sourceOneReg, Register sourceTwoReg) {
        return null;
    }

    public List<IRInstruction> createBREQ(Addr address, Register sourceOneReg, Register sourceTwoReg) {
        return null;
    }

    public List<IRInstruction> createBRLT(Addr address, Register sourceOneReg, Register sourceTwoReg) {
        return null;
    }

    public List<IRInstruction> createBRGT(Addr address, Register sourceOneReg, Register sourceTwoReg) {
        return null;
    }

    public List<IRInstruction> createBRGEQ(Addr address, Register sourceOneReg, Register sourceTwoReg) {
        return null;
    }

    public List<IRInstruction> createReturn(Register destReg, Register sourceReg) {
        return null;
    }




}