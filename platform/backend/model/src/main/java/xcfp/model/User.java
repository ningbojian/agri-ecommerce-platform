package xcfp.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
  @TableId(type = IdType.AUTO)
  private Long id;
  private String tel;
  /** 只允许写入，序列化时永不返回 */
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;
  private String nickname;
  private String avatar;
  private Integer status;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
  @TableLogic
  @JsonIgnore
  private Integer isDelete;
}
