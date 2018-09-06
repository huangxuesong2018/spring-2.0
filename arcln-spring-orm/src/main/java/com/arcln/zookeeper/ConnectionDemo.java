package com.arcln.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 原生的 zookeeper API
 */
public class ConnectionDemo {
    public static void main(String[] args) throws Exception {
        final CountDownLatch downLatch = new CountDownLatch(1);
        final ZooKeeper zookeeper = new ZooKeeper("192.168.1.128:2181,192.168.1.129:2181,192.168.1.130:2181",
                4000,
                new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        if(event.getState() == Event.KeeperState.SyncConnected){
                            downLatch.countDown();
                        }
                    }
                });
        downLatch.await();//为了等待zooKeeper连接成功
        System.out.println("连接状态--------->"+zookeeper.getState()+" <--------------");
        String nodeName = "/zk-persis-TEST";
        /** 创建节点
         * @param String path, 节点路径
         * @param byte[] data, value值
         * @param List<ACL> acl, 开放权限
         * @param  CreateMode createMode，存储模式
         */
        zookeeper.create(nodeName,"0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //Stat中保存了zookeeper节点中对应的所有信息
        Stat stat = new Stat();
        //得到当前的节点
        byte[] bytes = zookeeper.getData(nodeName,null,stat);
        System.out.println("创建时的值："+new String(bytes)+" ");

        //修改节点
        zookeeper.setData(nodeName,"1".getBytes(),stat.getVersion());

        //得到当前的节点
        byte[] bytes1 = zookeeper.getData(nodeName,null,stat);
        System.out.println("修改后的值："+new String(bytes1)+" ");

        //删除
        zookeeper.delete(nodeName,stat.getVersion());
        zookeeper.close();
       // System.in.read();
    }
}
