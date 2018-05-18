package arcln.vip.spring.demo.service;

import arcln.vip.spring.formework.annotation.GPService;

@GPService
public class DemoService implements IDemoService{
    @Override
    public String get(String name) {
        return "method get --> this name is ="+name;
    }

    @Override
    public String add(String name) {
        System.out.println("DemoService String add() --> this name is ="+name);
        return "method add --> this name is ="+name;
    }

    @Override
    public String update(String name) {
        System.out.println("DemoService String update() --> this name is ="+name);
        return "method update --> this name is ="+name;
    }
}
