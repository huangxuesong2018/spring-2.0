package arcln.vip.springorm;

import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrmTest<T> {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        OrmTest t = new OrmTest();
        List<Member> list = t.selectTable(Member.class);
        for (Member s : list) {
            System.out.println(s.getId()+"\t"+s.getId()+"\t"+s.getName());
        }
    }

    /**
     * 把以映射到类实例中
     * @param clazz
     * @param valueMap
     * @return
     * @throws Exception
     */
    public T mapping(Class clazz, Map<String,Object> valueMap) throws Exception {
        T instance = (T) clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields ) {
            if(valueMap.containsKey(f.getName().toLowerCase())){
                Object value = valueMap.get(f.getName().toLowerCase());
                f.setAccessible(true);
                f.set(instance,valueMap.get(f.getName()));

            }
        }
        return instance;
    }

    /**
     * 通过类得到表名
     * @param clazz
     * @return
     */
    public String getTableNameByClass(Class clazz){
        if(clazz.isAnnotationPresent(Table.class)){
            Table table = (Table) clazz.getAnnotation(Table.class);
            return table.name();
        }
        return clazz.getSimpleName();
    }


    public  List<T> selectTable(Class clazz){
        List<T> list = new ArrayList<T>();
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@//192.168.1.193:1521/orcl";
            String name ="prod";
            String pwd = "Ecp7YTXtestdb2TestProd";
            Connection connection = DriverManager.getConnection(url,name,pwd);

            //得到表名
            String tableName = getTableNameByClass(clazz);

            PreparedStatement pstmt = connection.prepareStatement("select * from "+ tableName);
            ResultSet rs =  pstmt.executeQuery();
            //一共有多少列
            int column =  rs.getMetaData().getColumnCount();
            while (rs.next()){
                //遍历所有列
                T instance = (T) clazz.newInstance();
                for (int i = 1; i<= column; i++){
                    String columnName = rs.getMetaData().getColumnName(i);
                    Field field = clazz.getDeclaredField(columnName.toLowerCase());
                    field.setAccessible(true);
                    field.set(instance,rs.getObject(columnName));
                }
                list.add(instance);
            }
            rs.close();
            pstmt.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
