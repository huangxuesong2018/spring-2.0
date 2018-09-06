package com.arcln.myrpc;

import com.arcln.myrpc.zk.IServiceDiscovery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RemoteInvocationHandler implements InvocationHandler {
    private IServiceDiscovery serviceDiscovery;

    public RemoteInvocationHandler(IServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterTypes(args);
        rpcRequest.setClassName(method.getDeclaringClass().getName());

        String serviceAddress = serviceDiscovery.discovery(rpcRequest.getClassName());
        TCPTransport tcpTransport = new TCPTransport(serviceAddress);
        return tcpTransport.send(rpcRequest);
    }
}
