package com.armorhud.utils;

import java.util.Random;

public class RandomUtil {
    public Random random = new Random();
    public static RandomUtil INSTANCE = new RandomUtil();

    public Random getRandom() {
        return random;
    }

    public int randomInRange(int n, int n2) {
        return n + random.nextInt(n2 - n + 1);
    }


}
