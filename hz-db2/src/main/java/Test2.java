import COM.ibm.db2.app.UDF;

public class Test2 extends UDF{

    public void test(String s1, String s2) throws Exception {
        set(2, "fuck");
    }

    public static void main(String[] args){
        System.out.println("fuck");
    }

}
