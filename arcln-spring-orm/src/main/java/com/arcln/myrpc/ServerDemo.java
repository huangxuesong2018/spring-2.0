package com.arcln.myrpc;

import com.arcln.myrpc.zk.IRegisterCenter;
import com.arcln.myrpc.zk.RegisterCenterImpl;

public class ServerDemo {
    public static void main(String[] args) {
        IGpHello hello = new GpHelloImpl();
        IRegisterCenter registerCenter = new RegisterCenterImpl();
        RpcServer rpcServer = new RpcServer(registerCenter,"127.0.0.1:8080");
        rpcServer.bind(hello);
        rpcServer.publisher();
        System.out.println("服务发布....");
    }
}
