import ir.*;
import ir.operand.*;
import ir.IRInstruction.OpCode;
import java.util.*;
import java.io.*;
import main.java.mips.*;
import main.java.mips.operand.*;


public class InstructionCreator {
    private final Register zeroReg = new Register("$0");
    private final Register tempReg = new Register("$t0"); // USE ARGUMENTS REGISTER AS TEMPORARY FOR MULTS/DIVS
    private final Register returnReg = new Register("$v0");
    private final Register argsReg = new Register("$a0");
    private final Register jumpReturnReg = new Register("$ra");
    private final String SbrkSyscallNum = "9";
    private final String printIntSyscallNum = "1";
    private final String printCharSyscallNum = "11";
    private final String getIntSyscallNum = "5";
    private final String getCharSyscallNum = "12";
    private final String exitSyscallNum = "10";
    private final String offsetShift = "2";
    
    // ARITHMETIC OPERATIONS

    public List<MIPSInstruction> createMove(Register destReg, Register sourceReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction move = new MIPSInstruction(MIPSOp.MOVE, label, destReg, sourceReg);
        returnList.add(move);
        return returnList;
    }


    // R Type
    public List<MIPSInstruction> createAdd(Register destReg, Register sourceOneReg, Register sourceTwoReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADD, label, destReg, sourceOneReg, sourceTwoReg);
        returnList.add(add);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createAdd(Register destReg, Register sourceOneReg, Imm valTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, label, destReg, sourceOneReg, valTwo);
        returnList.add(add);
        return returnList;

        
    }

    // I Type
    public List<MIPSInstruction> createAdd(Register destReg, Imm valOne, Imm valTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction addOne = new MIPSInstruction(MIPSOp.ADDI, label, destReg, zeroReg, valOne);
        label = "";
        MIPSInstruction addTwo = new MIPSInstruction(MIPSOp.ADDI, label, destReg, destReg, valTwo);
        returnList.add(addOne);
        returnList.add(addTwo);
        return returnList;
    }

    // R Type 
    public List<MIPSInstruction> createSub(Register destReg, Register sourceOneReg, Register sourceTwoReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction sub = new MIPSInstruction(MIPSOp.SUB, label, destReg, sourceOneReg, sourceTwoReg);
        returnList.add(sub);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createSub(Register destReg, Register sourceOneReg, Imm valTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        Imm valTwoNeg = new Imm(valTwo.getInt() * - 1 + "", "DEC");
        MIPSInstruction sub = new MIPSInstruction(MIPSOp.ADDI, label, destReg, sourceOneReg, valTwoNeg);
        returnList.add(sub);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createSub(Register destReg, Imm valOne, Register sourceTwoReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, label, destReg, zeroReg, valOne);
        label = "";
        MIPSInstruction sub = new MIPSInstruction(MIPSOp.SUB, label, destReg, destReg, sourceTwoReg);
        returnList.add(add);
        returnList.add(sub);
        return returnList;
    }
    

    // I Type 
    public List<MIPSInstruction> createSub(Register destReg, Imm valOne, Imm valTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        Imm valTwoNeg = new Imm(valTwo.getInt() * - 1 + "", "DEC");
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, label, destReg, zeroReg, valOne);
        label = "";
        MIPSInstruction sub = new MIPSInstruction(MIPSOp.ADDI, label, destReg, destReg, valTwo);
        returnList.add(add);
        returnList.add(sub);
        return returnList;
    }

    // R Type
    public List<MIPSInstruction> createMult(Register destReg, Register sourceOneReg, Register sourceTwoReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction mult = new MIPSInstruction(MIPSOp.MUL, label, destReg, sourceOneReg, sourceTwoReg);
        returnList.add(mult);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createMult(Register destReg, Register sourceOneReg, Imm valTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, label, destReg, zeroReg, valTwo);
        label = "";
        MIPSInstruction mult = new MIPSInstruction(MIPSOp.MUL, label, destReg, destReg, sourceOneReg);
        returnList.add(add);
        returnList.add(mult);
        return returnList;
    }

    // I Type 
    public List<MIPSInstruction> createMult(Register destReg, Imm valOne, Imm valTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction addFirst = new MIPSInstruction(MIPSOp.ADDI, label, destReg, zeroReg, valOne);
        label = "";
        MIPSInstruction addSecond = new MIPSInstruction(MIPSOp.ADDI, label, tempReg, zeroReg, valTwo);
        MIPSInstruction mult = new MIPSInstruction(MIPSOp.MUL, label, destReg, destReg, tempReg);
        returnList.add(addFirst);
        returnList.add(addSecond);
        returnList.add(mult);
        return returnList;
    }
    
    // R Type
    public List<MIPSInstruction> createDiv(Register destReg, Register sourceOneReg, Register sourceTwoReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction div = new MIPSInstruction(MIPSOp.DIV, label, destReg, sourceOneReg, sourceTwoReg);
        returnList.add(div);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createDiv(Register destReg, Register sourceOneReg, Imm valTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, label, destReg, zeroReg, valTwo); 
        label = "";
        MIPSInstruction div = new MIPSInstruction(MIPSOp.DIV, label, destReg, sourceOneReg, destReg);
        returnList.add(add);
        returnList.add(div);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createDiv(Register destReg, Imm valOne , Register sourceTwoReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, label, destReg, zeroReg, valOne);
        label = "";
        MIPSInstruction div = new MIPSInstruction(MIPSOp.DIV, label, destReg, destReg, sourceTwoReg);
        returnList.add(add);
        returnList.add(div);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createDiv(Register destReg, Imm valOne, Imm valTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction addFirst = new MIPSInstruction(MIPSOp.ADDI, label, destReg, zeroReg, valOne);
        label = "";
        MIPSInstruction addSecond = new MIPSInstruction(MIPSOp.ADDI, label, tempReg, zeroReg, valTwo);
        MIPSInstruction div = new MIPSInstruction(MIPSOp.DIV, label, destReg, destReg, tempReg);
        returnList.add(addFirst);
        returnList.add(addSecond);
        returnList.add(div);
        return returnList;
    }

    // R Type
    public List<MIPSInstruction> createAnd(Register destReg, Register sourceOneReg, Register sourceTwoReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction and = new MIPSInstruction(MIPSOp.AND, label, destReg, sourceOneReg, sourceTwoReg);
        returnList.add(and);
        return returnList;
    }
    
    // I Type
    public List<MIPSInstruction> createAnd(Register destReg, Register sourceOneReg, Imm valTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction and = new MIPSInstruction(MIPSOp.ANDI, label, destReg, sourceOneReg, valTwo);
        returnList.add(and);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createAnd(Register destReg, Imm valOne, Imm valTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, label, destReg, zeroReg, valTwo);
        label = "";
        MIPSInstruction and = new MIPSInstruction(MIPSOp.ANDI, label, destReg, destReg, valTwo);
        returnList.add(add);
        returnList.add(and);
        return returnList;
    }

    // R Type
    public List<MIPSInstruction> createOr(Register destReg, Register sourceOneReg, Register sourceTwoReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction or = new MIPSInstruction(MIPSOp.OR, label, destReg, sourceOneReg, sourceTwoReg);
        returnList.add(or);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createOr(Register destReg, Register sourceOneReg, Imm valTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction or = new MIPSInstruction(MIPSOp.ORI, label, destReg, sourceOneReg, valTwo);
        returnList.add(or);
        return returnList;
    }

    // I Type
    public List<MIPSInstruction> createOr(Register destReg, Imm valOne, Imm valTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction add = new MIPSInstruction(MIPSOp.ADDI, label, destReg, zeroReg, valOne);
        label = "";
        MIPSInstruction or = new MIPSInstruction(MIPSOp.ORI, label, destReg, destReg, valTwo);
        returnList.add(add);
        returnList.add(or);
        return returnList;
    }

    // BRANCH OPERATIONS
    public List<MIPSInstruction> createJump(Addr address, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction jump = new MIPSInstruction(MIPSOp.J, label, address);
        returnList.add(jump);
        return returnList;
    }

    public List<MIPSInstruction> createLinkedJump(Addr address, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction linkedJump = new MIPSInstruction(MIPSOp.JAL, label, address);
        returnList.add(linkedJump);
        return returnList;
    }

    public List<MIPSInstruction> createLabel(String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction newLabel = new MIPSInstruction(MIPSOp.ADD, label, zeroReg, zeroReg, zeroReg);
        returnList.add(newLabel);
        return returnList;
    }
    public List<MIPSInstruction> createJumpReturn(String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction returnJump = new MIPSInstruction(MIPSOp.JR, label, jumpReturnReg);
        returnList.add(returnJump);
        return returnList;
    }

    public List<MIPSInstruction> createBRNEQ(Addr address, Register sourceOneReg, Register sourceTwoReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BNE, label, sourceOneReg, sourceTwoReg, address);
        returnList.add(branch);
        return returnList;
    }
    
    public List<MIPSInstruction> createBRNEQ(Addr address, Register sourceOneReg, Imm immValTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createAdd(tempReg, zeroReg, immValTwo, label));
        label = "";
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BNE, label, sourceOneReg, tempReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBREQ(Addr address, Register sourceOneReg, Register sourceTwoReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BEQ, label, sourceOneReg, sourceTwoReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBREQ(Addr address, Register sourceOneReg, Imm immValTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createAdd(tempReg, zeroReg, immValTwo, label));
        label = "";
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BEQ, label, sourceOneReg, tempReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRLT(Addr address, Register sourceOneReg, Register sourceTwoReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, sourceOneReg, sourceTwoReg, label));
        label = "";
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BLT, label, tempReg, zeroReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRLT(Addr address, Register sourceOneReg, Imm immValTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, sourceOneReg, immValTwo, label));
        label = "";
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BLT, label, tempReg, zeroReg, address);
        returnList.add(branch);
        return returnList;
    }




    public List<MIPSInstruction> createBRLT(Addr address, Imm immValOne, Register sourceTwoReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, immValOne, sourceTwoReg, label));
        label = "";
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BLT, label, tempReg, zeroReg, address);
        returnList.add(branch);
        return returnList;
    }


    public List<MIPSInstruction> createBRGT(Addr address, Register sourceOneReg, Register sourceTwoReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, sourceOneReg, sourceTwoReg, label));
        label = "";
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BGT, label, tempReg, zeroReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRGT(Addr address, Register sourceOneReg, Imm immValTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, sourceOneReg, immValTwo, label));
        label = "";
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BGT, label, tempReg, zeroReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRGT(Addr address, Imm immValOne, Register sourceTwoReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, immValOne, sourceTwoReg, label));
        label = "";
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BGT, label, tempReg, zeroReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRGEQ(Addr address, Register sourceOneReg, Register sourceTwoReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, sourceOneReg, sourceTwoReg, label));
        label = "";
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BGE, label, tempReg, zeroReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRGEQ(Addr address, Register sourceOneReg, Imm immValTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, sourceOneReg, immValTwo, label));
        label = "";
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BGE, label, tempReg, zeroReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRGEQ(Addr address, Imm immValOne, Register sourceTwoReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, immValOne, sourceTwoReg, label));
        label = "";
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BGE, label, tempReg, zeroReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRLEQ (Addr address, Register sourceRegOne, Register sourceRegTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, sourceRegOne, sourceRegTwo, label));
        label = "";
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BLE, label, tempReg, zeroReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRLEQ (Addr address, Register sourceRegOne, Imm immValTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, sourceRegOne, immValTwo, label));
        label = "";
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BLE, label, tempReg, zeroReg, address);
        returnList.add(branch);
        return returnList;
    }

    public List<MIPSInstruction> createBRLEQ (Addr address, Imm immValOne, Register sourceRegTwo, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createSub(tempReg, immValOne, sourceRegTwo, label));
        label = "";
        MIPSInstruction branch = new MIPSInstruction(MIPSOp.BLE, label, tempReg, zeroReg, address);
        returnList.add(branch);
        return returnList;
    }


    public List<MIPSInstruction> createReturn(Register destReg, Register sourceReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createMove(destReg, sourceReg, label));
        label = "";
        returnList.addAll(createJumpReturn(label));
        return returnList;
    }

    public List<MIPSInstruction> createReturn(Register destReg, Imm sourceImm, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        returnList.addAll(createAdd(returnReg, zeroReg, sourceImm, label));
        label = "";
        returnList.addAll(createJumpReturn(label));
        return returnList;
    }

    public List<MIPSInstruction> createArray(Imm arraySize, Register arrayPtrReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        // store syscall num in return register 
        Imm sysCallNum = new Imm(SbrkSyscallNum, "DEC");
        returnList.addAll(createAdd(returnReg, zeroReg, sysCallNum, label));
        label = "";
        // move array size into argument register
        returnList.addAll(createAdd(argsReg, zeroReg, arraySize, label));
        // call sbrk 
        MIPSInstruction sbrk = new MIPSInstruction(MIPSOp.SYSCALL, label);
        returnList.add(sbrk);
        // move pointer to arrayPtr register
        returnList.addAll(createMove(arrayPtrReg, returnReg, label));
        return returnList;
    }

    public List<MIPSInstruction> createArrayLoad(Register arrayStoreReg, Register arrayPtrReg, Imm offsetImm, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        // store offset in assembler temporary
        returnList.addAll(createAdd(tempReg, zeroReg, offsetImm, label));
        label = "";
        // left shift  to account for number of bytes in int
        Imm shiftImm = new Imm(offsetShift, "DEC");
        MIPSInstruction sll = new MIPSInstruction(MIPSOp.SLL,label, tempReg, tempReg, shiftImm);
        returnList.add(sll);
        // add address of array to assembler temporary 
        // convert from register to address
        Addr tempRegAddr = new Addr(tempReg);
        returnList.addAll(createAdd(tempReg, tempReg, arrayPtrReg, label));
        MIPSInstruction arrayLoad = new MIPSInstruction(MIPSOp.LW, label, arrayStoreReg, tempRegAddr);
        returnList.add(arrayLoad);
        return returnList;
    }

    public List<MIPSInstruction> createArrayLoad(Register arrayStoreReg, Register arrayPtrReg, Register offsetReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        //store offset in assembler temporary
        returnList.addAll(createMove(tempReg, offsetReg, label));
        label = "";
        // left shift to account for number of bytes in int
        Imm shiftImm  = new Imm(offsetShift, "DEC");
        MIPSInstruction sll = new MIPSInstruction(MIPSOp.SLL, label, tempReg, tempReg, shiftImm);
        returnList.add(sll);
        // add address of array to assembler temporary
        returnList.addAll(createAdd(tempReg, tempReg, arrayPtrReg, label));
        // convert tempReg to Addr 
        Addr tempRegAddr = new Addr(tempReg);
        MIPSInstruction arrayLoad = new MIPSInstruction(MIPSOp.LW, label, arrayStoreReg, tempRegAddr);
        returnList.add(arrayLoad);
        return returnList;
    }

    public List<MIPSInstruction> createArrayStore(Register arrayStoreReg, Register arrayPtrReg, Imm offsetImm, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        // store offset in assembler temporary
        returnList.addAll(createAdd(tempReg, zeroReg, offsetImm, label));
        label = "";
        // left shift  to account for number of bytes in int
        Imm shiftImm = new Imm(offsetShift, "DEC");
        MIPSInstruction sll = new MIPSInstruction(MIPSOp.SLL,label, tempReg, tempReg, shiftImm);
        returnList.add(sll);
        // add address of array to assembler temporary 
        returnList.addAll(createAdd(tempReg, tempReg, arrayPtrReg, label));
        // convert tempReg to address
        Addr tempRegAddr = new Addr(tempReg);
        MIPSInstruction arrayStore = new MIPSInstruction(MIPSOp.SW, label, arrayStoreReg, tempRegAddr);
        returnList.add(arrayStore);
        return returnList;
    }

    public List<MIPSInstruction> createArrayStore(Register arrayStoreReg, Register arrayPtrReg, Register offsetReg, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        //store offset in assembler temporary
        returnList.addAll(createMove(tempReg, offsetReg, label));
        label = "";
        // left shift to account for number of bytes in int
        Imm shiftImm  = new Imm(offsetShift, "DEC");
        MIPSInstruction sll = new MIPSInstruction(MIPSOp.SLL, label, tempReg, tempReg, shiftImm);
        returnList.add(sll);
        // add address of array to assembler temporary
        returnList.addAll(createAdd(tempReg, tempReg, arrayPtrReg, label));
        // convert tempReg to Addr
        Addr tempRegAddr = new Addr(tempReg);
        MIPSInstruction arrayStore = new MIPSInstruction(MIPSOp.SW, label, arrayStoreReg, tempRegAddr);
        returnList.add(arrayStore);
        return returnList;
    }

    public List<MIPSInstruction> createPUTI(Register printReg, String label) {
        // store syscall number in return register
        List<MIPSInstruction> returnList = new LinkedList<>();
        Imm syscallNum = new Imm(printIntSyscallNum, "DEC");
        returnList.addAll(createAdd(returnReg, zeroReg, syscallNum, label));
        label = "";
        // store integer to print in argReg
        returnList.addAll(createMove(argsReg, printReg, label));
        // make syscall
        MIPSInstruction puti = new MIPSInstruction(MIPSOp.SYSCALL, label);
        returnList.add(puti);
        return returnList;

    }

    public List<MIPSInstruction> createPUTI(Imm printVal, String label) {
                // store syscall number in return register
                List<MIPSInstruction> returnList = new LinkedList<>();
                Imm syscallNum = new Imm(printIntSyscallNum, "DEC");
                returnList.addAll(createAdd(returnReg, zeroReg, syscallNum, label));
                label = "";
                // store integer to print in argReg
                returnList.addAll(createAdd(argsReg, zeroReg, printVal, label));
                // make syscall
                MIPSInstruction puti = new MIPSInstruction(MIPSOp.SYSCALL, label);
                returnList.add(puti);
                return returnList;
        
    }


    public List<MIPSInstruction> createPUTC(Register printReg, String label) {
                // store syscall number in return register
                List<MIPSInstruction> returnList = new LinkedList<>();
                Imm syscallNum = new Imm(printCharSyscallNum, "DEC");
                returnList.addAll(createAdd(returnReg, zeroReg, syscallNum, label));
                label = "";
                // store char to print in argReg
                returnList.addAll(createMove(argsReg, printReg, label));
                // make syscall
                MIPSInstruction putc = new MIPSInstruction(MIPSOp.SYSCALL, label);
                returnList.add(putc);
                return returnList;
    }

    public List<MIPSInstruction> createPUTC(Imm printVal, String label) {
                // store syscall number in return register
                List<MIPSInstruction> returnList = new LinkedList<>();
                Imm syscallNum = new Imm(printCharSyscallNum, "DEC");
                returnList.addAll(createAdd(returnReg, zeroReg, syscallNum, label));
                label = "";
                // store char to print in argReg
                returnList.addAll(createAdd(argsReg, zeroReg, printVal, label));
                // make syscall
                MIPSInstruction putc = new MIPSInstruction(MIPSOp.SYSCALL, label);
                returnList.add(putc);
                return returnList;
    }

    public List<MIPSInstruction> createGETI(Register destReg, String label) {
        // store syscall number in return register
        List<MIPSInstruction> returnList = new LinkedList<>();
        Imm syscallNum = new Imm(getIntSyscallNum, "DEC");
        returnList.addAll(createAdd(returnReg, zeroReg, syscallNum, label));
        label = "";
        // make syscall
        MIPSInstruction geti = new MIPSInstruction(MIPSOp.SYSCALL, label);
        returnList.add(geti);
        // move returned integer into destination
        returnList.addAll(createMove(destReg, returnReg, label));
        return returnList;
    }


    public List<MIPSInstruction> createGETC(Register destReg, String label) {
        // store syscall number in return register
        List<MIPSInstruction> returnList = new LinkedList<>();
        Imm syscallNum = new Imm(getCharSyscallNum, "DEC");
        returnList.addAll(createAdd(returnReg, zeroReg, syscallNum, label));
        label = "";
        // make syscall
        MIPSInstruction getc = new MIPSInstruction(MIPSOp.SYSCALL, label);
        returnList.add(getc);
        // move returned integer into destination
        returnList.addAll(createMove(destReg, returnReg, label));
        return returnList;
    }

    public List<MIPSInstruction> createLoadToRegister(Register destReg, Register pointerReg, Imm offsetImm, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        // store location in memory we are reading from in the assembly temporary
        // stack grows down so offset should subtract from pointer not add
        returnList.addAll(createSub(tempReg, pointerReg, offsetImm, label));
        label = "";
        // grab value at location stored in at and place it in the destination register 
        Addr tempRegAddr = new Addr(tempReg);
        MIPSInstruction storeWord = new MIPSInstruction(MIPSOp.LW, label, destReg, tempRegAddr);
        returnList.add(storeWord);
        return returnList;
    }

    public List<MIPSInstruction> createStoreFromRegister(Register sourceReg, Register pointerReg, Imm offsetImm, String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        // store location in memory we are writing to in the assembly temporary
        // stack grows down so offset should subtract from pointer not add 
        returnList.addAll(createSub(tempReg, pointerReg, offsetImm, label));
        label = "";
        // grab value in source reg and place it in the address stored in the assembly temporary
        // convert tempReg to addr
        Addr tempRegAddr = new Addr(tempReg);
        MIPSInstruction loadWord = new MIPSInstruction(MIPSOp.SW, label, sourceReg, tempRegAddr);
        returnList.add(loadWord);
        return returnList;
    }

    // returns a function exit syscall
    public List<MIPSInstruction> createExit(String label) {
        List<MIPSInstruction> returnList = new LinkedList<>();
        Imm returnVal = new Imm(exitSyscallNum + "", "DEC");
        returnList.addAll(createAdd(returnReg, zeroReg, returnVal, label));
        label = "";
        MIPSInstruction syscall = new MIPSInstruction(MIPSOp.SYSCALL, label);
        returnList.add(syscall);
        return returnList;

        

    }

}