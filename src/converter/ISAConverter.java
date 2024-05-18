package converter;

import ir.*;

public abstract class ISAConverter<ProgType> {
    public abstract ProgType convert(IRProgram irProg);
}
