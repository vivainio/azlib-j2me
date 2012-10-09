package org.helyx.basics4me.lang;

public class BooleanUtil {

	public static final String TRUE = "true";
	public static final String FALSE = "false";

	private BooleanUtil() {
		super();
	}
	
	public static boolean getBoolean(String value) {
		return parseBooleanIgnoreCase(value);
	}
	
	public static boolean parseBoolean(String value) {
		return TRUE.equals(value);
	}
	
	public static boolean parseBooleanIgnoreCase(String value) {
		if (value == null) {
			return false;
		}
		return TRUE.equals(value.toLowerCase());
	}

	public static String valueOf(boolean booleanValue) {
		return booleanValue ? TRUE : FALSE;
	}

}
