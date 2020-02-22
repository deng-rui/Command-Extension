package extension.data.global;

public class Strings {
	private static String GC_1 = "N";

	public static String getGC_1() {
		return GC_1;
	}

//读取
	public static void setGC_1(String GC_1) {
		Strings.GC_1 = GC_1;
	}

	public static void setGC() {
		Strings.GC_1 = "N";
	}
}