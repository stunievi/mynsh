package bin.leblanc.faker;

import java.util.Random;

public class Faker {
    static Random random = new Random();
    static String[] pool = "abcdefghijklmnopqrstuvwxyz".split("");

    public static String getPhone(){
        String phone = "150";
        for(int i = 0; i < 8; i++){
            phone += random.nextInt(10);
        }
        return phone;
    }

    public static String getName(){
        return String.valueOf(Math.random());
    }
}
