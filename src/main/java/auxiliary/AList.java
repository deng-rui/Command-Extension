package extension.auxiliary;

import java.util.*;

public class AList {
	private static List<String> Tourist = Collections.synchronizedList(new ArrayList<String>());

	public static void addTourist(String uuid) {
		Tourist.add(uuid);
	}
	//

	public static void removeTourist(String str) {    
         List<String> tempList = new ArrayList<String>(Tourist.size());  
         for(int i=0;i<Tourist.size();i++){  
             if(!Tourist.get(i).equals(str)){  
                 tempList.add(Tourist.get(i));  
             }  
         }  
         Tourist = tempList;  
    }
	//

	public static boolean getplayer_boolean(String uuid) {
		return Tourist.contains(uuid);
	}
	//是否存在

}