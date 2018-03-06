package bin.leblanc.taliyah.engine.bitcask;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BitcaskIndex {
    private long fileId;
    private int startPos;
    private int size;
}
