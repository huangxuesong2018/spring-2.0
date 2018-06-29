package com.arcln.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class HelloServiceImpl extends UnicastRemoteObject implements IHelloService {
    public HelloServiceImpl() throws RemoteException {
        super();
    }

    public String sayHello(String msg) throws RemoteException{
        return "hello,"+msg;
    }
}
