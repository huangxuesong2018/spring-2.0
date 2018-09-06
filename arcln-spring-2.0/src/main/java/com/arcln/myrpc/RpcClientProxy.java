package com.arcln.myrpc;

import com.arcln.myrpc.zk.IServiceDiscovery;

import java.lang.reflect.Proxy;

public class RpcClientProxy {
    private IServiceDiscovery serviceDiscovery;

    public RpcClientProxy(IServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T clientProxy(final Class<T> interfaces){
        //代理的理解，竟然是代码 ，那产生代理的时候，被代码对象在此之前，还没有被实例化过
        return (T)Proxy.newProxyInstance(interfaces.getClassLoader(),new Class[]{interfaces} , new RemoteInvocationHandler(serviceDiscovery));
    }
/*    public <T> T clientProxy(final Class<T> interfaces,final String host,final int port){
        //代理的理解，竟然是代码 ，那产生代理的时候，被代码对象在此之前，还没有被实例化过
        return (T)Proxy.newProxyInstance(interfaces.getClassLoader(),new Class[]{interfaces} , new RemoteInvocationHandler(host,port));
    }*/
}
