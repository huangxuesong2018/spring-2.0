package arcln.vip.spring.formework.webmvc;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 设计这个类的主要目的
 * 1.将一个静态文件 变成一个动态文件
 * 2.根据用户传送参数不同，产生不同的结果
 * 最终输出字符串，交给response
 */
public class GPViewResolver {
    private String viewName;
    private File templateFile;

    public GPViewResolver(String viewName,File
            templateFile) {
        this.viewName =viewName;
        this.templateFile = templateFile;

    }

    public String viewResolver(GPModelAndView mv) throws Exception{
        StringBuffer sb = new StringBuffer();

        RandomAccessFile ra = new RandomAccessFile(this.templateFile,"r");

        String line = null;
        while (null != (line = ra.readLine())){
            Matcher m = matcher(line);
            while (m.find()){
                for (int i = 1; i <= m.groupCount(); i++){
                    String paramName = m.group(i);
                    Object paramValue = mv.getModel().get(paramName);
                    if(null == paramValue) continue;

                    line = line.replaceAll("#\\{"+paramName+"\\}",paramValue.toString());
                }
            }

            sb.append(line);
        }
        return sb.toString();
    }

    private static Matcher matcher(String str){
        Pattern pattern = Pattern.compile("#\\{(.+?)\\}",Pattern.CASE_INSENSITIVE);
        return pattern.matcher(str);
    }

    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("/demo/add.*.json");
        String url = "/demo/addB.json";
        Matcher matcher = pattern.matcher(url);
        if(matcher.matches()){
            System.out.printf("y");
        }else System.out.printf("n");

    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
}
