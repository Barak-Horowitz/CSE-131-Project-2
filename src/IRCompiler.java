
import ir.*;
import compiler.*;
import java.util.*;
import java.io.IOException;

public class IRCompiler {
    public static void main(String[] args) throws IOException, IRException {
        IRProgram program = new IRReader().parseIRFile(args[0]);

        for(IRFunction fnc : program.functions) {
            FunctionContext ctx = new FunctionContext(fnc);
            for(BlockContext blk : ctx.blocks) {
                if(blk.labels.isEmpty()) {
                    System.out.println("<...>:");
                } else {
                    for(String i : blk.labels) {
                        System.out.println(i+":");
                    }
                }
                for(IRInstruction i : blk.instructions) {
                    System.out.println("\t"+i);
                }
            }
            System.out.println();

            // for(BlockContext blk : ctx.blocks) {
            //     System.out.println(blk.predecessors + " " + blk.branches);
            // }
            //
            // System.out.println();
        }
    }
}
