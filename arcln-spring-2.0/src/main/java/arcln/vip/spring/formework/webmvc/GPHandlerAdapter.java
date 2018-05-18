package arcln.vip.spring.formework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

/**
 * 专人干专事
 */
public class GPHandlerAdapter {
    private Map<String,Integer> paramMappintg;

    public GPHandlerAdapter(Map<String,Integer> paramMappintg) {
        this.paramMappintg = paramMappintg;
    }

    /**
     *
     * @param req
     * @param resp
     * @param handler  为什么要把handler传进来，因为handler中包含了controler method ,url信息
     * @return
     */
    public GPModelAndView handle(HttpServletRequest req, HttpServletResponse resp, GPHandlerMapping handler) throws Exception{
        //根据用户请求的参数信息，跟method参数信息进行动态匹配
        //response传进来的目的，只是为了，将其赋值给方法参数，仅此而已


        //只有当用户传过来的 ModelAndView为空时，才会new一个默认的

        //1.要准备好 这个方法 的形参列表
        Class<?>[] paramTypes = handler.getMethod().getParameterTypes();
        //2.要拿到自定义的命名参数列表 所在的位置
        Map<String,String[]> reqParameterMap = req.getParameterMap();
        //3.构造实参列表
        Object[] paramValues = new Object[paramTypes.length];
        for (Map.Entry<String,String[]> param : reqParameterMap.entrySet()){
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]","").replaceAll("\\s",",");
            if(!this.paramMappintg.containsKey(param.getKey()))continue;

            int index = this.paramMappintg.get(param.getKey());
            //因为页面上传过来的值都是string类型的 ，而在方法中定义的类型是变化的

            paramValues[index] = caseStringValue(value,paramTypes[index]);
        }
        if(this.paramMappintg.get(HttpServletRequest.class.getName()) != null) {
            int reqIndex = this.paramMappintg.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }
        if(this.paramMappintg.get(HttpServletResponse.class.getName()) != null) {
            int respIndex = this.paramMappintg.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }


        //4.从handle中取出controller ,method
        Object result = handler.getMethod().invoke(handler.getController(),paramValues);
        if(result == null) return null;

        boolean isModelAndView =  handler.getMethod().getReturnType() == GPModelAndView.class;
        return isModelAndView ? (GPModelAndView)result : null;
    }

    private Object caseStringValue(String value,Class<?> clazz){
        if(clazz == String.class){
            return value;
        }else if(clazz == Integer.class || clazz == int.class){
            return Integer.parseInt(value);
        }else return null;

    }
}
