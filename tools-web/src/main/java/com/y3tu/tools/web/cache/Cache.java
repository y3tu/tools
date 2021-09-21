package com.y3tu.tools.web.cache;

import com.y3tu.tools.kit.lang.func.Func0;

/**
 * 缓存的顶级接口
 *
 * @author y3tu
 */
public interface Cache {
    /**
     * 返回缓存名称
     *
     * @return 缓存名称
     */
    String getName();

    /**
     * 返回真正的缓存对象
     *
     * @return 缓存对象
     */
    Object getNativeCache();

    /**
     * 根据Key返回key对应的值，如果没有就返回NULL
     *
     * @param key key
     * @return 缓存key对应的值
     */
    Object get(Object key);

    /**
     * 根据KEY返回缓存中对应的值，并将其返回类型转换成对应类型，如果对应key不存在返回NULL
     *
     * @param key  缓存key
     * @param type 返回值类型
     * @param <T>  Object
     * @return 缓存key对应的值
     */
    <T> T get(Object key, Class<T> type);

    /**
     * 根据KEY返回缓存中对应的值，并将其返回类型转换成对应类型，如果对应key不存在则调用valueLoader加载数据
     *
     * @param key         缓存key
     * @param valueLoader 加载缓存的回调方法
     * @param <T>         Object
     * @return 缓存key对应的值
     */
    <T> T get(Object key, Func0<T> valueLoader);

    /**
     * 将对应key-value放到缓存，如果key原来有值就直接覆盖
     *
     * @param key   缓存key
     * @param value 缓存的值
     */
    void put(Object key, Object value);

    /**
     * 如果缓存key没有对应的值就将值put到缓存，如果有就直接返回原有的值
     *
     * @param key   缓存key
     * @param value 缓存key对应的值
     * @return 因为值本身可能为NULL，或者缓存key本来就没有对应值的时候也为NULL，
     * 所以如果返回NULL就表示已经将key-value键值对放到了缓存中
     */
    Object putIfAbsent(Object key, Object value);

    /**
     * 在缓存中删除对应的key
     *
     * @param key 缓存key
     */
    void evict(Object key);

    /**
     * 清除缓存
     */
    void clear();
}
