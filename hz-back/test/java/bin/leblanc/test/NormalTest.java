package bin.leblanc.test;

import bin.leblanc.zed.Zed;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Optional;

@Slf4j
public class NormalTest {

    public interface Cubi{
        void test(String a,String b);
    }

    enum fuck{
        test("fff");

        private String value;
        fuck(String value){
           this.value = value;
        }

        public String getValue() {
            return value;
        }
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


    @Test
    public void testEnum(){
        Optional<Object> op = Optional.empty();
        op.get();

    }
}
