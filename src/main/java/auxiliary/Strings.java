package extension.auxiliary;


public class Strings {
	private static String GC_1 = "禁止";
	private static String GC_2 = "允许";
	private static String GC_3 = "允许";

	public static String getGC_1() {
		return GC_1;
	}
	public static String getGC_2() {
		return GC_2;
	}
	public static String getGC_3() {
		return GC_3;
	}

//读取
	public static void setGC_1(String GC_1) {
		Strings.GC_1 = GC_1;
	}
	public static void setGC_2(String GC_2) {
		Strings.GC_2 = GC_2;
	}
	public static void setGC_3(String GC_3) {
		Strings.GC_3 = GC_3;
	}
	public static void setGC() {
		Strings.GC_1 = "禁止";
		Strings.GC_2 = "允许";
		Strings.GC_3 = "允许";
	}
}