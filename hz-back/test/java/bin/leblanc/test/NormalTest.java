package bin.leblanc.test;

import bin.leblanc.zed.Zed;
import bin.leblanc.zed.proxy.Method;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.yaml.snakeyaml.Yaml;

import java.io.*;

public class NormalTest {

    public interface Cubi{
        void test(String a,String b);
    }

    @Test
    public void test() throws IOException {

        Cubi ttt = Zed.createProxy("/Users/bin/work/zed_template.yaml",Cubi.class);
        ttt.test("13","2");
        int b = 1;

        Yaml yaml = new Yaml();
        Reader r = new FileReader("/Users/bin/work/zed_template.yaml");
        Object o = yaml.load(r);
        String s = yaml.dump(o);
        r.close();

    }

    @Test
    public void read() throws IOException {
        File file = new File("/Users/bin/work/Untitled-3");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str;
        int count = 0;
        OutputStream os = new FileOutputStream("/Users/bin/work/fuck");
        while((str = br.readLine()) != null){
            if(count++ %2 == 0){
                os.write((str + "\n").getBytes());
            }
        }
        br.close();
        os.close();
//        os.flush();

    }
}
