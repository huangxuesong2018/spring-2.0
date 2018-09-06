package com.arcln.myrpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TCPTransport {
    private String serviceAddress;

    public TCPTransport(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    private Socket newSocket(){
        Socket client = null;
        try {
            System.out.println("开始建立socket 连接.......");
            String[] arrs = serviceAddress.split(":");
            client = new Socket(arrs[0], Integer.parseInt(arrs[1]));
        } catch (IOException e) {
            throw new RuntimeException("socket 连接建立失败");
        }
        return client;
    }

    public Object send(RpcRequest request){
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        try{
            socket = newSocket();
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();

            objectInputStream = new ObjectInputStream(socket.getInputStream());
            return objectInputStream.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(objectInputStream !=null){
                    objectInputStream.close();
                }
                if(objectOutputStream != null){
                    objectOutputStream.close();
                }
                if(socket != null){
                        socket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
