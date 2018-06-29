package com.arcln.MySerializable;

import java.io.Serializable;

public class SuperMan implements Serializable{

    private String sex;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
