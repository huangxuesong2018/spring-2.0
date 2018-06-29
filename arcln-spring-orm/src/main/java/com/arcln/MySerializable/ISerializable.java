package com.arcln.MySerializable;

public interface ISerializable {
    /**
     * 序列化
     * @param o
     * @param <T>
     * @return
     */
    public <T> byte[] serializer(T o);

    /**
     * 反序列化
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T deSerializer(byte[] data,Class clazz);
}
