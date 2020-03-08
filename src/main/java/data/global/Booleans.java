package extension.data.global;

public class Booleans {
	private static boolean vote = true;

	public static boolean getvote() {
		return vote;
	}
//读取
	public static void setvote(boolean vote){
		Booleans.vote = vote;
	}
//设定
}