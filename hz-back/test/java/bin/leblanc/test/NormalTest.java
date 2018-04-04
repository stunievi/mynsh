package bin.leblanc.test;

import bin.leblanc.zed.Zed;
import com.beeasy.hzback.core.helper.Utils;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import javax.script.*;
import java.io.*;

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

    public void ccc(int c, String d){
        int ccc =1;
    }

    @Test
    public void testEnum() throws ScriptException, FileNotFoundException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");

        Integer d = (Integer) engine.eval("1+2");
        ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) engine.eval("(function(){return {c:1};})();");
        ScriptObjectMirror scriptObject = scriptObjectMirror;
        Integer cc = (Integer) scriptObject.get("c");
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);

        CompiledScript cs = ((Compilable) engine).compile(Utils.getReader("classpath:config/behiver.js"));
//        CompiledScript cs = ((Compilable)engine).compile("(function c(){function go(){ a.ccc() } return go(1,2) })()");
//        ScriptFunction sf = ScriptFunction.createAnonymous().set
        Bindings bindings1 = engine.createBindings();
        bindings1.put("a",this);
//        cs.eval
//        bindings1.put("a",new ArrayList<>(Arrays.asList(1,3,4)));
        engine.eval(Utils.getReader("classpath:/config/behiver.js"),bindings1);
        engine.eval("go(1,2)");

//        cs.eval("go(1,2)");
        Object o = cs.eval(bindings1);
//        String s = ret.toString();

        int a = 1;
    }
}
