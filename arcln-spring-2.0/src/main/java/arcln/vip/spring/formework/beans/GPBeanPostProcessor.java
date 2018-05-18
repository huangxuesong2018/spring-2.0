package arcln.vip.spring.formework.beans;
//用于 做事件监听 的
public class GPBeanPostProcessor {
    public Object postProcessBeforeInitialization(Object bean,String beanName){
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean,String beanName){
        return bean;
    }
}
