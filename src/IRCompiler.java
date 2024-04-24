
import ir.*;
import compiler.*;
import java.util.*;
import java.io.IOException;

public class IRCompiler {
    public static void main(String[] args) throws IOException, IRException {
        IRProgram program = new IRReader().parseIRFile(args[0]);

        for(IRFunction fnc : program.functions) {
            FunctionContext ctx = new FunctionContext(fnc);

            new IRPrinter(System.out).printFunction(ctx.getOptimizedFunction());
        }
    }
}
