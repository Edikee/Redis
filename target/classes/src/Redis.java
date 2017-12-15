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
	LinkedList<Map<String, String>> connection;

	public Redis() {
		connection = new LinkedList<Map<String,String>>();
		Map<String, String> tmp = new HashMap<String, String>();
		tmp.put("host", "127.0.0.1");
		tmp.put("port", "6379");
		connection.add(tmp);

		Map<String, String> tmp1 = new HashMap<String, String>();
		tmp1.put("host", "127.0.0.1");
		tmp1.put("port", "6380");
		connection.add(tmp1);
		

	}

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
		
		while (true) {
			Map<String, String> tmp = null;
			try {
				tmp = connection.getFirst();
				 
				JedisPool pool = new JedisPool(tmp.get("host"), Integer.parseInt(tmp.get("port")));
				if (pool.getResource().isConnected())
					System.out.println("connected to: "+ tmp.get("port"));
					return pool.getResource();
				
			} catch (Exception e) {
				System.out.println(tmp.get("port")+" server is down");
				connection.remove(tmp);
				connection.addLast(tmp);
			 
			}
		}

	}

}
