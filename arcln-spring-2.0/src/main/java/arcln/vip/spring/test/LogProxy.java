package arcln.vip.spring.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class LogProxy implements InvocationHandler{
    private Object target;
    public Object CaseProxy(Object instance){
        this.target = instance;
        Object o = Proxy.newProxyInstance(instance.getClass().getClassLoader(),instance.getClass().getInterfaces(),this);
        return o;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("------------begin--------------");
        Object o =  method.invoke(target,args);
        System.out.println("------------end--------------");
        return o;
    }
}
