package ir.operand;

import ir.IRInstruction;

public abstract class IROperand {

    protected String value;

    protected IRInstruction parent;

    public IROperand(String value, IRInstruction parent) {
        this.value = value;
        this.parent = parent;
    }

    public IRInstruction getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return value;
    }

<<<<<<< HEAD
    @Override
    public int hashCode() {
        return value.hashCode();
    }

=======
>>>>>>> 0502606 (first commit)
}
