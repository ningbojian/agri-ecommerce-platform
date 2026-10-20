package xcfp.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_address")
public class UserAddress {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userid;
    private String address;
    private String addtel;
    private String addname;
    private Integer isDefault;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    @JsonIgnore
    private Integer isDelete;
}
