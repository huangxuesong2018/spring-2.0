package com.arcln.myrpc;

import com.arcln.myrpc.anno.RpcAnnotation;
import com.arcln.myrpc.zk.IRegisterCenter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcServer {
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private IRegisterCenter registerCenter; //注册中心
    private String serviceAddress; //服务发布地址
    //存放服务名称和服务对象之间的关系
    Map<String,Object> handerMap = new HashMap<String,Object>();

    /**
     *
     * @param registerCenter
     * @param serviceAddress
     */
    public RpcServer(IRegisterCenter registerCenter, String serviceAddress) {
        this.registerCenter = registerCenter;
        this.serviceAddress = serviceAddress;
    }

    /**
     * 绑定服务名称和服务对象
     * handerMap<实现类的接口全称，实现类>
     * @param services  具体的实现类
     */
    public void bind(Object ...services){
        for(Object service : services){
            RpcAnnotation annotation = service.getClass().getAnnotation(RpcAnnotation.class);
            String serviceName = annotation.className().getName();
            handerMap.put(serviceName,service);//绑定服务接口名称对应的服务
        }
    }

    /**
     * 发布
     */
    public void publisher(){
        ServerSocket serverSocket = null;
        try {
            String[] addrs = serviceAddress.split(":");

            serverSocket = new ServerSocket(Integer.parseInt(addrs[1]));
            for(String interfaceName : handerMap.keySet()){
                //把对应的接口地址，和服务写到zookeeper中
                registerCenter.register(interfaceName,serviceAddress);
                System.out.println("注册服务成功："+interfaceName+"->"+serviceAddress);
            }

            while (true){
                System.out.println("服务监听中..................");
                Socket socket = serverSocket.accept();
                threadPool.execute(new ProcessorHandler(handerMap,socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
/*    public void publisher(final Object service, int port){
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while (true){
                System.out.println("服务监听中..................");
                Socket socket = serverSocket.accept();
                threadPool.execute(new ProcessorHandler(service,socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
}
