package com.arcln.myrpc;

public class ClientDemo {
    public static void main(String[] args) {
        PrclientProxy prclientProxy = new PrclientProxy();
        IGpHello hello = (IGpHello)prclientProxy.clientProxy(IGpHello.class,"127.0.0.1",8080);
        System.out.println(hello.sayHello("Mic3"));
    }
}
