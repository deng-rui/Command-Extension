package extension.data.global;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lists {
	private static List<String> Maps_List;


    final public static void addMapsList(String i) {
		Maps_List.add(i);
	}
	//加入


    final public static List<String> getMapsList() {
		return Maps_List;
	}
	//获取


    final public static void emptyMapsList() {
		Maps_List = Collections.synchronizedList(new ArrayList<String>());
	}
	//清空

	final public static List<String> updatePlayerData(List<String> list, int old, String nw) {
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

	final public static List removeList(List list, String old) {
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