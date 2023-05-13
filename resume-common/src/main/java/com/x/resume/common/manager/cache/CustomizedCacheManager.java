package com.x.resume.common.manager.cache;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.x.resume.common.client.RedisClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Service
public class CustomizedCacheManager {

    @Resource
    private RedisClient redisClient;

    /**
     * 缓存服务
     *
     * @param key        主键
     * @param expireTime 过期时间（秒）
     * @param callBack   回调函数
     * @param params     参数
     * @return
     */
    public <T> T get(String key, int expireTime, CacheCallBack<T> callBack, Class<T> clazz, Object... params) {
        T t = this.redisClient.getObject(key, clazz);
        if (t != null) {
            return t;
        }
        t = callBack.execute(params);
        if (t == null) {
            return null;
        }
        this.redisClient.setex(key, t, expireTime);
        return t;
    }

    public String get(String key) {
        return this.redisClient.get(key);
    }

    public boolean set(String key, String values, Integer expire) {
        return this.redisClient.setex(key, values, expire);
    }

    public void removeByPre(String pre) {

        Set<String> keys = this.redisClient.execute(i -> i.keys(pre + "*"));

        if (CollectionUtils.isEmpty(keys)) {
            return;
        }

        this.redisClient.del(keys.toArray(new String[keys.size()]));
    }

    public <T> T getObject(String key, Class<T> clazz) {
        return this.redisClient.getObject(key, clazz);
    }

    public <T> T getObject(String key, TypeReference<T> typeReference) {
        String value = this.redisClient.get(key);
        return value == null ? null : JSONObject.parseObject(value, typeReference);
    }

    public Long del(String... keys) {
        return this.redisClient.del(keys);
    }

    public boolean setex(String key, Object value, int expireTime) {
        return this.redisClient.setex(key, value, expireTime);
    }

    public Long incrAndExpireAt(String key, long timestamp) {
        final Long result = this.redisClient.incr(key);
        this.redisClient.expireAt(key, timestamp);
        return result;
    }

    public List<String> brpop(int timeout, String key) {
        return this.redisClient.execute((j) -> {
            return j.brpop(timeout, key);
        });
    }

    public void setRedisClient(RedisClient redisClient) {
        this.redisClient = redisClient;
    }
}
