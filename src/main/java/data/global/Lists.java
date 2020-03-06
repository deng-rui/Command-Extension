package extension.data.global;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lists {
	private static List<String> Maps_List;

	public static void addMaps_List(String i) {
		Maps_List.add(i);
	}
	//加入

	public static List<String> getMaps_List() {
		return Maps_List;
	}
	//获取

	public static void EmptyMaps_List() {
		Maps_List = Collections.synchronizedList(new ArrayList<String>());
	}
	//清空

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
	//更新

	public static List removeList(List list, String old) {
		List<String> tempList = new ArrayList<String>(list.size());  
		for(int i=0;i<list.size();i++){
			if(!old.equals(i)) {  
				tempList.add((String)list.get(i));
			}
		}  
		return tempList;  
	}
	//删除

}