package com.armorhud.utils;


public class TimerUtil {
    public long lastMs = System.currentTimeMillis();

    public boolean delay(double seconds) {
        double elapsedSeconds = (System.currentTimeMillis() - lastMs) / 1000.0;
        return elapsedSeconds > seconds;
    }




    public double getElapsedTime() {
        return (System.currentTimeMillis() - this.lastMs) / 1000.0;
    }

    public void reset() {
        this.lastMs = System.currentTimeMillis();
    }
}
