package com.arcln.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class WatcherDemo {
    public static void main(String[] args) throws Exception {
        final CountDownLatch downLatch = new CountDownLatch(1);
        final ZooKeeper zookeeper = new ZooKeeper("192.168.1.128:2181,192.168.1.129:2181,192.168.1.130:2181", 4000,
                new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        System.out.println("全局默认事件:"+event.getType()+"->"+event.getPath()+"->"+event.getState());
                        if(event.getState() == Event.KeeperState.SyncConnected)
                            downLatch.countDown();
                    }
                });
        downLatch.await();
        String path = "/zk-persis-b";
        zookeeper.create(path,"x".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        //通过  exists  getData  getchildren 绑定事件
        //通过 exists绑定事件，watcher只会绑定一次事件
        Stat stat = zookeeper.exists(path, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println(watchedEvent.getType()+"->"+watchedEvent.getPath());
                try {
                    zookeeper.exists(watchedEvent.getPath(),true);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //通过修改值 ，来触发事件
        stat = zookeeper.setData(path,"y".getBytes(),stat.getVersion());
        //通过删除值 ，来触发事件
        zookeeper.delete(path,stat.getVersion());
    }
}
