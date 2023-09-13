package com.foo.gosucatcher.global.util;

public class RandomNumberUtils {
	public static String createAuthenticationStringNumber() {
		return String.valueOf((int)(Math.random() * (90_000)) + 100_000);
	}

	public static String createTemporaryStringPassword() {
		return String.valueOf((int)(Math.random() * (9_000_000)) + 10_000_000);
	}
}
