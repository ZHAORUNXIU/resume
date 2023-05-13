package com.x.resume.common.client;

import com.x.resume.common.util.SerializationUtil;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Redis 客户端类
 */
public class RedisClient extends JedisConnectionFactory {

    /**
     * 执行返回结果
     */
    public <T> T execute(Function<Jedis, T> func) {
        try (Jedis j = fetchJedisConnector()) {
            return func.apply(j);
        }
    }

    /**
     * 执行结果返回OK字符串作为成功使用
     */
    public boolean executeOK(Function<Jedis, String> func) {
        return "OK".equals(execute(func));
    }

    /**
     * 执行结果返回1L表示成功情况使用
     */
    public boolean executeSuccess(Function<Jedis, Long> func) {
        return 1 == execute(func).intValue();
    }

    //key相关操作------

    /**
     * 获取值
     *
     * @param key 键
     * @return 键值
     */
    public String get(String key) {
        return execute(j -> j.get(key));
    }

    /**
     * 获取值
     *
     * @param key 键
     * @return 键值
     */
    public <T> T getObject(String key, Class<T> clazz) {
        String value = get(key);
        return value == null ? null : SerializationUtil.deserialize(value, clazz);
    }

    /**
     * 获取长整型值值
     *
     * @param key 键
     * @return 键值
     */
    public Long getLong(String key) {
        String value = get(key);
        return value == null ? null : Long.valueOf(value);
    }

    /**
     * 获取整型值值
     *
     * @param key 键
     * @return 键值
     */
    public Integer getInteger(String key) {
        String value = get(key);
        return value == null ? null : Integer.valueOf(value);
    }

    /**
     * 重新设置超时时间
     *
     * @param key     键
     * @param timeout 超时时间（单位秒）
     * @return 设置是否成功
     */
    public boolean expire(String key, int timeout) {
        return executeSuccess(j -> j.expire(key, timeout));
    }

    /**
     * 设置生存时间，到达这个时间过期
     *
     * @param key       键
     * @param timestamp unix时间戳(单位秒)
     * @return 设置是否成功
     */
    public boolean expireAt(String key, long timestamp) {
        return executeSuccess(j -> j.expireAt(key, timestamp));
    }

    /**
     * 重新设置超时时间
     *
     * @param key     键
     * @param timeout 超时时间（单位毫秒）
     * @return 设置是否成功
     */
    public boolean pexpire(String key, long timeout) {
        return executeSuccess(j -> j.pexpire(key, timeout));
    }

    /**
     * 设置生存时间，到达这个时间过期
     *
     * @param key       键
     * @param timestamp unix时间戳(单位毫秒)
     * @return 设置是否成功
     */
    public boolean pexpireAt(String key, long timestamp) {
        return executeSuccess(j -> j.pexpireAt(key, timestamp));
    }

    /**
     * 秒为单位，返回剩余过期时间
     *
     * @param key 键
     * @return key不存在返回-2，key没有设置过期时间返回-1
     */
    public long ttl(String key) {
        return execute(j -> j.ttl(key));
    }

    /**
     * 毫秒为单位，返回剩余过期时间
     *
     * @param key 键
     * @return key不存在返回-2，key没有设置过期时间返回-1
     */
    public long pttl(String key) {
        return execute(j -> j.pttl(key));
    }

    /**
     * 返回键对应的值类型
     *
     * @param key 键
     * @return none (key不存在),string (字符串),list (列表),set (集合),zset (有序集),hash (哈希表)
     */
    public String type(String key) {
        return execute(j -> j.type(key));
    }


    /**
     * 设置生存时间，到达这个时间过期
     *
     * @param key 键
     * @return 设置是否成功
     */
    public boolean persist(String key) {
        return executeSuccess(j -> j.persist(key));
    }

    /**
     * 删除键
     *
     * @param keys 键
     * @return 删除数量
     */
    public Long del(String... keys) {
        return execute(j -> j.del(keys));
    }

    /**
     * 判断键是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    public boolean exists(String key) {
        return execute(j -> j.exists(key));
    }

    //string相关操作------

    /**
     * 设置值，同时设置过期时间
     *
     * @param key        键
     * @param value      值
     * @param expireTime 单位秒
     * @return 返回是否成功
     */
    public boolean setex(String key, Object value, int expireTime) {
        String data = value instanceof String ? (String) value : SerializationUtil.serialize(value);
        return executeOK(j -> j.setex(key, expireTime, data));
    }

    /**
     * 设置值，同时设置过期时间
     *
     * @param key        键
     * @param value      值
     * @param expireTime 单位毫秒
     * @return 返回是否成功
     */
    public boolean psetex(String key, Object value, long expireTime) {
        String data = value instanceof String ? (String) value : SerializationUtil.serialize(value);
        return executeOK(j -> j.psetex(key, expireTime, data));
    }

    /**
     * 设置值
     *
     * @param key   键
     * @param value 值
     * @return 返回是否成功
     */
    public boolean set(String key, Object value) {
        String data = value instanceof String ? (String) value : SerializationUtil.serialize(value);
        return executeOK(j -> j.set(key, data));
    }

    /**
     * 追加字符串，如果key不存在类似set操作，如果key存在则在原字符串默认追加value
     *
     * @param key   键
     * @param value 值
     * @return key中字符串的长度
     */
    public Long append(String key, String value) {
        return execute(j -> j.append(key, value));
    }

    /**
     * 对key的值做减1操作，如果不存在则先设置为0再减1
     *
     * @param key 键
     * @return 操作后的值
     */
    public Long decr(String key) {
        return execute(j -> j.decr(key));
    }

    /**
     * 对key的值做减value操作，如果不存在则先设置为0再减value
     *
     * @param key   键
     * @param value 每次操作值
     * @return 操作后的值
     */
    public Long decrBy(String key, long value) {
        return execute(j -> j.decrBy(key, value));
    }

    /**
     * 对key的值做加1操作，如果不存在则先设置为0再加1
     *
     * @param key 键
     * @return 操作后的值
     */
    public Long incr(String key) {
        return execute(j -> j.incr(key));
    }

    /**
     * 对key的值做加value操作，如果不存在则先设置为0再加value
     *
     * @param key   键
     * @param value 每次操作值
     * @return 操作后的值
     */
    public Long incrBy(String key, long value) {
        return execute(j -> j.incrBy(key, value));
    }

    /**
     * 截取子串，start和end可为负值，如果为负表示从末尾算，-1为最后一个数
     *
     * @param key   键
     * @param start 开始位置，包含
     * @param end   结束位置，包含
     * @return 返回符合条件的子串
     */
    public String getrange(String key, long start, long end) {
        return execute(j -> j.getrange(key, start, end));
    }

    /**
     * 从指定的偏移量开始，覆盖字符串的值,不可以有负值
     *
     * @param key    键
     * @param offset 偏移量，包含便宜位置
     * @param value  覆盖使用的字符串
     * @return 修改后字符串的长度
     */
    public Long setrange(String key, long offset, String value) {
        return execute(j -> j.setrange(key, offset, value));
    }

    /**
     * 获取key的值长度
     *
     * @param key 键
     * @return 值得长度
     */
    public Long strlen(String key) {
        return execute(j -> j.strlen(key));
    }

    /**
     * 获取旧值，并且设置新值
     *
     * @param key   键
     * @param value 值
     * @return 返回旧值，如果key不存在，则返回null
     */
    public String getSet(String key, String value) {
        return execute(j -> j.getSet(key, value));
    }

    /**
     * 获取所有键值
     *
     * @param keys 键
     * @return 不存在的键返回null
     */
    public List<String> mget(String... keys) {
        return execute(j -> j.mget(keys));
    }

    /**
     * 设置所有键值，存在覆盖
     *
     * @param keyvalues 键值，格式key1 value1 key2 value2
     * @reutrn 成功返回true
     */
    public boolean mset(String... keyvalues) {
        return executeOK(j -> j.mset(keyvalues));
    }

    /**
     * 设置所有键值，所有key都不存在则执行成功，如果有一个key存在则不设置
     *
     * @param keyvalues 键值，格式key1 value1 key2 value2
     * @return 都成功返回true，否则返回false
     */
    public boolean msetnx(String... keyvalues) {
        return executeSuccess(j -> j.msetnx(keyvalues));
    }

    /**
     * 设置所有键值，存在则不设置
     *
     * @param key   键
     * @param value 值
     * @return 存在则不设置返回false
     */
    public boolean setnx(String key, String value) {
        return executeSuccess(j -> j.setnx(key, value));
    }

    // hash表-------------------

    /**
     * 设置域值,如果存在覆盖
     *
     * @param key   键
     * @param field 域
     * @param value 域
     * @return field不存在返回1，field原来存在返回0
     */
    public Long hset(String key, String field, String value) {
        return execute(j -> j.hset(key, field, value));
    }

    /**
     * 设置域值，如果存在则操作无效
     *
     * @param key   键
     * @param field 域
     * @param value 域
     * @return field不存在执行设置返回ture
     */
    public boolean hsetnx(String key, String field, String value) {
        return executeSuccess(j -> j.hsetnx(key, field, value));
    }

    /**
     * 删除域
     *
     * @param key    键
     * @param fields 域
     * @return 返回删除域数量
     */
    public Long hdel(String key, String... fields) {
        return execute(j -> j.hdel(key, fields));
    }

    /**
     * 指定域是否存在
     *
     * @param key   键
     * @param field 域
     * @return 存在返回true
     */
    public boolean hexists(String key, String field) {
        return execute(j -> j.hexists(key, field));
    }

    /**
     * 返回给定域的值
     *
     * @param key   键
     * @param field 域
     * @return 返回值
     */
    public String hget(String key, String field) {
        return execute(j -> j.hget(key, field));
    }

    /**
     * 返回给定键的所有域值
     *
     * @param key 键
     * @reurn 返回结构的所有键值
     */
    public Map<String, String> hgetAll(String key) {
        return execute(j -> j.hgetAll(key));
    }

    /**
     * 增长固定值
     *
     * @param key   键
     * @param field 域
     * @param value 增长值
     * @return field对应的新值
     */
    public Long hincrBy(String key, String field, long value) {
        return execute(j -> j.hincrBy(key, field, value));
    }

    /**
     * 获取键的所有属性域
     *
     * @param key 键
     * @return 获取所有键
     */
    public Set<String> hkeys(String key) {
        return execute(j -> j.hkeys(key));
    }

    /**
     * 获取键的所有值
     *
     * @param key 键
     * @return 获取所有值
     */
    public List<String> hvals(String key) {
        return execute(j -> j.hvals(key));
    }

    /**
     * 获取键的所有属性域数量
     *
     * @param key 键
     * @return 数量
     */
    public Long hlen(String key) {
        return execute(j -> j.hlen(key));
    }

    /**
     * 返回给定域的值列表
     *
     * @param key    键
     * @param fields 域
     * @return 如果field不存在返回null占位
     */
    public List<String> hmget(String key, String... fields) {
        return execute(j -> j.hmget(key, fields));
    }

    /**
     * 同时设置多个值
     *
     * @param key  键
     * @param hash 域值对
     * @return 成功返回true
     */
    public boolean hmset(String key, Map<String, String> hash) {
        return executeOK(j -> j.hmset(key, hash));
    }

    // list -----------------

    /**
     * 获取索引对应的值，0为第一个，-1为最后一个
     *
     * @param key   键
     * @param index 域值
     * @return index对应的值
     */
    public String lindex(String key, long index) {
        return execute(j -> j.lindex(key, index));
    }

    /**
     * 在给定的pivot前面插入值
     *
     * @param key   键
     * @param pivot 域值
     * @param value 域值
     * @return 返回列表长度，如果pivot不存在返回-1，key不存在或是空列表返回0，key不是列表返回错误
     */
    public Long linsertBefore(String key, String pivot, String value) {
        return execute(j -> j.linsert(key, LIST_POSITION.BEFORE, pivot, value));
    }

    /**
     * 在给定的pivot后面插入值
     *
     * @param key   键
     * @param pivot 域值
     * @param value 域值
     * @return 返回列表长度，如果pivot不存在返回-1，key不存在或是空列表返回0，key不是列表返回错误
     */
    public Long linsertAfter(String key, String pivot, String value) {
        return execute(j -> j.linsert(key, LIST_POSITION.AFTER, pivot, value));
    }

    /**
     * 列表长度
     *
     * @param key 键
     * @return 返回列表长度
     */
    public Long llen(String key) {
        return execute(j -> j.llen(key));
    }

    /**
     * 返回列表头元素,同时移除该元素
     *
     * @param key 键
     * @return 头元素
     */
    public String lpop(String key) {
        return execute(j -> j.lpop(key));
    }

    /**
     * 从头元素开始压入列表，执行顺序从左到右，如果列表不存在则创建列表
     *
     * @param key    键
     * @param values 值
     * @return 返回列表长度
     */
    public Long lpush(String key, String... values) {
        return execute(j -> j.lpush(key, values));
    }

    /**
     * 从头元素开始压入列表，执行顺序从左到右，如果列表不存在则不处理
     *
     * @param key   键
     * @param value 值
     * @return 返回列表长度
     */
    public Long lpushx(String key, String value) {
        return execute(j -> j.lpushx(key, value));
    }

    /**
     * 返回范围内的列表数据，start和stop可以是负，0为开始，-1为最后一个，结果包含start和stop
     *
     * @param key   键
     * @param start 开始位置
     * @param stop  结束位置
     * @return 返回列表数据
     */
    public List<String> lrange(String key, long start, long stop) {
        return execute(j -> j.lrange(key, start, stop));
    }

    /**
     * 移除一定数量的元素
     *
     * @param key   键
     * @param count 负表示从队尾开始，正表示对头开始，0表示所有
     * @param value 判断值
     * @return 移除元素数量
     */
    public Long lrem(String key, long count, String value) {
        return execute(j -> j.lrem(key, count, value));
    }

    /**
     * 替换索引的值
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return 成功返回true
     */
    public boolean lset(String key, long index, String value) {
        return executeOK(j -> j.lset(key, index, value));
    }

    /**
     * 列表裁剪，只保留start到end的数据，如果start>end则列表为空列表
     *
     * @param key   键
     * @param start 开始
     * @param end   结束
     * @return 成功返回true
     */
    public boolean ltrim(String key, long start, long end) {
        return executeOK(j -> j.ltrim(key, start, end));
    }

    /**
     * 移除并返回队尾元素
     *
     * @param key 键
     * @return 队尾元素，不存在返回null
     */
    public String rpop(String key) {
        return execute(j -> j.rpop(key));
    }

    /**
     * 从队尾插入元素，values从左向右执行，key不存在则创建新列表
     *
     * @param key    键
     * @param values 值
     * @return 返回列表长度
     */
    public Long rpush(String key, String... values) {
        return execute(j -> j.rpush(key, values));
    }

    /**
     * 从队尾插入元素，values从左向右执行，key不存在则放弃操作
     *
     * @param key   键
     * @param value 值
     * @return 返回列表长度
     */
    public Long rpushx(String key, String value) {
        return execute(j -> j.rpushx(key, value));
    }

    // set -------------

    /**
     * 元素插入集合中
     *
     * @param key    键
     * @param values 值
     * @return 返回新增集合元素数量
     */
    public Long sadd(String key, String... values) {
        return execute(j -> j.sadd(key, values));
    }

    /**
     * 返回集合数量
     *
     * @param key 键
     * @return 返回集合数量
     */
    public Long scard(String key) {
        return execute(j -> j.scard(key));
    }

    /**
     * 返回第一个集合与其他集合的差集
     *
     * @param keys 键
     * @return 差集集合
     */
    public Set<String> sdiff(String... keys) {
        return execute(j -> j.sdiff(keys));
    }

    /**
     * 第一个集合与其他集合的差集，结果同时保存到目标集合
     *
     * @param dstKey 目标集合
     * @param keys   键
     * @return 目标集合的数量
     */
    public Long sdiffstore(String dstKey, String... keys) {
        return execute(j -> j.sdiffstore(dstKey, keys));
    }

    /**
     * 返回所有给定集合的交集
     *
     * @param keys 键
     * @return 交集集合
     */
    public Set<String> sinter(String... keys) {
        return execute(j -> j.sinter(keys));
    }

    /**
     * 所有给定集合的交集,结果保存到目标集合，如果目标集合已存在则覆盖
     *
     * @param dstKey 目标集合
     * @param keys   键
     * @return 新集合数量
     */
    public Long sinterstore(String dstKey, String... keys) {
        return execute(j -> j.sinterstore(dstKey, keys));
    }

    /**
     * 返回目标集合成员
     *
     * @param key 键
     * @return 集合成员
     */
    public Set<String> smembers(String key) {
        return execute(j -> j.smembers(key));
    }

    /**
     * 把member从srcKey移除，然后加入dstKey
     *
     * @param srcKey 源集合键
     * @param dstKey 目标集合键
     * @param member src的成员
     * @return member从srcKey成功移除返回true，否则返回false
     */
    public boolean smove(String srcKey, String dstKey, String member) {
        return executeSuccess(j -> j.smove(srcKey, dstKey, member));
    }

    /**
     * 随机获取一个集合值，并且删除
     *
     * @param key 集合键
     * @return 集合数据
     */
    public String spop(String key) {
        return execute(j -> j.spop(key));
    }

    /**
     * 随机获取一个集合值，但不删除
     *
     * @param key 集合键
     * @return 集合数据
     */
    public String srandmember(String key) {
        return execute(j -> j.srandmember(key));
    }

    /**
     * count大于0，随机获取一个count数量的列表，列表内容不相同，如果count大于集合数量则返回集合所有值
     * count小于0，随机获取count数量的列表，值可能相同
     *
     * @param key   集合键
     * @param count 数量
     * @return 集合元素列表
     */
    public List<String> srandmember(String key, int count) {
        return execute(j -> j.srandmember(key, count));
    }

    /**
     * 移除集合中的元素
     *
     * @param key     键
     * @param members 集合元素
     * @return 移除数量
     */
    public Long srem(String key, String... members) {
        return execute(j -> j.srem(key, members));
    }

    /**
     * 集合并集
     *
     * @param keys 键
     * @return 集合并集
     */
    public Set<String> sunion(String... keys) {
        return execute(j -> j.sunion(keys));
    }

    /**
     * 集合并集,并把数据存入dstKey中
     *
     * @param dstKey 目标集合
     * @param keys   键
     * @return 新集合数量
     */
    public Long sunionstore(String dstKey, String... keys) {
        return execute(j -> j.sunionstore(dstKey, keys));
    }

}

