package arcln.vip.spring.formework.context;

import arcln.vip.spring.formework.annotation.GPAutowried;
import arcln.vip.spring.formework.annotation.GPController;
import arcln.vip.spring.formework.annotation.GPService;
import arcln.vip.spring.formework.aop.GPAopConfig;
import arcln.vip.spring.formework.beans.GPBeanDefinition;
import arcln.vip.spring.formework.beans.GPBeanPostProcessor;
import arcln.vip.spring.formework.beans.GPBeanWrapper;
import arcln.vip.spring.formework.context.support.GPBeanDefinitionReader;
import arcln.vip.spring.formework.core.GPBeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GPApplicationContext extends GPDefaultListableBeanFactory implements GPBeanFactory {
    private String[] configLocations;

    //用来保证注册式单例的容器
    private Map<String,Object> beanCacheMap = new HashMap<String,Object>();
    private GPBeanDefinitionReader reader;
    //用来存储所有的被代理过的对象
    private Map<String,GPBeanWrapper>beanWrapperMap = new ConcurrentHashMap<String,GPBeanWrapper>();

    public GPApplicationContext(String ... configLocations){
        this.configLocations = configLocations;
        this.refresh();
    }

    public void refresh(){

        //定位  <定位指定包名下的所有类路径>
        this.reader = new GPBeanDefinitionReader(configLocations);

        //加载
        List<String> beanDefinitionList = reader.loadBeanDefinition();

        //注册
        doRegistry(beanDefinitionList);

        //依赖注入(lazy-init = false 要执行依赖注入)
        //在这里自动调用 getBean方法
        doAutorited();
    }
    //开始执行自动化的依赖注入
    private void doAutorited() {
        for(Map.Entry<String,GPBeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()){
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                Object bean = getBean(beanName);
            }
        }
    }

    public void populateBean(String beanName,Object instance){
        Class<?> clazz = instance.getClass();
        if(!(clazz.isAnnotationPresent(GPController.class)
                || clazz.isAnnotationPresent(GPService.class))){
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields){
            if(!field.isAnnotationPresent(GPAutowried.class))continue;

            GPAutowried autowried = field.getAnnotation(GPAutowried.class);
            String autowriedName = autowried.value().trim();

            if("".equals(autowriedName)){
                autowriedName = field.getType().getName();
            }

            field.setAccessible(true);
            try {
                GPBeanWrapper beanWrapper = this.beanWrapperMap.get(autowriedName);
                //这个时候 ，Bean中的属性，有可能还未注入进spring工厂中
                Object ov = null;
                if(beanWrapper == null){
                    Object o = getBean(autowriedName);
                    beanWrapper = new GPBeanWrapper(o);
                }
                field.set(instance,beanWrapper.getOriginalInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 这个方法就是真正的 将BeanDefinition注册 到beanDefinitionMap
     * @param beanDefinitions  所有的类路径，仅仅是类路径而已
     */
    private void doRegistry(List<String> beanDefinitions) {
        try {
            //beanName 有三种情况
            //1.默认是类名首字母小写
            //2.自定义名字
            //3.接口注入
            for(String classname : beanDefinitions){
                Class<?> beanClass = Class.forName(classname);
                //如果是一个接口，是不能实例化的 ，用它的实现类
                if(beanClass.isInterface()){continue;}

                GPBeanDefinition beanDefinition = reader.registerBean(classname);
                if(beanDefinition != null){
                    //如果是多个实现类，只能覆盖
                    //为什么,因这spring没有那么智能,就是这么傻
                    //这个时候，可以自定义名字
                    this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
                }

                Class<?>[] interfaces =  beanClass.getInterfaces();
                for (Class<?> i : interfaces){
                    this.beanDefinitionMap.put(i.getName(),beanDefinition);
                }
                //到这里，容器初始化完毕
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    //依赖注入 从这里开始   通过读取 beanDefinition中的信息，然后，通过反射，创建一个实例并返回
    //spring的做法是,不会把最原始的对象放出去,会用一个beanWrapper来进行一次包装
    //装饰器模式
    //1，保留原来的OOP关系
    //2.我需要对它进行扩展，增强(为了以后AOP打基础)
    @Override
    public Object getBean(String beanName) {
        GPBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        String className = beanDefinition.getBeanClassName();
        try{
            //生成通知事件
            GPBeanPostProcessor beanPostProcessor = new GPBeanPostProcessor();

            Object instance = instantionBean(beanDefinition);
            if(instance == null )return null;
            //在实初始化以前调用一
            beanPostProcessor.postProcessBeforeInitialization(instance,className);

            GPBeanWrapper beanWrapper = new GPBeanWrapper(instance);
            beanWrapper.setAopProxy(instanceAopConfig(beanDefinition));
            beanWrapper.setPostProcessor(beanPostProcessor);
            this.beanWrapperMap.put(className,beanWrapper);

            //在实例初始化以后调用一次
            beanPostProcessor.postProcessAfterInitialization(instance,className);

            //通过反射，生成依赖的实例
            populateBean(beanName,instance);//beanWrapper.getOriginalInstance()

            //通过这要一调，相当于给我们自已留有了可操作空间
            return this.beanWrapperMap.get(className).getWrappedInstance();
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     * @param beanDefinition 扫描到的所有类
     * @return
     * @throws Exception
     */
    private GPAopConfig instanceAopConfig(GPBeanDefinition beanDefinition) throws Exception{
        GPAopConfig config = new GPAopConfig();
        String expression = reader.getConfig().getProperty("pointCut");
        String[] before =  reader.getConfig().getProperty("aspectBefore").split("\\s");
        String[] after =  reader.getConfig().getProperty("aspectAfter").split("\\s");

        String className = beanDefinition.getBeanClassName();
        Class<?> clazz = Class.forName(className);

        Pattern pattern = Pattern.compile(expression);
        Class<?> aspectClass = Class.forName(before[0]);

        for (Method method : clazz.getMethods()){
            //public .* arcln\.vip\.spring\.demo\.service\..*Service\..*\(.*\)
            //public java.lang.String arcln.vip.spring.demo.service.DemoService.get(java.lang.String)
            Matcher matcher = pattern.matcher(method.toString());
            if(matcher.matches()){
                //把能满足切面规则的类，添加到AOP的配置中
                config.put(method,aspectClass.newInstance(),new Method[]{aspectClass.getMethod(before[1]),aspectClass.getMethod(after[1])});
            }
        }

        return config;
    }

    //传一个beanDefinition就返回一个实例Bean
    private Object instantionBean(GPBeanDefinition beanDefinition){
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try {
            //因为根据class 才能确定一个类是否有实例
            if(this.beanCacheMap.containsKey(className)){
                instance = this.beanCacheMap.get(className);
            }else{
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
            }
            this.beanCacheMap.put(className,instance);
        }catch (Exception e){
            e.printStackTrace();
        }
        return instance;
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }
}
