package com.arcln.MySerializable;

import java.io.*;

public class App {
    public static void main(String[] args) {
        /*ISerializable iSerializable = new  FileSerializer();

        User user = new User("Mic",18);
        user.setSex("男");
        user.setAge(50);
        user.setLike("菲菲");
        byte[] bytes =  iSerializable.serializer(user);

        System.out.println("--------------------");
        System.out.println("-------------------c-");
        User u = iSerializable.deSerializer(null,User.class);
        System.out.println(u+"\t"+u.getSex());*/

        try {
            File file = new File("user");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            User user = new User("Mic",18);
            user.setSex("男");
            user.setAge(50);
            user.setLike("菲菲");
            objectOutputStream.writeObject(user);

            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            User user1 = (User)objectInputStream.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
