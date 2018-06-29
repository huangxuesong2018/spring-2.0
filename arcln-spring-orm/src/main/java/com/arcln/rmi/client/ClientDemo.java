package com.arcln.rmi.client;

import com.arcln.rmi.HelloServiceImpl;
import com.arcln.rmi.IHelloService;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientDemo {
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        IHelloService helloService = (IHelloService) Naming.lookup("rmi://127.0.0.1/hello");
        System.out.println(helloService.sayHello("Mic"));
    }

    public void t() throws ClassNotFoundException {
        Class clazz = Class.forName("com.arcln.rmi.client.MethodTest");

        clazz.getDeclaredMethods();
    }
}
