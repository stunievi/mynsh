package bin.leblanc.taliyah.engine.bitcask;

import lombok.Data;

import java.io.Serializable;

@Data
public class BitcaskValue implements Serializable {
    private static final long serialVersionUID = 1L;
    private Object obj;

    public BitcaskValue(Object o) {
        this.obj = o;
    }
}
