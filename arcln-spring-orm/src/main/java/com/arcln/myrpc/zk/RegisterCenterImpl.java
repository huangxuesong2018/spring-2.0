package com.arcln.myrpc.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class RegisterCenterImpl implements IRegisterCenter {
    private CuratorFramework curatorFramework;

    {   //连接zookeeper
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        curatorFramework = CuratorFrameworkFactory.builder().
                connectString(ZkConfig.CONNECTION_STR).
                sessionTimeoutMs(4000).
                retryPolicy(retryPolicy).build();
        curatorFramework.start();
    }

    /**
     * 在zookeeper中创建节点
     * @param serviceName 服务接口类的全路径（如:com.arcln.myrpc.IGpHello）
     * @param serviceAddress  服务接口类的远程接口地址(如: 127.0.0.1:8080)
     */
    @Override
    public void register(String serviceName, String serviceAddress) {
        //在zookeeper中的path
        String servicePath = ZkConfig.ZK_REGISTRYS_PATH +"/" + serviceName;

        try {
            //判断 /registrys/product-service 是否存在，不存在则创建
            if(curatorFramework.checkExists().forPath(servicePath) == null){
                //建立一个持久化节点，父节点不存在时自动创建父节点(/registrys/com.arcln.myrpc.IGpHello)
                curatorFramework.create().creatingParentsIfNeeded().
                        withMode(CreateMode.PERSISTENT).forPath(servicePath,"0".getBytes());
            }
            // path ->  /registrys/com.arcln.myrpc.IGpHello/127.0.0.1:8080
            String addressPath = servicePath + "/" + serviceAddress;
            //临时节点
            String rsNode = curatorFramework.create().withMode(CreateMode.EPHEMERAL).
                    forPath(addressPath,"0".getBytes());
            System.out.println("服务注册成功->"+rsNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
