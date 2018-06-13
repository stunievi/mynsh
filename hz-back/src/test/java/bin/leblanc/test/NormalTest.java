package bin.leblanc.test;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.entity.InspectTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;
import org.springframework.util.StringUtils;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.script.*;
import java.io.*;
import java.net.MalformedURLException;
import java.util.List;

@Slf4j
public class NormalTest {

    public interface Cubi {
        void test(String a, String b);
    }

    enum fuck {
        test("fff");

        private String value;

        fuck(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    class d{
        long d;
    }

    public int getPage(int count, int pageSize){
        int p = count / pageSize;
        int mod = count % pageSize;
        if(mod > 0){
            p = p + 1;
        }
        if(0 == p){
            return 1;
        }
        return p;
    }
    @Test
    public void test() throws IOException {
        int count = 11;
        int pagesize = 10;

        getPage(count,pagesize);

        val dd = new d();

        try(
                InputStream is = new InputStream() {
        @Override
        public int read() throws IOException {
            return 0;
        }
    };
            OutputStream os = new OutputStream() {
                @Override
                public void write(int b) throws IOException {

                }
            };
        ){

        }
        catch (Exception e){

        }
        val a = 1;
//        a = 2;

        String b = null;
        boolean bb = StringUtils.isEmpty(b);

        String sql = "select user as c from emp_table as d";
        String dbType = JdbcConstants.MYSQL;

        //格式化输出
        String result = SQLUtils.format(sql, dbType);
        System.out.println(result); // 缺省大写格式
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        String s = SQLUtils.addCondition(sql,"fuck = 2",dbType);
        //解析出的独立语句的个数
        System.out.println("size is:" + stmtList.size());
        for (int i = 0; i < stmtList.size(); i++) {

            SQLStatement stmt = stmtList.get(i);
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            stmt.accept(visitor);
            //获取表名称
            System.out.println("Tables : " + visitor.getTables());
            //获取操作方法名称,依赖于表名称
            System.out.println("Manipulation : " + visitor.getTables());
            //获取字段名称
            System.out.println("fields : " + visitor.getColumns());
            for (TableStat.Column column : visitor.getColumns()) {
                int c = 1;
            }
        }

//        Cubi ttt = Zed.createProxy("/Users/bin/work/zed_template.yaml",Cubi.class);
//        ttt.test("13","2");
//        int b = 1;
//
//        Yaml yaml = new Yaml();
//        Reader r = new FileReader("/Users/bin/work/zed_template.yaml");
//        Object o = yaml.load(r);
//        String s = yaml.dump(o);
//        r.close();

    }

    @Test
    public void read() throws IOException {
        File file = new File("/Users/bin/work/Untitled-3");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str;
        int count = 0;
        OutputStream os = new FileOutputStream("/Users/bin/work/fuck");
        while ((str = br.readLine()) != null) {
            if (count++ % 2 == 0) {
                os.write((str + "\n").getBytes());
            }
        }
        br.close();
        os.close();
//        os.flush();

    }

    @Test
    public void ccc() {
        InspectTask task = new InspectTask();
//        int ccc =1;
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
        bindings1.put("a", this);
//        cs.eval
//        bindings1.put("a",new ArrayList<>(Arrays.asList(1,3,4)));
        engine.eval(Utils.getReader("classpath:/config/behiver.js"), bindings1);
        engine.eval("go(1,2)");

//        cs.eval("go(1,2)");
        Object o = cs.eval(bindings1);
//        String s = ret.toString();

        int a = 1;
    }


    @Test
    public void ttt() throws IOException, com.lowagie.text.DocumentException {
        String inputFile = "/Users/bin/work/test.html";
        String url = new File(inputFile).toURI().toURL().toString();
        String outputFile = "/Users/bin/work/2.pdf";
        System.out.println(url);
        OutputStream os = new FileOutputStream(outputFile);
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(url);
// 解决中文支持问题
//ITextFontResolver fontResolver = renderer.getFontResolver();
//fontResolver.addFont("C:/Windows/Fonts/SIMSUN.TTC",
//                BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
// 解决图片的相对路径问题
// renderer.getSharedContext().setBaseURL("bytes:/D:/z/temp/");
        renderer.layout();
        renderer.createPDF(os);
        os.close();
    }

    @Test
    public void testfff() {
        File file = new File("/Users/bin/work/1.jpg");
        if (file.exists()) {
            Document document = new Document();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream("/Users/bin/work/2.pdf");
                PdfWriter writer = PdfWriter.getInstance(document, fos);

//                writer.setPageEvent(new );

// 添加PDF文档的某些信息，比如作者，主题等等
                document.addAuthor("arui");
                document.addSubject("test pdf.");
// 设置文档的大小
                document.setPageSize(PageSize.A4);
// 打开文档


                document.open();

                XMLWorkerHelper.getInstance().parseXHtml(writer, document, new FileInputStream("/Users/bin/work/test.html"));

//                Image image =Image.getInstance("/Users/bin/work/1.jpg");
//                image.setAlignment(Image.ALIGN_CENTER);
//                image.scaleAbsoluteHeight(5000);
////                image.s
//                document.add(image);
                document.close();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
// 写入一段文字
//document.add(new Paragraph("JUST TEST ..."));
// 读取一个图片
//                Image image = Image.getInstance(imgFilePath);
//                float imageHeight=image.getScaledHeight();
//                float imageWidth=image.getScaledWidth();
//                int i=0;
//                while(imageHeight>500||imageWidth>500){
//                    image.scalePercent(100-i);
//                    i++;
//                    imageHeight=image.getScaledHeight();
//                    imageWidth=image.getScaledWidth();
//                    System.out.println("imageHeight->"+imageHeight);
//                    System.out.println("imageWidth->"+imageWidth);
    }
}
