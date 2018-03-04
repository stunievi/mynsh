package bin.leblanc.db.driver.bitcask;


import lombok.Getter;

@Getter
public class BitcaskOptions {
    private int exprSeconds = 0;
    private long maxFileSize = 1024 * 1024;
    private boolean readWrite = false;
    private int openTimeSeconds = 20;

    public int exprTime(){
        if(exprSeconds > 0){
            return BitcaskFile.timestamp() - exprSeconds;
        }
        else{
            return 0;
        }
    }
}
