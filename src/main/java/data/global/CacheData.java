package extension.data.global;

import extension.util.encryption.RSA;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static extension.util.DateUtil.getLocalTimeFromUTC;

public class CacheData {

	private static final Map<String, Data> CACHEDATA = new ConcurrentHashMap<String, Data>();

    final public static void addRsaCache(String botuuid) throws NoSuchAlgorithmException {
		Data data = new Data(RSA.buildKeyPair());
		CACHEDATA.put(botuuid,data);
	}

    final public static String getRsaCachePuky(String botuuid) throws IOException {
		return RSA.getPublicKey(Cache.get(botuuid).puky);
	}

    final public static PrivateKey getRsaCachePrky(String botuuid) {
		return CACHEDATA.get(botuuid).prky;
	}

    final public static boolean isRsaCache(String botuuid) {
		return CACHEDATA.containsKey(botuuid);
	}

	final public static void clear() {
		Iterator it = CACHEDATA.entrySet().iterator();
		while(it.hasNext()){
			Entry entry = (Entry)it.next();
			Data data = (Data)entry.getValue();
			final long time = getLocalTimeFromUTC();
			if (data.endtime < time) {
				CACHEDATA.remove((String)entry.getKey());
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
