package com.arcln.myrpc;

import com.arcln.myrpc.anno.RpcAnnotation;
import org.junit.Ignore;

@RpcAnnotation(className=IGpHello.class)
public class MyHelloImpl implements IGpHello {
    @Override
    public String sayHello(String msg) {
        return "arcln ->" + msg;
    }
}
