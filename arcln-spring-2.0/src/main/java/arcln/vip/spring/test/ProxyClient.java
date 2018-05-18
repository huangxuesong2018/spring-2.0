package arcln.vip.spring.test;

import java.lang.reflect.Proxy;

public class ProxyClient {
    public static void main(String[] args) {
        LogService instance = new LogServiceImpl();
        LogService proxy = (LogService)new LogProxy().CaseProxy(instance);
        String msg = proxy.log("HA HA AH");
        System.out.println("log:"+msg);

        proxy.log22("HA HA AH");
    }
}
