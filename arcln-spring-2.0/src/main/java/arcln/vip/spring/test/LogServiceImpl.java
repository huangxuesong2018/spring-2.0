package arcln.vip.spring.test;

public class LogServiceImpl implements  LogService{
    @Override
    public String log(String msg) {
        return msg+"_LogServiceImpl";
    }

    @Override
    public String log22(String msg) {
        System.out.println("没有返回..................................");
        return msg;
    }
}
