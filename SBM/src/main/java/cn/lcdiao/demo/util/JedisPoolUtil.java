package cn.lcdiao.demo.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolUtil {
    private static volatile JedisPool jedisPool = null;
    private JedisPoolUtil(){};
    public static JedisPool getJedisPoolInstance(){
        if(null==jedisPool){
            synchronized (JedisPoolUtil.class){
                if(null==jedisPool){
                    JedisPoolConfig poolConfig = new JedisPoolConfig();
                    poolConfig.setMaxWaitMillis(100*1000);////连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
                    poolConfig.setMaxTotal(1000);//最大连接数, 默认8个
                    poolConfig.setMaxIdle(32);//最大空闲连接数, 默认8个
                    poolConfig.setTestOnBorrow(true);//在获取连接的时候检查有效性<ping()>, 默认false
                    jedisPool = new JedisPool(poolConfig,"134.175.116.100",6379);//池子只有一份
                }
            }
        }
        return jedisPool;
    }

    public static void release(Jedis jedis){//解放实例
        if(null!=jedis){
            jedis.close();
        }
    }
}
