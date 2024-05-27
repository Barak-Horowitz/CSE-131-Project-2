import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;
import java.io.*;
import main.java.mips.*;
import main.java.mips.operand.*;


public class InstructionCreator {
    private final Register zeroReg = new Register("0");
    private final Register tempReg = new Register("at"); // USE ARGUMENTS REGISTER AS TEMPORARY FOR MULTS/DIVS
    private final Register returnReg = new Register("v0");
    private final Register argsReg = new Register("a0");
    private final String SbrkSyscallNum = "9";
    private final String printIntSyscallNum = "1";
    private final String printCharSyscallNum = "11";
    private final String getIntSyscallNum = "5";
    private final String getCharSyscallNum = "12";
    private final String offsetShift = "2";
    
    // ARITHMETIC OPERATIONS

    public List<MIPSInstruction> createMove(Register destReg, Register sourceReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction move = new MIPSInstruction(MIPSOp.MOVE, "", destReg, sourceReg);
        returnList.add(move);
        return returnList;
    }


    // R Type
    public List<MIPSInstruction> createAdd(Register destReg, Register sourceOneReg, Register sourceTwoReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADD, "", destReg, sourceOneReg, sourceTwoReg);
        returnList.add(add);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createAdd(Register destReg, Register sourceOneReg, Imm valTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, "", destReg, sourceOneReg, valTwo);
        returnList.add(add);
        return returnList;

        
    }

    // I Type
    public List<MIPSInstruction> createAdd(Register destReg, Imm valOne, Imm valTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction addOne = new MIPSInstruction(MIPSOp.ADDI, "", destReg, zeroReg, valOne);
        MIPSInstruction addTwo = new MIPSInstruction(MIPSOp.ADDI, "", destReg, destReg, valTwo);
        returnList.add(addOne);
        returnList.add(addTwo);
        return returnList;
    }

    // R Type 
    public List<MIPSInstruction> createSub(Register destReg, Register sourceOneReg, Register sourceTwoReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction sub = new MIPSInstruction(MIPSOp.SUB, "", destReg, sourceOneReg, sourceTwoReg);
        returnList.add(sub);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createSub(Register destReg, Register sourceOneReg, Imm valTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        valTwo.val = (valTwo.getInt() * - 1 + "");
        MIPSInstruction sub = new MIPSInstruction(MIPSOp.ADDI, "", destReg, sourceOneReg, valTwo);
        returnList.add(sub);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createSub(Register destReg, Imm valOne, Register sourceTwoReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, "", destReg, zeroReg, valOne);
        MIPSInstruction sub = new MIPSInstruction(MIPSOp.SUB, "", destReg, destReg, sourceTwoReg);
        returnList.add(add);
        returnList.add(sub);
        return returnList;
    }
    

    // I Type 
    public List<MIPSInstruction> createSub(Register destReg, Imm valOne, Imm valTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        valTwo.val = (valTwo.getInt() * - 1 + "");
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, "", destReg, zeroReg, valOne);
        MIPSInstruction sub = new MIPSInstruction(MIPSOp.ADDI, "", destReg, destReg, valTwo);
        returnList.add(add);
        returnList.add(sub);
        return returnList;
    }

    // R Type
    public List<MIPSInstruction> createMult(Register destReg, Register sourceOneReg, Register sourceTwoReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction mult = new MIPSInstruction(MIPSOp.MUL, "", destReg, sourceOneReg, sourceTwoReg);
        returnList.add(mult);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createMult(Register destReg, Register sourceOneReg, Imm valTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, "", destReg, zeroReg, valTwo);
        MIPSInstruction mult = new MIPSInstruction(MIPSOp.MUL, "", destReg, destReg, sourceOneReg);
        returnList.add(add);
        returnList.add(mult);
        return returnList;
    }

    // I Type 
    public List<MIPSInstruction> createMult(Register destReg, Imm valOne, Imm valTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction addFirst = new MIPSInstruction(MIPSOp.ADDI, "", destReg, zeroReg, valOne);
        MIPSInstruction addSecond = new MIPSInstruction(MIPSOp.ADDI, "", tempReg, zeroReg, valTwo);
        MIPSInstruction mult = new MIPSInstruction(MIPSOp.MUL, "", destReg, destReg, tempReg);
        returnList.add(addFirst);
        returnList.add(addSecond);
        returnList.add(mult);
        return returnList;
    }
    
    // R Type
    public List<MIPSInstruction> createDiv(Register destReg, Register sourceOneReg, Register sourceTwoReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction div = new MIPSInstruction(MIPSOp.DIV, "", destReg, sourceOneReg, sourceTwoReg);
        returnList.add(div);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createDiv(Register destReg, Register sourceOneReg, Imm valTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, "", destReg, zeroReg, valTwo); 
        MIPSInstruction div = new MIPSInstruction(MIPSOp.DIV, "", destReg, sourceOneReg, destReg);
        returnList.add(add);
        returnList.add(div);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createDiv(Register destReg, Imm valOne , Register sourceTwoReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, "", destReg, zeroReg, valOne);
        MIPSInstruction div = new MIPSInstruction(MIPSOp.DIV, "", destReg, destReg, sourceTwoReg);
        returnList.add(add);
        returnList.add(div);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createDiv(Register destReg, Imm valOne, Imm valTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction addFirst = new MIPSInstruction(MIPSOp.ADDI, "", destReg, zeroReg, valOne);
        MIPSInstruction addSecond = new MIPSInstruction(MIPSOp.ADDI, "", tempReg, zeroReg, valTwo);
        MIPSInstruction div = new MIPSInstruction(MIPSOp.DIV, "", destReg, destReg, tempReg);
        returnList.add(addFirst);
        returnList.add(addSecond);
        returnList.add(div);
        return returnList;
    }

    // R Type
    public List<MIPSInstruction> createAnd(Register destReg, Register sourceOneReg, Register sourceTwoReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction and = new MIPSInstruction(MIPSOp.AND, "", destReg, sourceOneReg, sourceTwoReg);
        returnList.add(and);
        return returnList;
    }
    
    // I Type
    public List<MIPSInstruction> createAnd(Register destReg, Register sourceOneReg, Imm valTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction and = new MIPSInstruction(MIPSOp.ANDI, "", destReg, sourceOneReg, valTwo);
        returnList.add(and);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createAnd(Register destReg, Imm valOne, Imm valTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, "", destReg, zeroReg, valTwo);
        MIPSInstruction and = new MIPSInstruction(MIPSOp.ANDI, "", destReg, destReg, valTwo);
        returnList.add(add);
        returnList.add(and);
        return returnList;
    }

    // R Type
    public List<MIPSInstruction> createOr(Register destReg, Register sourceOneReg, Register sourceTwoReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction or = new MIPSInstruction(MIPSOp.OR, "", destReg, sourceOneReg, sourceTwoReg);
        returnList.add(or);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createOr(Register destReg, Register sourceOneReg, Imm valTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction or = new MIPSInstruction(MIPSOp.ORI, "", destReg, sourceOneReg, valTwo);
        returnList.add(or);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createOr(Register destReg, Imm valOne, Imm valTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, "", destReg, zeroReg, valOne);
        MIPSInstruction or = new MIPSInstruction(MIPSOp.ORI, "", destReg, destReg, valTwo);
        returnList.add(add);
        returnList.add(or);
        return returnList;
    }

    // BRANCH OPERATIONS

    public List<MIPSInstruction> createLabel(String labelName) {
        // MIPS doesn't have a label instruction so we spoof one with a no-op
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction label = new MIPSInstruction(MIPSOp.ADD, labelName, zeroReg, zeroReg, zeroReg);
        returnList.add(label);
        return returnList;
    }

    public List<MIPSInstruction> createJump(Addr address) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction jump = new MIPSInstruction(MIPSOp.J, "", address);
        returnList.add(jump);
        return returnList;
    }

    public List<MIPSInstruction> createLinkedJump(Addr address) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction linkedJump = new MIPSInstruction(MIPSOp.JAL, "", address);
        returnList.add(linkedJump);
        return returnList;
    }

    public List<MIPSInstruction> createJumpReturn() {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction returnJump = new MIPSInstruction(MIPSOp.JR, "");
        returnList.add(returnJump);
        return returnList;
    }

    public List<MIPSInstruction> createBRNEQ(Addr address, Register sourceOneReg, Register sourceTwoReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BNE, "", sourceOneReg, sourceTwoReg, address);
        returnList.add(branch);
        return returnList;
    }
    
    public List<MIPSInstruction> createBRNEQ(Addr address, Register sourceOneReg, Imm immValTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createAdd(tempReg, zeroReg, immValTwo));
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BNE, "", sourceOneReg, tempReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBREQ(Addr address, Register sourceOneReg, Register sourceTwoReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BEQ, "", sourceOneReg, sourceTwoReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBREQ(Addr address, Register sourceOneReg, Imm immValTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createAdd(tempReg, zeroReg, immValTwo));
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BEQ, "", sourceOneReg, tempReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRLT(Addr address, Register sourceOneReg, Register sourceTwoReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, sourceOneReg, sourceTwoReg));
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BLT, "", tempReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRLT(Addr address, Register sourceOneReg, Imm immValTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, sourceOneReg, immValTwo));
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BLT, "", tempReg, address);
        returnList.add(branch);
        return returnList;
    }




    public List<MIPSInstruction> createBRLT(Addr address, Imm immValOne, Register sourceTwoReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, immValOne, sourceTwoReg));
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BLT, "", tempReg, address);
        returnList.add(branch);
        return returnList;
    }


    public List<MIPSInstruction> createBRGT(Addr address, Register sourceOneReg, Register sourceTwoReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, sourceOneReg, sourceTwoReg));
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BGT, "", tempReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRGT(Addr address, Register sourceOneReg, Imm immValTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, sourceOneReg, immValTwo));
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BGT, "", tempReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRGT(Addr address, Imm immValOne, Register sourceTwoReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, immValOne, sourceTwoReg));
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BGT, "", tempReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRGEQ(Addr address, Register sourceOneReg, Register sourceTwoReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, sourceOneReg, sourceTwoReg));
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BGE, "", tempReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRGEQ(Addr address, Register sourceOneReg, Imm immValTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, sourceOneReg, immValTwo));
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BGE, "", tempReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRGEQ(Addr address, Imm immValOne, Register sourceTwoReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, immValOne, sourceTwoReg));
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BGE, "", tempReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRLEQ (Addr address, Register sourceRegOne, Register sourceRegTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, sourceRegOne, sourceRegTwo));
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BLE, "", tempReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRLEQ (Addr address, Register sourceRegOne, Imm immValTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, sourceRegOne, immValTwo));
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BLE, "", tempReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRLEQ (Addr address, Imm immValOne, Register sourceRegTwo) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, immValOne, sourceRegTwo));
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BLE, "", tempReg, address);
        returnList.add(branch);
        return returnList;
    }


    public List<MIPSInstruction> createReturn(Register destReg, Register sourceReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createMove(destReg, sourceReg));
        returnList.addAll(createJumpReturn());
        return returnList;
    }

    public List<MIPSInstruction> createArray(Imm arraySize, Register arrayPtrReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        // store syscall num in return register 
        Imm sysCallNum = new Imm(SbrkSyscallNum, "DEC");
        returnList.addAll(createAdd(returnReg, zeroReg, sysCallNum));
        // move array size into argument register
        returnList.addAll(createAdd(argsReg, zeroReg, arraySize));
        // call sbrk 
        MIPSInstruction sbrk = new MIPSInstruction(MIPSOp.SYSCALL, "");
        returnList.add(sbrk);
        // move pointer to arrayPtr register
        returnList.addAll(createMove(arrayPtrReg, returnReg));
        return returnList;
    }

    public List<MIPSInstruction> createArrayLoad(Register arrayStoreReg, Register arrayPtrReg, Imm offsetImm) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        // store offset in assembler temporary
        returnList.addAll(createAdd(tempReg, zeroReg, offsetImm));
        // left shift  to account for number of bytes in int
        Imm shiftImm = new Imm(offsetShift, "DEC");
        MIPSInstruction sll = new MIPSInstruction(MIPSOp.SLL,"", tempReg, tempReg, shiftImm);
        returnList.add(sll);
        // add address of array to assembler temporary 
        returnList.addAll(createAdd(tempReg, tempReg, arrayPtrReg));
        MIPSInstruction arrayLoad = new MIPSInstruction(MIPSOp.LW, "", arrayStoreReg, tempReg);
        returnList.add(arrayLoad);
        return returnList;
    }

    public List<MIPSInstruction> createArrayLoad(Register arrayStoreReg, Register arrayPtrReg, Register offsetReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        //store offset in assembler temporary
        returnList.addAll(createMove(tempReg, offsetReg));
        // left shift to account for number of bytes in int
        Imm shiftImm  = new Imm(offsetShift, "DEC");
        MIPSInstruction sll = new MIPSInstruction(MIPSOp.SLL, "", tempReg, tempReg, shiftImm);
        // add address of array to assembler temporary
        returnList.addAll(createAdd(tempReg, tempReg, arrayPtrReg));
        MIPSInstruction arrayLoad = new MIPSInstruction(MIPSOp.LW, "", arrayStoreReg, tempReg);
        returnList.add(arrayLoad);
        return returnList;
    }

    public List<MIPSInstruction> createArrayStore(Register arrayStoreReg, Register arrayPtrReg, Imm offsetImm) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        // store offset in assembler temporary
        returnList.addAll(createAdd(tempReg, zeroReg, offsetImm));
        // left shift  to account for number of bytes in int
        Imm shiftImm = new Imm(offsetShift, "DEC");
        MIPSInstruction sll = new MIPSInstruction(MIPSOp.SLL,"", tempReg, tempReg, shiftImm);
        returnList.add(sll);
        // add address of array to assembler temporary 
        returnList.addAll(createAdd(tempReg, tempReg, arrayPtrReg));
        MIPSInstruction arrayStore = new MIPSInstruction(MIPSOp.SW, "", arrayStoreReg, tempReg);
        returnList.add(arrayStore);
        return returnList;
    }

    public List<MIPSInstruction> createArrayStore(Register arrayStoreReg, Register arrayPtrReg, Register offsetReg) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        //store offset in assembler temporary
        returnList.addAll(createMove(tempReg, offsetReg));
        // left shift to account for number of bytes in int
        Imm shiftImm  = new Imm(offsetShift, "DEC");
        MIPSInstruction sll = new MIPSInstruction(MIPSOp.SLL, "", tempReg, tempReg, shiftImm);
        // add address of array to assembler temporary
        returnList.addAll(createAdd(tempReg, tempReg, arrayPtrReg));
        MIPSInstruction arrayStore = new MIPSInstruction(MIPSOp.SW, "", arrayStoreReg, tempReg);
        returnList.add(arrayStore);
        return returnList;
    }

    public List<MIPSInstruction> createPUTI(Register printReg) {
        // store syscall number in return register
        List<MIPSInstruction> returnList = new LinkedList<>();
        Imm syscallNum = new Imm(printIntSyscallNum, "DEC");
        returnList.addAll(createAdd(returnReg, zeroReg, syscallNum));
        // store integer to print in argReg
        returnList.addAll(createMove(argsReg, printReg));
        // make syscall
        MIPSInstruction puti = new MIPSInstruction(MIPSOp.SYSCALL, "");
        returnList.add(puti);
        return returnList;

    }

    public List<MIPSInstruction> createPUTI(Imm printVal) {
                // store syscall number in return register
                List<MIPSInstruction> returnList = new LinkedList<>();
                Imm syscallNum = new Imm(printIntSyscallNum, "DEC");
                returnList.addAll(createAdd(returnReg, zeroReg, syscallNum));
                // store integer to print in argReg
                returnList.addAll(createAdd(argsReg, zeroReg, printVal));
                // make syscall
                MIPSInstruction puti = new MIPSInstruction(MIPSOp.SYSCALL, "");
                returnList.add(puti);
                return returnList;
        
    }


    public List<MIPSInstruction> createPUTC(Register printReg) {
                // store syscall number in return register
                List<MIPSInstruction> returnList = new LinkedList<>();
                Imm syscallNum = new Imm(printCharSyscallNum, "DEC");
                returnList.addAll(createAdd(returnReg, zeroReg, syscallNum));
                // store char to print in argReg
                returnList.addAll(createMove(argsReg, printReg));
                // make syscall
                MIPSInstruction putc = new MIPSInstruction(MIPSOp.SYSCALL, "");
                returnList.add(putc);
                return returnList;
    }

    public List<MIPSInstruction> createPUTC(Imm printVal) {
                // store syscall number in return register
                List<MIPSInstruction> returnList = new LinkedList<>();
                Imm syscallNum = new Imm(printCharSyscallNum, "DEC");
                returnList.addAll(createAdd(returnReg, zeroReg, syscallNum));
                // store char to print in argReg
                returnList.addAll(createAdd(argsReg, zeroReg, printVal));
                // make syscall
                MIPSInstruction putc = new MIPSInstruction(MIPSOp.SYSCALL, "");
                returnList.add(putc);
                return returnList;
    }

    public List<MIPSInstruction> createGETI(Register destReg) {
        // store syscall number in return register
        List<MIPSInstruction> returnList = new LinkedList<>();
        Imm syscallNum = new Imm(getIntSyscallNum, "DEC");
        returnList.addAll(createAdd(returnReg, zeroReg, syscallNum));
        // make syscall
        MIPSInstruction geti = new MIPSInstruction(MIPSOp.SYSCALL, "");
        returnList.add(geti);
        // move returned integer into destination
        returnList.addAll(createMove(destReg, returnReg));
        return returnList;
    }


    public List<MIPSInstruction> createGETC(Register destReg) {
        // store syscall number in return register
        List<MIPSInstruction> returnList = new LinkedList<>();
        Imm syscallNum = new Imm(getCharSyscallNum, "DEC");
        returnList.addAll(createAdd(returnReg, zeroReg, syscallNum));
        // make syscall
        MIPSInstruction getc = new MIPSInstruction(MIPSOp.SYSCALL, "");
        returnList.add(getc);
        // move returned integer into destination
        returnList.addAll(createMove(destReg, returnReg));
        return returnList;
    }

}