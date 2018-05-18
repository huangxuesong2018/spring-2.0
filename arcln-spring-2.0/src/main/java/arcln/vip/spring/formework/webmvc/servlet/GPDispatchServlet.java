package arcln.vip.spring.formework.webmvc.servlet;


import arcln.vip.spring.formework.annotation.GPController;
import arcln.vip.spring.formework.annotation.GPRequestMapping;
import arcln.vip.spring.formework.annotation.GPRequestParam;
import arcln.vip.spring.formework.aop.GPAOPProxyUtil;
import arcln.vip.spring.formework.context.GPApplicationContext;
import arcln.vip.spring.formework.webmvc.GPHandlerAdapter;
import arcln.vip.spring.formework.webmvc.GPHandlerMapping;
import arcln.vip.spring.formework.webmvc.GPModelAndView;
import arcln.vip.spring.formework.webmvc.GPViewResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//只是作为一个mvc 的启动入口
public class GPDispatchServlet extends HttpServlet {

    // private Map<String,GPHandlerMapping> handlerMapping = new HashMap<String,GPHandlerMapping>();
    //这样设计的精典之处，handlerMappings是springMVC中最核心的设计，也是最经典的
    private List<GPHandlerMapping> handlerMappings = new ArrayList<GPHandlerMapping>();

    //private List<GPHandlerAdapter> handlerAdapters = new ArrayList<GPHandlerAdapter>();
    private Map<GPHandlerMapping, GPHandlerAdapter> handlerAdapters = new HashMap<GPHandlerMapping, GPHandlerAdapter>();

    private List<GPViewResolver> viewResolvers = new ArrayList<GPViewResolver>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        GPApplicationContext context = new GPApplicationContext(config.getInitParameter("contextConfigLocation"));
        initStrategies(context);
    }


    //SpirngMVC,九大组件
    protected void initStrategies(GPApplicationContext context) {
        initMultipartResolver(context);
        initLocaleResolver(context);
        initThemeResolver(context);
        initHandlerExceptionResolvers(context);
        initRequestToViewNameTranslator(context);
        initFlashMapManager(context);

        //HandlerMappings用来保存Controller中配置的RequestMapping和Method的一个对应关系
        initHandlerMappings(context);
        //HandlerAdapters 用来动态匹配Method参数,包括类转换，动态赋值
        initHandlerAdapters(context);
        //自已解析一套模板语言
        initViewResolvers(context);

    }

    private void initFlashMapManager(GPApplicationContext context) {
    }

    private void initRequestToViewNameTranslator(GPApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(GPApplicationContext context) {
    }

    private void initThemeResolver(GPApplicationContext context) {
    }

    private void initLocaleResolver(GPApplicationContext context) {
    }

    private void initMultipartResolver(GPApplicationContext context) {
    }

    //HandlerMappings用来保存Controller中配置的RequestMapping和Method的一个对应关系
    private void initHandlerMappings(GPApplicationContext context) {
        //按照通常的理解 是一个Map<String,Method> map
        // map.put(url,)

        String[] beanNames = context.getBeanDefinitionNames();
        try {
            for (String name : beanNames) {
                Object proxy = context.getBean(name);
                Object controller = GPAOPProxyUtil.getTargetObject(proxy);
                Class<?> clazz = controller.getClass();

                if (!clazz.isAnnotationPresent(GPController.class)) continue;

                String baseUrl = "";
                //查找配置在GPController类上的GPRequestMapping的路径
                if (clazz.isAnnotationPresent(GPRequestMapping.class)) {
                    GPRequestMapping requestMapping = clazz.getAnnotation(GPRequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                //扫描所有的public 方法
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(GPRequestMapping.class)) continue;

                    GPRequestMapping requestMapping = method.getAnnotation(GPRequestMapping.class);
                    String regex = ("/" + baseUrl + requestMapping.value()).replaceAll("\\*",".\\*").replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new GPHandlerMapping(pattern, controller, method));
                    System.out.println("Mapping:" + regex + "," + method);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //HandlerAdapters 用来动态匹配Method参数,包括类转换，动态赋值
    private void initHandlerAdapters(GPApplicationContext context) {
        //在初始化阶段
        for (GPHandlerMapping handlerMapping : this.handlerMappings) {
            Map<String, Integer> paramMapping = new HashMap<String, Integer>();
            //这里只是出来的命名参数
            Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
            for (int i = 0; i < pa.length; i++) {
                for (Annotation a : pa[i]) {
                    if (a instanceof GPRequestParam) {
                        String paramName = ((GPRequestParam) a).value();
                        if (!"".equals(paramName.trim())) {
                            paramMapping.put(paramName, i);
                        }
                    }
                }
            }
            //处理非命名参数
            //只处理Request和response
            Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
            for (int i = 0; i < paramTypes.length; i++) {
                Class<?> type = paramTypes[i];
                if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
                    paramMapping.put(type.getName(), i);
                }
            }

            this.handlerAdapters.put(handlerMapping, new GPHandlerAdapter(paramMapping));
        }
    }

    //自已解析一套模板语言
    private void initViewResolvers(GPApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);
        for (File template : templateRootDir.listFiles()) {
            this.viewResolvers.add(new GPViewResolver(template.getName(), template));
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
        System.out.println("--------------------------------");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception");
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //根据用户请求
        GPHandlerMapping handler = getHandler(req);
        if(handler == null){
            resp.getWriter().write("404 Not found");
            return ;
        }

        GPHandlerAdapter ha = getHandlerAdapter(handler);
        //这一步只是调用方法 得到返回值
        GPModelAndView mv = ha.handle(req, resp, handler);
        // 这一步才是真正的输出
        processDispatchResult(resp, mv);
    }

    private void processDispatchResult(HttpServletResponse resp, GPModelAndView mv) throws Exception{
        //调用 viewResolver的resolveViewName方法
        if(null == mv) return ;

        if(this.viewResolvers.isEmpty()) return ;

        for (GPViewResolver gpViewResolver :  this.viewResolvers){
            if(!mv.getViewName().equals(gpViewResolver.getViewName()))continue;

            String out = gpViewResolver.viewResolver(mv);
            if(out != null){
                resp.getWriter().write(out);
            }
        }
    }

    private GPHandlerAdapter getHandlerAdapter(GPHandlerMapping gpHandlerMapping) {
        if(this.handlerAdapters.isEmpty())return null;
        return this.handlerAdapters.get(gpHandlerMapping);
    }

    protected GPHandlerMapping getHandler(HttpServletRequest req) {
        if(this.handlerMappings.isEmpty()) return null;
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();

        url = url.replace(contextPath,"").replaceAll("/+","/");

        for (GPHandlerMapping handlerMapping : this.handlerMappings ) {
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            if(!matcher.matches())continue;
            return handlerMapping;
        }
        return null;
    }
}
