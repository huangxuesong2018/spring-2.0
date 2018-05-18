package arcln.vip.spring.formework.aop;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * 通过代理对象找出被代理的对象
 */
public class GPAOPProxyUtil {
    public static Object getTargetObject(Object proxy) throws Exception{
        //如果不是一个代理对象就直接返回
        if(!isAopProxy(proxy))return proxy;

        return getProxyTargetObject(proxy);
    }

    private static boolean isAopProxy(Object object){
        return Proxy.isProxyClass(object.getClass());
    }

    private static Object getProxyTargetObject(Object proxy) throws Exception{
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        GPAopProxy aopProxy = (GPAopProxy)h.get(proxy);
        Field target = aopProxy.getClass().getDeclaredField("target");
        target.setAccessible(true);
        return target.get(aopProxy);
    }
}
