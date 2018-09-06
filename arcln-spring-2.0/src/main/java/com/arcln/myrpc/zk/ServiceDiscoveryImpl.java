package com.arcln.myrpc.zk;

import com.arcln.myrpc.zk.loadbanace.LoadBanalce;
import com.arcln.myrpc.zk.loadbanace.RandomLoadBanance;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;

public class ServiceDiscoveryImpl implements IServiceDiscovery {
    private CuratorFramework curatorFramework;
    //提供服务的节点集合
    private List<String> reps;
    public ServiceDiscoveryImpl(String address) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        curatorFramework = CuratorFrameworkFactory.builder().
                connectString(address).
                sessionTimeoutMs(4000).
                retryPolicy(retryPolicy).build();
        curatorFramework.start();
    }

    @Override
    public String discovery(String serviceName) {
        //  path -> /register/com.arcln.myrpc.IGpHello
        String servicePath = ZkConfig.ZK_REGISTRYS_PATH +"/" + serviceName;
        try {
            reps = curatorFramework.getChildren().forPath(servicePath);
        } catch (Exception e) {
            throw new RuntimeException("获取子节点异常："+e);
        }
        registerWatcher(servicePath);
        //拿到多个节点的ip后，做负载
        LoadBanalce loadBanalce = new RandomLoadBanance();
        String url = loadBanalce.selectHost(reps);
        System.out.println("集群服务器列表"+reps+",找到真正的服务地址-->"+url);
        return url;
    }

    //监听子节点的变化
    private void registerWatcher(final String path){
        try {
            //对子节点的监听
            PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework,path,true);
            childrenCache.start();
            PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                    reps = curatorFramework.getChildren().forPath(path);
                    System.out.println(Thread.currentThread().getName()+"监听到集群服务器的变化"+reps);
                }
            };
            childrenCache.getListenable().addListener(pathChildrenCacheListener);
        } catch (Exception e) {
            throw new RuntimeException("pathChildrenCacheListener",e);
        }
    }
}
