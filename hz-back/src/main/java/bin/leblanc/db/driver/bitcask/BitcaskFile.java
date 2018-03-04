package bin.leblanc.db.driver.bitcask;

public class BitcaskFile {
    public static int timestamp(){
        return (int)(System.currentTimeMillis() / 1000L);
    }


}
