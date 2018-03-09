package bin.leblanc.taliyah.engine.bitcask;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BitcaskIndex {
    private long timestamp;
    private long fileId;
    private int valueSize;
    private int valuePos;
    private byte[] key;
}
