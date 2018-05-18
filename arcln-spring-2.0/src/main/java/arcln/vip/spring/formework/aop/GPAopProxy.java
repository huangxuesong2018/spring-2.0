package arcln.vip.spring.formework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

//默认jdk动态代理
public class GPAopProxy implements InvocationHandler{
    private GPAopConfig config;

    private Object target;


    public void setConfig(GPAopConfig config){
        this.config = config;
    }

    //把原生的对象
    public Object getProxy(Object instance){
        this.target = instance;
        Class<?> clazz = instance.getClass();
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(),clazz.getInterfaces(),this);
        return o;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method m =this.target.getClass().getMethod(method.getName(),method.getParameterTypes());
        if(config.contains(m)){
            GPAopConfig.GPAspect aspect = config.get(m);
            aspect.getPoints()[0].invoke(aspect.getAspect());
        }
        Object result = method.invoke(this.target,args);
        if(config.contains(m)){
            GPAopConfig.GPAspect aspect = config.get(m);
            aspect.getPoints()[1].invoke(aspect.getAspect());
        }

        return result;
    }
}
