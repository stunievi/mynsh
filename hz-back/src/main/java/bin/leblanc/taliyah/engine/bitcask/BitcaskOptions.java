package bin.leblanc.taliyah.engine.bitcask;


import lombok.Data;

@Data
public class BitcaskOptions {
    private int maxFileSize = 1024 * 1024;
    private String dirPath = null;
    private String dataExtension = ".data";
    private String hintExtension = ".hint";


}
