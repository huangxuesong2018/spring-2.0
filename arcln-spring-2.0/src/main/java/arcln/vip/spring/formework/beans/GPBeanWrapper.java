package arcln.vip.spring.formework.beans;

import arcln.vip.spring.formework.aop.GPAopConfig;
import arcln.vip.spring.formework.aop.GPAopProxy;
import arcln.vip.spring.formework.core.GPFactoryBean;

public class GPBeanWrapper extends GPFactoryBean {
    private GPAopProxy aopProxy = new GPAopProxy();

    public GPBeanPostProcessor getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(GPBeanPostProcessor postProcessor) {
        this.postProcessor = postProcessor;
    }

    //还会用到 观察者 模式
    //1.支付事件响应 会有一个监听
    private GPBeanPostProcessor postProcessor;

    private Object wrappedInstance;
    //原始的通过反射new出来，要把包装起来，存下来
    private Object originalInstance;


    public GPBeanWrapper(Object instance){
        this.wrappedInstance = aopProxy.getProxy(instance);
        this.originalInstance = instance;
    }

    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }

    public Object getOriginalInstance() {
        return originalInstance;
    }

    //返回代理以后的class
    //可能是这个 $Proxy0
    public Class<?> getWrappedClass(){
        return this.wrappedInstance.getClass();
    }

    public void setAopProxy(GPAopConfig config){
        aopProxy.setConfig(config);
    }


}
