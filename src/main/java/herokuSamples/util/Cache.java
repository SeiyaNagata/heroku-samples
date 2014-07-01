package herokuSamples.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

import redis.clients.jedis.Jedis;

public class Cache {

	private static Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private static final CacheImpl impl;

	static {
		String uri = System.getenv("REDISCLOUD_URL");
		CacheImpl c = null;
		if (uri != null) {
			try {
				c = new RedisCache(new URI(uri));
				log.info("Create RedisCache");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		if (c == null) {
			c = new InMemoryCache();
			log.info("Create InMemoryCache");
		}
		impl = c;
	}
	public static Object get(String key) {
		return null;
	}

	public static void set(String key, Object value) {
		set(key, value, 3600);
	}

	public static void set(String key, Object value, int expiration) {

	}

	public static void remove(String key) {

	}

	private interface CacheImpl {
		public Object get(String key);
		public void set(String key, Object value, int expiration);
		public void remove(String key);
	}

	private static class RedisCache implements CacheImpl {

		private URI uri;

		public RedisCache(URI uri) {
			this.uri = uri;
		}

		public Object get(String key) {
			Jedis redis = new Jedis(this.uri);
			try {
				return redis.get(key);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void set(String key, Object value, int expiration) {
			Jedis redis = new Jedis(this.uri);
			try {
				return redis.setex(key, expiration, value.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void remove(String key) {
			Jedis redis = new Jedis(this.uri);
			try {
				redis.del(key);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static class InMemoryCache implements CacheImpl {
		private Map<String, Entry> map = new HashMap();

		public synchronized Object get(String key) {
			Entry entry = map.get(key);
			if (entry == null || entry.isExpired()) {
				return null;
			} else {
				return entry.value;
			}
		}

		public synchronized void set(String key, Object value, int expiration) {
			map.put(key, new Entry(expiration, value));
		}

		public synchronized void remove(String key) {
			map.remove(key);
		}
	}

	private static class Entry {
		public long lifetime;
		public Object value;

		public Entry(int expiration, Object value) {
			this.lifetime = expiration == 0 ? Long.MAX_VALUE : System.currentTimeMillis() + (expiration * 1000);
			this.value = value;
		}

		public boolean isExpired() {
			return System.currentTimeMillis() > this.lifetime;
		}
	}
}