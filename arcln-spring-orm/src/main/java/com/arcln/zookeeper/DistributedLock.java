package com.arcln.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class DistributedLock implements Lock,Watcher{
    private ZooKeeper zk = null;
    private String ROOT_LOCK = "/locks2";//定义根节点
    private String WAIT_LOCK;//等待前一个锁
    private String CURRENT_LOCK;//表示当前锁
    private CountDownLatch countDownLatch;

    public DistributedLock(){
        try {
            zk = new ZooKeeper("192.168.1.128:2181",4000,this);
            //用来检查根节点是否存在
            Stat stat = zk.exists(ROOT_LOCK,false);
            if(stat == null){
                zk.create(ROOT_LOCK,"0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void lock() {
        if(tryLock()){
            System.out.println(Thread.currentThread().getName()+"->"+CURRENT_LOCK+"-->===============获取锁成功");
            return;
        }
        try {
            waitForLock(WAIT_LOCK);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean waitForLock(String prev) throws KeeperException, InterruptedException {
        //如果prev节点一直存在，则一直等待
        Stat stat = zk.exists(prev,true);
        if(stat != null){
            countDownLatch = new CountDownLatch(1);
            System.out.println(Thread.currentThread().getName()+"->等待锁"+prev+"的释放");
            countDownLatch.await();
            System.out.println(Thread.currentThread().getName()+"->获得锁");
        }
        return true;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        try {
            //在根节点下创建一个临时有序节点
            CURRENT_LOCK = zk.create(ROOT_LOCK+"/","0".getBytes(),
                    ZooDefs.Ids.READ_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(Thread.currentThread().getName()+"->"+CURRENT_LOCK+"，尝试竟争锁");

            //获得根节点下的所有子节点
            List<String> childrens = zk.getChildren(ROOT_LOCK,false);

            SortedSet<String> sortedSet = new TreeSet<String>();
            for (String children : childrens) {
                sortedSet.add(ROOT_LOCK+"/"+children);
            }
            //取出第一个节点

            String firstNode = sortedSet.first();
            System.out.println(CURRENT_LOCK+"----"+firstNode);
            if(CURRENT_LOCK.equals(firstNode)){//如果当前创建的节点是最小的节点，直接获取锁

                System.out.println(CURRENT_LOCK+"##############333"+firstNode);
                return true;
            }
            //取出比当前创建的节点还小的节点
            SortedSet<String> lessThenMe = sortedSet.headSet(CURRENT_LOCK);
            if(!lessThenMe.isEmpty()){
                //把上一个节点，做为当前节点要等待的锁
                WAIT_LOCK = lessThenMe.last();
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        System.out.println(Thread.currentThread().getName()+"->释放锁:"+CURRENT_LOCK);
        try {
            zk.delete(CURRENT_LOCK,-1);//version 为 -1时，指不管版本号是多少，都直接删除
            CURRENT_LOCK = null;
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public void process(WatchedEvent event) {
        if(this.countDownLatch != null){
            this.countDownLatch.countDown();
        }
    }
}
