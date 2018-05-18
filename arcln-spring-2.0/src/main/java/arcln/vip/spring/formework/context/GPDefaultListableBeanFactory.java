package arcln.vip.spring.formework.context;

import arcln.vip.spring.formework.beans.GPBeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GPDefaultListableBeanFactory extends GPAbstractApplicationContext{
    /**
     * beanDefinitionMap用来保存配置信息
     * 只是保存了一些类的配置类型,还没有开始实例化bean
     */
    protected Map<String,GPBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String,GPBeanDefinition>();

    @Override
    protected void refreshBeanFactory() {

    }

    @Override
    protected void onRefresh() throws Exception {
        // For subclasses: do nothing by default.
    }
}
