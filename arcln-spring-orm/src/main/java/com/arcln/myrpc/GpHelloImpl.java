package com.arcln.myrpc;

public class GpHelloImpl implements IGpHello {
    public String sayHello(String msg) {
        return "hello,"+msg;
    }
}
