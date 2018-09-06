package com.arcln.myrpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

public class ProcessorHandler implements Runnable {
   // private Object service;
    private Socket socket;
    private Map<String,Object> handerMap;

    public ProcessorHandler(Map<String,Object> handerMap, Socket socket) {
        this.handerMap = handerMap;
        this.socket = socket;
    }

    public void run() {
        ObjectInputStream objectInputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try{
            System.out.println("监听到客户端请求");
            objectInputStream = new ObjectInputStream(this.socket.getInputStream());
            RpcRequest request = (RpcRequest)objectInputStream.readObject();
            Object o = this.invoke(request);

            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(o);
            objectOutputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(objectInputStream !=null){
                    objectInputStream.close();
                }
                if(objectOutputStream !=null){
                    objectOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 第一种方式
     * @param request
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private Object invoke(RpcRequest request) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object[] args = request.getParameterTypes();
        Class<?>[] parameterTypes = new Class[args.length];
        for(int i = 0; i < args.length ; i ++){
            parameterTypes[i] = args[i].getClass();
        }
        Object service = handerMap.get(request.getClassName());
        String methodName = request.getMethodName();
        Method method = service.getClass().getMethod(methodName,parameterTypes);
        return method.invoke(service,args);
    }
}
