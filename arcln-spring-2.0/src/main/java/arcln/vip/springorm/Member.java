package arcln.vip.springorm;

import javax.persistence.Table;
import java.io.Serializable;

@Table(name="PROD.T_TEST_MEMBER")
public class Member implements Serializable{
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
