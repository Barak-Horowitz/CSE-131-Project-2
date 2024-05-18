package converter;

import compiler.*;
import ir.*;
import isa.mips.*;

public class MIPSConverter extends ISAConverter<MIPSProgram> {
    @Override
    public MIPSProgram convert(IRProgram irProg) {
        return null;
    }

    public MIPSProgram convertFunction(IRFunction irFunc) throws IRException {
        FunctionContext ctx = new FunctionContext(irFunc);

        return null;
    }
}
