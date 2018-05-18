package arcln.vip.spring.demo.action;


import arcln.vip.spring.demo.service.IDemoService;
import arcln.vip.spring.formework.annotation.GPAutowried;
import arcln.vip.spring.formework.annotation.GPController;
import arcln.vip.spring.formework.annotation.GPRequestMapping;
import arcln.vip.spring.formework.annotation.GPRequestParam;
import arcln.vip.spring.formework.webmvc.GPModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@GPController
@GPRequestMapping("/demo")
public class DemoAction {
    @GPAutowried
    private IDemoService demoService;
    //我只是一个普通的方法
    private String normarlFiel;

    @GPRequestMapping(value = "/add*.json")
    public GPModelAndView add(HttpServletRequest rep, HttpServletResponse response, @GPRequestParam(value = "name") String name){
        String result = demoService.add(name);
        return out(response,result);
    }

    @GPRequestMapping(value = "/first.html")
    public GPModelAndView query(HttpServletRequest rep, HttpServletResponse response, @GPRequestParam("name") String name,
                                @GPRequestParam("address") String address){
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("name",name);
        model.put("address",address);
        return new GPModelAndView("first.html",model);
    }

    @GPRequestMapping(value = "/update.json")
    public GPModelAndView update(HttpServletRequest rep, HttpServletResponse response, String name,@GPRequestParam("id")Integer id){
        String result = demoService.update(name);
        return out(response,result);
    }

    private GPModelAndView out(HttpServletResponse resp,String str){
        try{
            resp.getWriter().write(str);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
