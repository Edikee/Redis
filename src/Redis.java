import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.plaf.synth.SynthSplitPaneUI;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import static redis.clients.jedis.ScanParams.SCAN_POINTER_START;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.ShardedJedis;

public class Redis {
	Jedis jedis;

	public void addItem(Map<String, String> map) {
		jedis = getConnection();
		String product = getNextId();
		for (String key : map.keySet()) {
			if (!key.equals("id"))
				jedis.hset(product, key, map.get(key));
		}
		jedis.lpush("Product_keys", product);
	}

	public void removeItem(int id) {
		jedis = getConnection();
		String product = "product:" + id;
		jedis.lrem("Product_keys", 1, product);
		jedis.del(product);
	}

	public String getNextId() {

		jedis = getConnection();
		int index = 0;
		boolean ok = false;
		Object[] keys = jedis.keys("product:*").toArray();
		while (!ok) {
			ok = true;
			for (int i = 0; i < keys.length; i++) {
				int currentkey = Integer.parseInt(keys[i].toString().split(":")[1]);
				if (currentkey == index) {
					index++;
					ok = false;
				}
			}
		}

		return "product:" + index;

	}

	public void updateItem(int id, Map<String, String> map) {
		jedis = getConnection();
		String product = "product:" + id;
		for (String key : map.keySet()) {
			if (!key.equals("id"))
				jedis.hset(product, key, map.get(key));
		}
	}

	public List<Map<String, String>> getProduct() {
		jedis = getConnection();
		List<Map<String, String>> list = new LinkedList<Map<String, String>>();
		Map<String, String> tmpmap = new HashMap<String, String>();
		for (String keys : getKeys()) {

			String id = keys.split(":")[1];
			tmpmap = jedis.hgetAll(keys);
			tmpmap.put("id", id);
			list.add(tmpmap);
		}
		return list;
	}

	private List<String> getKeys() {
		jedis = getConnection();
		Object[] keys = jedis.keys("product:*").toArray();
		List<String> list = new LinkedList<String>();
		for (int i = 0; i < keys.length; i++) {
			list.add(keys[i] + "");

		}

		return list;

	}

	private Jedis getConnection() {

		try {
			JedisPool pool = new JedisPool("localhost", 6379);
			if (pool.getResource().isConnected())
				return pool.getResource();
		} catch (Exception e) {
			System.out.println("6379 server is down");
		}

		try {
			JedisPool pool = new JedisPool("localhost", 6380);

			if (pool.getResource().isConnected())
				return pool.getResource();
		} catch (Exception e) {
			System.out.println("6380 server is down");
		}

		return null;

	}

}
