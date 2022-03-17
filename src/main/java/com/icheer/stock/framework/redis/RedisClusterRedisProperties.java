package com.icheer.stock.framework.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisClusterRedisProperties {


    @Value("${spring.redis.timeout}")
    private Integer redisTimeout;

    @Value("${spring.redis.jedis.pool.max-active}")
    private Integer poolMaxActive;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private Integer poolMaxIdle;

    @Value("${spring.redis.jedis.pool.min-idle}")
    private Integer poolMinIdle;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private Integer poolMaxWait;

    @Value("${spring.redis.cluster.nodes}")
    private List<String> clusterNodes;

    @Value("${spring.redis.cluster.max-redirects}")
    private Integer clusterMaxRedirects;

    @Value("${spring.share.redis.password}")
    private String password;

    @Value("${spring.redis.client-name}")
    private String client_name;


    public List<String> getClusterNodes(){
        return  clusterNodes;
    }
    public String getPassword(){
        return password;
    }

    public String getClient_name(){return  client_name;}
    public Integer getRedisTimeout(){
        return redisTimeout;
    }
    public Integer getPoolMaxActive(){
        return  poolMaxActive;
    }
    public Integer getPoolMaxIdle(){
        return poolMaxIdle;
    }
    public Integer getPoolMinIdle(){
        return poolMinIdle;
    }
    public Integer getPoolMaxWait(){
        return  poolMaxWait;
    }
    public Integer getClusterMaxRedirects(){
        return clusterMaxRedirects;
    }
}
