package com.arcln.myrpc;

import java.lang.reflect.Proxy;

public class PrclientProxy {
    public <T> T clientProxy(final Class<T> interfaces,final String host,final int port){
       return (T)Proxy.newProxyInstance(interfaces.getClassLoader(),new Class[]{interfaces} , new RemoteInvocationHandler(host,port));
    }
}
