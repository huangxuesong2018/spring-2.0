package com.arcln.myrpc;

import com.arcln.myrpc.anno.RpcAnnotation;

@RpcAnnotation(className=IGpHello.class)
public class GpHelloImpl implements IGpHello {
    public String sayHello(String msg) {
        return "hello,"+msg;
    }

    public static void main(String[] args) {
        IGpHello a = new GpHelloImpl();
        System.out.println(a.getClass().getAnnotation(RpcAnnotation.class).className().getName());
    }
}
