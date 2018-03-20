package bin.leblanc.test;

import bin.leblanc.zed.Zed;
import bin.leblanc.zed.proxy.Method;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

public class NormalTest {

    public interface Cubi{
        void test(String a,String b);
    }

    @Test
    public void test(){

        Cubi ttt = Zed.createProxy("/Users/bin/work/zed_template.yaml",Cubi.class);
        ttt.test("13","2");
        int b = 1;

    }
}
