package compiler;

import ir.*;
import java.util.*;

public class BlockContext {
    public Collection<String> labels;
    public LinkedList<IRInstruction> instructions;
    public Collection<BlockContext> predecessors, branches;

    public BlockContext(Collection<String> labels, List<IRInstruction> instructions) {
        this.labels       = new ArrayList<>(labels);
        this.instructions = new LinkedList<>(instructions);
        this.predecessors = new HashSet<>();
        this.branches     = new HashSet<>();
    }

    @Override
    public String toString() {
        return labels.toString();
    }
}
