package com.armorhud.utils;

import java.util.concurrent.ThreadLocalRandom;

public enum MathUtils
{
	;
	public static int clamp(int value, int min, int max) {
		if (value < min) return min;
		return Math.min(value, max);
	}

	public static float clamp(float value, float min, float max) {
		if (value < min) return min;
		return Math.min(value, max);
	}
	public static int getRandomInt(int from, int to) {
		if (from >= to) return from;
		return ThreadLocalRandom.current().nextInt(from, to + 1);
	}

	public static double clamp(double value, double min, double max) {
		if (value < min) return min;
		return Math.min(value, max);
	}
}
