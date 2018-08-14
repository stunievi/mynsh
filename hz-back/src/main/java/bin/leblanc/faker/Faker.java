package bin.leblanc.faker;

import java.util.Random;

public class Faker {
    static Random random = new Random();
    static String[] pool = "abcdefghijklmnopqrstuvwxyz".split("");
    static String[] f = new String[]{"张", "王", "周", "武", "李",
            "胡", "赵", "陈", "苗", "戴", "习", "毛", "朱", "韩", "陆"};

    static String[] s = new String[]{"克", "明", "发", "代发", "犯的",
            "和", "我", "人", "同", "娟", "娟娟", "丽", "美丽", "利", "陆"
            , "空间", "改", "办法", "航空",
            "留", "泰", "晨光", "长城", "层层", "莉莉", "胡霍", "娜娜", "大", "光荣"};

    public static String getPhone() {
        String phone = "150";
        for (int i = 0; i < 8; i++) {
            phone += random.nextInt(10);
        }
        return phone;
    }

    public static String getName() {
        return String.valueOf(Math.random());
    }

    public static String getTrueName() {
        return f[random.nextInt(f.length - 1)] + s[random.nextInt(s.length - 1)];
    }
}
