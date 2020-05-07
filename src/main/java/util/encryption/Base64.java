package extension.util.encryption;

public class Base64 {

	private final static char[] STR = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/'};

	// 解密
	public String decode(String code){
		//对字符串的长度进行计算
		int length = code.length();
		//判断长度的合法性
		if(length == 0 || length % 4 != 0) {
            return null;
        }
		//获取字符串末尾的'='号数目
		int endEqualNum = 0;
		if(code.endsWith("==")) {
            endEqualNum = 2;
        } else if(code.endsWith("=")) {
            endEqualNum = 1;
        }
		//对末尾的=号进行替换
		code.replace('=','0');
		StringBuilder sb = new StringBuilder(length);
		//解码
		int blockNum = length / 4;
		String afterDecode = "";
		for(int i = 0;i < blockNum;i++){
			afterDecode = decodeDetail(code.substring(i * 4,i * 4 + 4));
			sb.append(afterDecode);
		}
		//返回字符串
		String result = sb.toString();
		return result.substring(0,result.length() - endEqualNum);
	}

	// 加密
	public String encode(String code){
		//初始化判断
		if (code == null || "".equals(code)) {
            return null;
        }
		//获取需编码字符串的长度
		int length = code.length();
		StringBuilder sb = new StringBuilder(length * 2);
		//转化为char型数组
		char[] code1 = code.toCharArray();
		//获取长度对3的取余
		int mod = length % 3;
		//获取长度对3的倍数的
		int div = length / 3;
		//编码
		for(int i = 0;i < div;i++){
			int temp = i * 3;
			sb.append(encodeDetail(code1[temp],code1[temp + 1],code1[temp + 2]));
		}
		//对超出的进行额外的编码
		if (mod == 1) {
			String str = encodeDetail(code1[length - 1], '\0', '\0');
			sb.append(str.substring(0,str.length() - 2) + "==");
		}
		if(mod == 2) {
			String str = encodeDetail(code1[length - 2], code1[length - 1], '\0');
			sb.append(str.substring(0,str.length() - 1) + "=");
		}
		return sb.toString();
	}

	private String encodeDetail(char a1,char a2,char a3){
		char[] b = new char[4];
		b[0] = STR[((a1 & 0xFC) >> 2)];
		b[1] = STR[(a1 & 0x03) << 4 | (a2 & 0xF0) >> 4];
		b[2] = STR[(a2 & 0x0F) << 2 | (a3 & 0xC0) >> 6];
		b[3] = STR[(a3 & 0x3F)];
		return String.copyValueOf(b);
	}

	private String decodeDetail(String str){
		int len = str.length();
		if(len != 4) {
            return null;
        }
		char[] b = new char[3];
		int a1 = getIndex(str.charAt(0));
		int a2 = getIndex(str.charAt(1));
		int a3 = getIndex(str.charAt(2));
		int a4 = getIndex(str.charAt(3));
		b[0] = (char) (a1 << 2 | (a2 & 0x30) >> 4);
		b[1] = (char) ((a2 & 0x0F) << 4 | (a3 & 0x3C) >> 2);
		b[2] = (char) ((a3 & 0x03) << 6 | a4);
		return String.copyValueOf(b);
	}

	private int getIndex(char c){
		for(int i = 0;i < STR.length;i++){
			if(STR[i] == c) {
                return i;
            }
		}
		return -1;
	}
}