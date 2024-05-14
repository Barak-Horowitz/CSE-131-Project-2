package ir;

<<<<<<< HEAD
import ir.operand.*;
import java.lang.StringBuilder;
=======
import ir.operand.IROperand;
>>>>>>> 0502606 (first commit)

public class IRInstruction {

    public enum OpCode {
        ASSIGN,
        ADD, SUB, MULT, DIV, AND, OR,
        GOTO,
        BREQ, BRNEQ, BRLT, BRGT, BRLEQ, BRGEQ,
        RETURN,
        CALL, CALLR,
        ARRAY_STORE, ARRAY_LOAD,
        LABEL;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public OpCode opCode;

    public IROperand[] operands;

    public int irLineNumber;

    public IRInstruction() {}

    public IRInstruction(OpCode opCode, IROperand[] operands, int irLineNumber) {
        this.opCode = opCode;
        this.operands = operands;
        this.irLineNumber = irLineNumber;
    }

<<<<<<< HEAD
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(opCode.toString());
        for(IROperand o : operands) {
            builder.append(", ");
            builder.append(o.toString());
        }
        return builder.toString();
    }

    public boolean isInternalJump() {
        switch(opCode) {
            case GOTO:
            case BREQ:
            case BRNEQ:
            case BRLT:
            case BRGT:
            case BRLEQ:
            case BRGEQ: return true;
        }
        return false;
    }

    public boolean isFunctionJump() {
        switch(opCode) {
            case CALL:
            case CALLR:
            case RETURN: return true;
        }
        return false;
    }

    public boolean isJump() {
        return isInternalJump() || isFunctionJump();
    }

    public boolean isBranch() {
        switch(opCode) {
            case BREQ:
            case BRNEQ:
            case BRLT:
            case BRGT:
            case BRLEQ:
            case BRGEQ: return true;
        }
        return false;
    }

    public boolean isArithmetic() {
        switch(opCode) {
            case ASSIGN:
            case ADD:
            case SUB:
            case MULT:
            case DIV:
            case AND:
            case OR:
            case CALLR:
            case RETURN:
            case ARRAY_STORE:
            case ARRAY_LOAD: return true;
        }
        return false;
    }

    public boolean isWriteToVar() {
        switch(opCode) {
            case ASSIGN:
            case ADD:
            case SUB:
            case MULT:
            case DIV:
            case AND:
            case OR:
            case CALLR:
            case ARRAY_LOAD: return true;
        }
        return false;
    }

    public boolean isWriteToArray() {
        switch(opCode) {
            case ARRAY_STORE: return true;
        }
        return false;
    }

    public boolean isArrayOp() {
        switch(opCode) {
            case ARRAY_LOAD:
            case ARRAY_STORE: return true;
        }
        return false;
    }
=======
>>>>>>> 0502606 (first commit)
}
