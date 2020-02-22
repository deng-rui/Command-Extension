package extension.core;

public static class Initialization {
	
	public static void Player_Privilege_classification() {
		JSONObject date = getData("mods/GA/Authority.json");
		for (int i = 0; i < 11; i++) {
			etPower_Data(i,(List)date.get(i));
		}
	}
}