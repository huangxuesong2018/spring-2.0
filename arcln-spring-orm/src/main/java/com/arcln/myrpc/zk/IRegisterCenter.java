package com.arcln.myrpc.zk;

public interface IRegisterCenter {
    /**
     * 在zookeeper中创建节点
     * @param serviceName 服务接口类的全路径（如:com.arcln.myrpc.IGpHello）
     * @param serviceAddress  服务接口类的远程接口地址(如: 127.0.0.1:8080)
     */
    public void register(String serviceName,String serviceAddress);
}
