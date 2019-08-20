import sun.nio.ch.IOUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Fix {

    public static void main(String[] args) throws IOException {
        String path = "";
        String out = "";
        String wall = "";
        for (int i = 0; i < args.length; i++) {
            if(args[i].equals("-f")){
                path = args[++i];
            }
            if(args[i].equals("-o")){
                out = args[++i];
            }
            if(args[i].equals("-l")){
                wall = args[++i];
            }
        }
        if(path.isEmpty()){
            System.out.println("no file");
            System.exit(-1);
        }
        //读取名单
        byte[] limit = new byte[1024];
        int total = 0;
        if(!wall.isEmpty()){
            BufferedReader reader = new BufferedReader(new FileReader(wall));
            String line = null;
            while((line = reader.readLine()) != null){
                if(line.isEmpty()){
                    continue;
                }
                limit[total++] = Integer.valueOf(line, 16).byteValue();
            }
        }
        File file = new File(path);
        File outFile = null;
        if(out.isEmpty()){
            outFile = new File(file.getParent(), file.getName() + ".out");
        } else {
            outFile = new File(out);
        }

        InputStream fis = new FileInputStream(file);
        OutputStream fos = new FileOutputStream(outFile) ;

        byte last = -1;
        //2m buffer
        byte[] bs = new byte[1024*1024*2];
        while(true){
            int n = fis.read(bs);
            if (n < 0) {
                break;
            }

//            skips.clear();
            if(last > 0){
                //如果上次没检查完，则优先检查第一个
                if(bs[0] == (byte)0x7c){
                    fos.write(' ');
                } else if(bs[0] == '\n'){
                    //如果上一个是7c，则7C替换为空格
                    if(last == '|'){
                        last = ' ';
                    } else {
                        bs[0] = ' ';
                    }
                    fos.write(last);
                } else {
                    fos.write(last);
                }
                last = 0;
            }
            for (int i = 0; i < n; i++) {
                check:
                for (int i1 = 0; i1 < total; i1++) {
                    if(limit[i1] == bs[i]){
                        if(i == n - 1){
                            //如果正好命中了最後一位, 下次需要检查第一位
                            last = bs[i];
                        } else {
                            if(bs[i + 1] == (byte)0x7c){
                                //命中錯誤
                                bs[i++] = ' ';
                            }
                        }
                        break check;
                    }
                }

                if(i > 0){
                    if(bs[i] == '\n'){
                        //如果上一个是7c，则7C替换为空格
                        if(bs[i - 1] == '|'){
                            bs[i - 1] = ' ';
                        } else {
                            bs[i] = ' ';
                        }
                    }
                }
            }

            //如果可以余一个
            if(last > 0){
                n -= 1;
            }

            //补上最后的
            fos.write(bs, 0, n);
        }
        //如果还有余孽
        if(last > 0){
            fos.write(last);
        }

        fos.flush();
        fos.close();
        fis.close();

    }



}
