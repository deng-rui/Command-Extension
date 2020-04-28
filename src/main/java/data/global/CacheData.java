package extension.data.global;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import java.security.NoSuchAlgorithmException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.PrivateKey;

import extension.util.encryption.RSA;

import static extension.util.DateUtil.getLocalTimeFromUTC;

public class CacheData {

	private static final Map<String, Data> Cache = new ConcurrentHashMap<String, Data>();

	final public static void addRSACache(String botuuid) throws NoSuchAlgorithmException {
		Data data = new Data(RSA.buildKeyPair());
		Cache.put(botuuid,data);
	}

	final public static String getRSACache_Puky(String botuuid) throws IOException {
		return RSA.getPublicKey(Cache.get(botuuid).puky);
	}

	final public static PrivateKey getRSACache_Prky(String botuuid) {
		return Cache.get(botuuid).prky;
	}

	final public static boolean isRSACache(String botuuid) {
		return Cache.containsKey(botuuid);
	}

	final public static void clear() {
		Iterator it = Cache.entrySet().iterator();
		while(it.hasNext()){
			Entry entry = (Entry)it.next();
			Data data = (Data)entry.getValue();
			final long time = getLocalTimeFromUTC();
			if (data.endtime < time) {
				Cache.remove((String)entry.getKey());
			}
		}
	}

	static class Data {
		public long endtime;
		public PublicKey puky;
		public PrivateKey prky;

		public Data(KeyPair key) {
			PublicKey puky = key.getPublic();
	    	PrivateKey prky = key.getPrivate();
			this.endtime = getLocalTimeFromUTC()+300;
		}
	}
}
