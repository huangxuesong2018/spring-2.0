package com.arcln.MySerializable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class User extends SuperMan{

    private String name;
    private Integer age;
    private static int num = 5;
    private transient String like;

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException{
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(like);
    }
    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        like = (String) objectInputStream.readObject();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public static int getNum() {
        return num;
    }

    public static void setNum(int num) {
        User.num = num;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", like='" + like + '\'' +
                '}';
    }
}
