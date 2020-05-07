package extension.data.global;

import extension.util.encryption.Rsa;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static extension.util.DateUtil.getLocalTimeFromU;

/**
 * @author Dr
 */
public class CacheData {

	private static final Map<String, Data> CACHEDATA = new ConcurrentHashMap<String, Data>();

    final public static void addRsaCache(String botuuid) throws NoSuchAlgorithmException {
		Data data = new Data(Rsa.buildKeyPair());
		CACHEDATA.put(botuuid,data);
	}

    final public static String getRsaCachePuky(String botuuid) throws IOException {
		return Rsa.getPublicKey(CACHEDATA.get(botuuid).puky);
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
			final long time = getLocalTimeFromU();
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
			this.endtime = getLocalTimeFromU()+300;
		}
	}
}
