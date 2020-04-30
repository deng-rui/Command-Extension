package extension.util;

import extension.data.global.Config;
import extension.dependent.UTF8Control;
import extension.util.log.Log;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static extension.util.IsUtil.isBlank;

//Java
//
//GA-Exted
//GA-Exted
//Json
 
public class LocaleUtil {

	private String[] lg = null;

	public LocaleUtil(String lg) {
		this.lg = lg.split("_");
	}

	private String language(String o,String t,String input,Object[] params) {
		Locale locale = new Locale(o,t);
		//URLClassLoader file = new URLClassLoader(new URL[] {new File(FileUtil.File(Config.Plugin_Resources_bundles_Path).getPath()).toURI().toURL()});
		//ResourceBundle bundle = ResourceBundle.getBundle("GA", locale, file, new UTF8Control());
		//UTF-8 外置资源
		ResourceBundle bundle = ResourceBundle.getBundle("bundles/GA", locale, new UTF8Control());	
		try {
			if (input !=null){
				if (params == null){
					String result = bundle.getString(input);
					return result;
				}else{
					String result = new MessageFormat(bundle.getString(input),locale).format(params);
					return result;
				}
			}
		//防止使游戏崩溃 CALL..
		} catch (MissingResourceException e) {
			Log.error(e);
		}
		return input+" : Key is invalid.";
		
	}

	public String getinput(String input,Object... params) {
		if (params == null) {
            return core(input,null);
        }
		Object[] ps = new Object[params.length];
		for (int i=0;i<params.length;i++) {
            ps[i] = params[i];
        }
		return core(input,ps);
	}

	public String getinputt(String input,Object[] ps) {
		return core(input,ps);
	}

	// 暂时并行
	// 我还未想好到底如何实现 help :(
	private String core(String input,Object[] params) {
		String[] lang = lg;
		if(isBlank(lg)) {
            lang = Config.SERVER_LANGUAGE.split("_");
        }
		if (params == null) {
            return language(lang[0],lang[1],input,null);
        }
		return language(lang[0],lang[1],input,params);
	}
}

