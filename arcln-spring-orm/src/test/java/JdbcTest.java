import com.arcln.orm.demo.model.Member;

import javax.persistence.Table;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Tom on 2018/5/9.
 */
public class JdbcTest<T>{
    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        JdbcTest t = new JdbcTest();
        List<Member> list = t.selectTable(Member.class);
        for (Member s : list) {
            System.out.println(s.getId()+"\t"+s.getId()+"\t"+s.getName());
        }
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
                    Object type = field.getType();
                    if(Long.class == type){
                        Long v = rs.getLong(columnName);
                        field.set(instance,v);
                    }else if(String.class == type){
                        field.set(instance,rs.getString(columnName));
                    }else if(Integer.class == type){
                        field.set(instance,rs.getInt(columnName));
                    }
                    // field.set(instance,rs.getObject(columnName));
                   // System.out.println(rs.getObject(columnName));
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
