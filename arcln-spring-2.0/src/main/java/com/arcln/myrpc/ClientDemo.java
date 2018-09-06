package com.arcln.myrpc;

import com.arcln.myrpc.zk.IServiceDiscovery;
import com.arcln.myrpc.zk.ServiceDiscoveryImpl;
import com.arcln.myrpc.zk.ZkConfig;

import java.io.IOException;

public class ClientDemo {
    public static void main(String[] args) throws IOException {
        //连接zookeeper
        IServiceDiscovery serviceDiscovery = new ServiceDiscoveryImpl(ZkConfig.CONNECTION_STR);

        RpcClientProxy proxy = new RpcClientProxy(serviceDiscovery);
        IGpHello gpHello = proxy.clientProxy(IGpHello.class);//这个地方只是传入了接口，真正的实现类什么时候才会发现
        System.out.println(gpHello.sayHello("Mic3"));
        System.in.read();
        System.out.println("over");
    }
}
