package arcln.vip.spring.formework.context.support;

import arcln.vip.spring.formework.beans.GPBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 对配置文件进行查找 ，读取，解析
 */
public class GPBeanDefinitionReader {
    private Properties contextConfig = new Properties();
    //查找scanPacket下面所有的类和接口文件，保存类路径
    private List<String> registryBeanClass  =new ArrayList<String>();

    //在配置文件中用来获取自动扫描的包名的key
    private final String SCAN_PACKET = "scanPackage";

    /**
     * 解析配置文件,扫描Class
     * @param locations
     */
    public GPBeanDefinitionReader(String ... locations){
        //在spring中是通过Reader去查找定位的
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath*:",""));
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(null != is) {
                    is.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        doScanner(contextConfig.getProperty("scanPackage"));

    }

    /**
     * 递归扫描所有的相关联的class.并且保存到一个registryBeanClass  list中
     * registryBeanClass 指定包名下的所有类路径
     * @param packageName
     */
    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.","/"));
        File classDir = new File(url.getFile());

        for(File file : classDir.listFiles()){
            if(file.isDirectory()){
                doScanner(packageName+"."+file.getName());
            }else{
                registryBeanClass.add(packageName+"."+file.getName().replace(".class",""));
            }

        }
    }

    /**
     * 返回扫描到的所有类路径
     * @return
     */
    public List<String> loadBeanDefinition(){
        return this.registryBeanClass;
    }

    /**
     * 每注册一个className ,就返回一个BeanDifintion，自已包装
     * 只是为了对配置进行一个包装
     * @param className
     * @return
     */
    public GPBeanDefinition registerBean(String className){
        if(this.registryBeanClass.contains(className)){
            GPBeanDefinition beanDefinition = new GPBeanDefinition();
            beanDefinition.setBeanClassName(className);
            beanDefinition.setFactoryBeanName(className.substring(className.lastIndexOf(".")+1));
            return beanDefinition;
        }
        return null;
    }

    private static String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public Properties getConfig(){
        return this.contextConfig;
    }
}
