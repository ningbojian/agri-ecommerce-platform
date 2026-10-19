package xcfp.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Kerr
 * @create 2021-10-26 23:49
 */
@Data
@TableName("user_address")
public class UserAddress {
    @TableId(type = IdType.AUTO)
    private long id;
    private long userid;
    private String address;
    private String addtel;
    private String addname;
    @TableLogic
    private long isDelete;
}
