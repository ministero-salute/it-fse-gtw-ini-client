package it.finanze.sanita.fse2.ms.iniclient.utility;

import com.google.gson.Gson;

import java.util.UUID;


public final class Utilities {

	/**
	 * Private constructor to avoid instantiation.
	 */
	private Utilities() {
		// Constructor intentionally empty.
	}

	/**
	 * Returns {@code true} if the String passed as parameter is null or empty.
	 * 
	 * @param str	String to validate.
	 * @return		{@code true} if the String passed as parameter is null or empty.
	 */
	public static boolean isNullOrEmpty(final String str) {
	    boolean out = false;
		if (str == null || str.isEmpty()) {
			out = true;
		}
		return out;
	}

	     

	public static String generateUUID() {
	    return UUID.randomUUID().toString();
	}

	public static <T> T clone (Object object, Class<T> outputClass) {
		Gson gson = new Gson();
		return gson.fromJson(gson.toJson(object), outputClass);
	}

	public static boolean isIVA(String input) {
		Utilities.isNullOrEmpty(input);
		for (int i = 0; i < input.length(); i++) {
			if (!Character.isDigit(input.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
