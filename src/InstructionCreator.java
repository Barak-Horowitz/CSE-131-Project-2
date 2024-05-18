import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;
import java.io.*;
import mips.*;


public class InstructionCreator {
    private final Register zeroReg = new Register(0);
    private final Register tempReg = new Register(4); // USE ARGUMENTS REGISTER AS TEMPORARY FOR MULTS/DIVS
    // TODO: fill in creation methods
    
    // ARITHMETIC OPERATIONS

    public List<IRInstruction> createMove(Register destReg, Register sourceReg) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction move = new MIPSInstruction(MIPSOp.MOVE, "", destReg, sourceReg);
        returnList.add(move)
        return returnList;
    }


    // R Type
    public List<IRInstruction> createAdd(Register destReg, Register sourceOneReg, Register sourceTwoReg) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADD, destReg, sourceOneReg, sourceTwoReg);
        returnList.add(add);
        return returnList;
    }

    // I Type
    public List<IRInstruction> createAdd(Register destReg, Register sourceOneReg, Imm valTwo) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, destReg, sourceOneReg, valTwo);
        returnList.add(add);
        return returnList;

        
    }

    // I Type
    public List<IRInstruction> createAdd(Register destReg, Imm valOne, Imm valTwo) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction addOne = new MIPSInstruction(MIPSOp.ADDI, destReg, zeroReg, valOne);
        MIPSInstruction addTwo = new MIPSInstruction(MIPSOp.AddI, destReg, destReg, valTwo);
        returnList.add(addOne);
        returnList.add(addTwo);
        return returnList;
    }

    // R Type 
    public List<IRInstruction> createSub(Register destReg, Register sourceOneReg, Register sourceTwoReg) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction sub = new MIPSInstruction(MIPSOp.SUB, destReg, sourceOneReg, sourceTwoReg);
        returnList.add(sub);
        return returnList;
    }

    // I Type
    public List<IRInstruction> createSub(Register destReg, Register sourceOneReg, Imm valTwo) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction sub = new MIPSInstruction(MIPSOp.ADDI, destReg, sourceOneReg, valTwo);
        returnList.add(sub);
        return returnList;
    }

    // I Type
    public List<IRInstruction> createSub(Register destReg, Imm valOne, Register sourceTwoReg) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, destReg, zeroReg, valOne);
        MIPSInstruction sub = new MIPSInstruction(MIPSOp.SUB, destReg, destReg, sourceTwoReg);
        returnList.add(add);
        returnList.add(sub);
        return returnList;
    }
    

    // I Type 
    public List<IRInstruction> createSub(Register destReg, Imm valOne, Imm valTwo) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, destReg, zeroReg, valOne);
        MIPSInstruction sub = new MIPSInstruction(MIPSOp.ADDI, destReg, destReg, valTwo);
        returnList.add(add);
        returnList.add(sub);
        return returnList;
    }

    // R Type
    // TODO: figure out if multiplications can have immediates in tiger!
    public List<IRInstruction> createMult(Register destReg, Register sourceOneReg, Register sourceTwoReg) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction mult = new MIPSInstruction(MIPSOp.MULT, destReg, sourceOneReg, sourceTwoReg);
        returnList.add(mult);
        return returnList;
    }

    // I Type
    public List<IRInstruction> createMult(Register destReg, Register sourceOneReg, Imm valTwo) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, destReg, zeroReg, valTwo);
        MIPSInstruction mult = new MIPSInstruction(MIPSOp.MULT, destReg, destReg, sourceOneReg);
        returnList.add(mult);
        return returnList;
    }

    // I Type 
    public List<IRInstruction> createMult(Register destReg, Imm valOne, Imm valTwo) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction addFirst = new MIPSInstruction(MIPSOp.ADDI, destReg, zeroReg, valOne);
        MIPSInstruction addSecond = new MIPSInstruction(MIPSOp.ADDI, tempReg, zeroReg, valTwo);
        MIPSInstruction mult = new MIPSInstruction(MIPSOp.MULT, destReg, destReg, tempReg);
        returnList.add(mult);
    }
    
    // R Type
    public List<IRInstruction> createDiv(Register destReg, Register sourceOneReg, Register sourceTwoReg) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction div = new MIPSInstruction(MIPSOp.DIV, destReg, sourceOneReg, sourceTwoReg);
        returnList.add(div);
        return returnList;
    }

    // I Type
    public List<IRInstruction> createDiv(Register destReg, Register sourceOneReg, Imm valTwo) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, destReg, zeroReg, valTwo); 
        MIPSInstruction div = new MIPSInstruction(MIPSOp.DIV, destReg, sourceOneReg, destReg);
        returnList.add(add);
        returnList.add(div);
        return returnList;
    }

    // I Type
    public List<IRInstruction> createDiv(Register destReg, Imm valOne , Register sourceTwoReg) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, destReg, zeroReg, valOne)
        MIPSInstruction div = new MIPSInstruction(MIPSOp.DIV, destReg, destReg, sourceTwoReg);
        returnList.add(add);
        returnList.add(div);
        return returnList;
    }

    // I Type
    public List<IRInstruction> createDiv(Register destReg, Imm valOne, Imm valTwo) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction addFirst = new MIPSInstruction(MIPSOp.ADDI, destReg, zeroReg, valOne);
        MIPSInstruction addSecond = new MIPSInstruction(MIPSOp.ADDI, tempReg, zeroReg, valTwo);
        MIPSInstruction div = new MIPSInstruction(MIPSOp.DIV, destReg, destReg, tempReg);
        returnList.add(addFirst);
        returnList.add(addSecond);
        returnList.add(div);
        return returnList;
    }

    // R Type
    public List<IRInstruction> createAnd(Register destReg, Register sourceOneReg, Register sourceTwoReg) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction and = new MIPSInstruction(MIPSOp.AND, destReg, sourceOneReg, sourceTwoReg);
        returnList.add(and);
        return returnList;
    }
    
    // I Type
    public List<IRInstruction> createAnd(Register destReg, Register sourceOneReg, Imm valTwo) {
        List<IRInstruction> returnList = new LinkedList<();
        mipsInstruction and = new MIPSInstruction(MIPSOp.ANDI, destReg, sourceOneReg, valTwo);
        returnList.add(and);
        return returnList;
    }

    // I Type
    public List<IRInstruction> createAnd(Register destReg, Imm valOne, Imm valTwo) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, destReg, zeroReg, valTwo);
        MIPSInstruction and = new MIPSInstruction(MIPSOp.ANDI, destReg, destReg, valTwo);
        returnList.add(add);
        returnList.add(and);
        return returnList;
    }

    // R Type
    public List<IRInstruction> createOr(Register destReg, Register sourceOneReg, Register sourceTwoReg) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction or = new MIPSInstruction(MIPSOp.OR, destReg, sourceOneReg, sourceTwoReg);
        returnList.add(or)
        return returnList;
    }

    // I Type
    public List<IRInstruction> createOr(Register destReg, Register sourceOneReg, Imm valTwo) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction or = new MIPSInstruction(MIPSOp.ORI destReg, sourceOneReg, valTwo);
        returnList.add(or);
        return returnList;
    }

    // I Type
    public List<IRInstruction> createOr(Register destReg, Imm valOne, Imm valTwo) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, destReg, zeroReg, valOne);
        MIPSInstruction or = new MIPSInstruction(MIPSOp.ORI, destReg, destReg, valTwo);
        returnList.add(add);
        returnList.add(or);
        return returnList;
    }

    // BRANCH OPERATIONS

    public List<IRInstruction> createLabel(String labelName) {
        // MIPS doesn't have a label instruction so we spoof one with a no-op
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction label = new MIPSInstruction(MIPSOp.ADD,labelName, zeroReg, zeroReg, zeroReg);
        returnList.add(label);
        return returnList;
    }

    public List<IRInstruction> createJump(Addr address) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction jump = new MIPSInstruction(MIPSOp.J, "", address);
        returnList.add(jump);
        return returnList;
    }

    public List<IRInstruction> createLinkedJump(Addr address) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction linkedJump = new MIPSInstruction(MIPSOp.JAL, "", address);
        returnList.add(linkedJump);
        return returnList;
    }

    public List<IRInstruction> createJumpReturn() {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction returnJump = new MIPSInstruction(MIPSOp.JR, "", address);
        returnList.add(returnJump);
        return returnList;
    }

    public List<IRInstruction> createBRNEQ(Addr address, Register sourceOneReg, Register sourceTwoReg) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BNE, "", sourceOneReg, sourceTwoReg);
        returnList.add(branch)
        return returnList;
    }

    public List<IRInstruction> createBREQ(Addr address, Register sourceOneReg, Register sourceTwoReg) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BEQ, "", sourceOneReg, sourceTwoReg);
        returnList.add(branch);
        return returnList;
    }

    public List<IRInstruction> createBRLT(Addr address, Register sourceOneReg, Register sourceTwoReg) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BLT, "". sourceOneReg, sourceTwoReg);
        returnList.add(branch);
        return returnList;
    }

    public List<IRInstruction> createBRGT(Addr address, Register sourceOneReg, Register sourceTwoReg) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BGT, "" sourceOneReg, sourceTwoReg);
        returnList.add(branch);
        return returnList;
    }

    public List<IRInstruction> createBRGEQ(Addr address, Register sourceOneReg, Register sourceTwoReg) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BGE, "", sourceOneReg, sourceTwoReg);
        returnList.add(branch);
        return returnList;
    }

    public List<IRInstruction> createReturn(Register destReg, Register sourceReg) {
        List<IRInstruction> returnList = new LinkedList<();
        MIPSInstruction move = createMove(destReg, sourceReg);
        MIPSInstruction returnJump = createJumpReturn();
        returnList.add(move);
        returnList.add(returnJump);
        return returnList;
    }
    
}