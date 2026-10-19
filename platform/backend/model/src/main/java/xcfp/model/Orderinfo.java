package xcfp.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Kerr
 * @create 2021-10-27 1:51
 */
@Data
@TableName("orderinfo")
public class Orderinfo {
    @TableId(type = IdType.AUTO)
    private long id;
    private long userid;
    private long supid;
    private String orderid;
    private String proname;
    private String supname;
    private double price;
    private double pay;
    private long num;
    private long addid;
    private String state;
    private String express;
    @TableLogic
    private long isDelete;
}
