package com.arcln.myrpc;

public class ServerDemo {
    public static void main(String[] args) {
        RpcServer server = new RpcServer();
        IGpHello hello = new GpHelloImpl();
        server.publisher(hello,8080);
        System.out.println("服务发布....");
    }
}
