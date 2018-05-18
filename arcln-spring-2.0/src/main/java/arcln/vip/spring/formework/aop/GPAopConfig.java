package arcln.vip.spring.formework.aop;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 目标代理对象的一个方法 要增强
 * 用户自已实现的业务逻辑
 * 配置文件的目的，告诉spring哪些类的哪些方法要增强，增强的内容是什么?
 * 对配置文件中所体现的内容进行封装
 */
public class GPAopConfig {

    private Map<Method,GPAspect> points = new HashMap<Method,GPAspect>();

    public void put(Method target,Object aspect,Method[] points){
        this.points.put(target,new GPAspect(aspect,points));
    }

    public GPAspect get(Method method){
        return this.points.get(method);
    }

    public boolean contains(Method method){
        return this.points.containsKey(method);
    }

    public class GPAspect{
        private Object aspect;
        private Method[] points;
        public GPAspect(Object aspect, Method[] points) {
            this.aspect = aspect;
            this.points = points;
        }

        public Object getAspect() {
            return aspect;
        }

        public Method[] getPoints() {
            return points;
        }
    }
}
