package extension.util.alone;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
//Java

//DFA? Bloom Filter?
public class BadWord {
	public static String filePath = "/other/Sensitive_Thesaurus.txt";
	public static Set<String> words;
	public static Map<String,String> wordMap;
	public static int minMatchTYpe = 1;// 最小匹配规则
	public static int maxMatchType = 2;// 最大匹配规则
	static{
		BadWord.words = readTxtByLine(filePath);
		addBadWordToHashMap(BadWord.words);
	}
	private static Set<String> readTxtByLine(String path){  
		Set<String> keyWordSet = new HashSet<String>();
		BufferedReader reader=null;  
		String temp=null;  
		//int line=1;  
		try{  
			reader=new BufferedReader(new InputStreamReader(BadWord.class.getResourceAsStream(path),"UTF-8"));  
			while((temp=reader.readLine())!=null){  
				keyWordSet.add(temp); 
			}  
		} catch(Exception e){  
			e.printStackTrace();  
		} finally{  
			if(reader!=null){  
				try{  
					reader.close();  
				}catch(Exception e){  
					e.printStackTrace();  
				}  
			}  
		}
		return keyWordSet;
	}
	/**
	 * 检查文字中是否包含敏感字符，检查规则如下：<br>
	 * @param txt
	 * @param beginIndex
	 * @param matchType
	 * @return，如果存在，则返回敏感词字符的长度，不存在返回0
	 * @version 1.0
	 */
	@SuppressWarnings({ "rawtypes"})
	public static int checkBadWord(String txt,int beginIndex,int matchType){
		boolean  flag = false;    // 敏感词结束标识位：用于敏感词只有1位的情况
		int matchFlag = 0;     // 匹配标识数默认为0
		char word = 0;
		Map nowMap = wordMap;
		for(int i = beginIndex; i < txt.length() ; i++){
			word = txt.charAt(i);
			nowMap = (Map) nowMap.get(word);     // 获取指定key
			if(nowMap != null){     // 存在，则判断是否为最后一个
				matchFlag++;     // 找到相应key，匹配标识+1 
				if("1".equals(nowMap.get("isEnd"))){       // 如果为最后一个匹配规则,结束循环，返回匹配标识数
					flag = true;       // 结束标志位为true   
					if(minMatchTYpe == matchType){    // 最小规则，直接返回,最大规则还需继续查找
						break;
					}
				}
			} else {     // 不存在，直接返回
				break;
			}
		}
		 /* if(matchFlag < 2 && !flag){     
			matchFlag = 0;
		}*/
		if(!flag){     
			matchFlag = 0;
		}
		return matchFlag;
	}
	
	/**
	 * 替换敏感字字符
	 * @param txt
	 * @param matchType
	 * @param replaceChar 替换字符，默认*
	 * @version 1.0
	 */
	public static String replaceBadWord(String txt,int matchType,String replaceChar){
		String resultTxt = txt;
		Set<String> set = getBadWord(txt, matchType);
		Iterator<String> iterator = set.iterator();
		String word = null;
		String replaceString = null;
		while (iterator.hasNext()) {
			word = iterator.next();
			replaceString = getReplaceChars(replaceChar, word.length());
			resultTxt = resultTxt.replaceAll(word, replaceString);
		}
		
		return resultTxt;
	}
	/**
	 * 获取文字中的敏感词
	 * @param txt 文字
	 * @param matchType 匹配规则 1：最小匹配规则，2：最大匹配规则
	 * @return
	 * @version 1.0
	 */
	public static Set<String> getBadWord(String txt , int matchType){
		Set<String> sensitiveWordList = new HashSet<String>();
		
		for(int i = 0 ; i < txt.length() ; i++){
			int length = checkBadWord(txt, i, matchType);
			if(length > 0){
				sensitiveWordList.add(txt.substring(i, i+length));
				i = i + length - 1;
			}
		}
		
		return sensitiveWordList;
	}
	
	/**
	 * 获取替换字符串
	 * @param replaceChar
	 * @param length
	 * @return
	 * @version 1.0
	 */
	private static String getReplaceChars(String replaceChar,int length){
		String resultReplace = replaceChar;
		for(int i = 1 ; i < length ; i++){
			resultReplace += replaceChar;
		}
		
		return resultReplace;
	}
	
	/**
	 * 将我们的敏感词库构建成了一个类似与一颗一颗的树，这样我们判断一个词是否为敏感词时就大大减少了检索的匹配范围。
	 * @param keyWordSet 敏感词库
	 * @author yqwang0907
	 * @date 2018年2月28日下午5:28:08
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void addBadWordToHashMap(Set<String> keyWordSet) {
		//初始化敏感词容器，减少扩容操作
		wordMap = new HashMap(keyWordSet.size());
		String key = null;  
		Map nowMap = null;
		Map<String, String> newWorMap = null;
		//迭代keyWordSet
		Iterator<String> iterator = keyWordSet.iterator();
		while(iterator.hasNext()){
			key = iterator.next();
			nowMap = wordMap;
			for(int i = 0 ; i < key.length() ; i++){
				char keyChar = key.charAt(i);
				Object wordMap = nowMap.get(keyChar);
				if(wordMap != null){
					nowMap = (Map) wordMap;
				}
				else{
					newWorMap = new HashMap<String,String>();
					newWorMap.put("isEnd", "0");
					nowMap.put(keyChar, newWorMap);
					nowMap = newWorMap;
				}
				
				if(i == key.length() - 1) {
					nowMap.put("isEnd", "1");
				}
			}
		}
	}

    public static Set<String> badWord(String text) {
		Set<String> s = BadWord.words;
		Map<String,String> map = BadWord.wordMap;
		long beginTime = System.currentTimeMillis();
		Set<String> set = BadWord.getBadWord(text, 2);
		return set;
	}
}