package bin.leblanc.test;

import bin.leblanc.taliyah.engine.bitcask.Bitcask;
import bin.leblanc.taliyah.engine.bitcask.BitcaskOptions;
import lombok.extern.slf4j.Slf4j;
import static org.junit.Assert.*;
import org.junit.Test;

@Slf4j
public class Testdb {



    @Test
    public void bitcask(){
        BitcaskOptions options = new BitcaskOptions();
        options.setDirPath("E:/cubicask");
        Bitcask bitcask = new Bitcask(options);
        bitcask.put("fuck",1);
        int a = (int) bitcask.get("fuck").orElse(2);
        assertEquals(a,1);

        bitcask.close();

        bitcask.open();
        a = (int) bitcask.get("fuck").orElse(2);
        assertEquals(a,1);
    }
}
