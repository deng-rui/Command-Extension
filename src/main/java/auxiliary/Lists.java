package extension.auxiliary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lists {
	//private static List<String> PlayerDate_SQL_Temp = Collections.synchronizedList(new ArrayList<String>());

	//public static void addPlayerDate_SQL_Temp(List list) {
	//	PlayerDate_SQL_Temp = list;
	//}
	//

	public static List updatePlayerData(List list, int old, String nw) {
		List<String> tempList = new ArrayList<String>(list.size()+1);  
		for(int i=0;i<list.size();i++){
			if(Integer.valueOf(old).equals(i)) {  
				tempList.add(nw);
			} else {
				tempList.add((String)list.get(i));  
			}
		}  
		return tempList;  
	}

	/*
	public static List updatePlayerDate_SQL_Temp() {
		return PlayerDate_SQL_Temp
	}

	public static String updatePlayerDate_SQL_Temp(int i) {
		return PlayerDate_SQL_Temp.get(i);
	}
	

	public static boolean getplayer_boolean(String uuid) {
		return Tourist.contains(uuid);
	}
	//是否存在
	*/
}