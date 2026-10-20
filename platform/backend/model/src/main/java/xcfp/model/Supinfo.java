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
@TableName("supinfo")
public class Supinfo {
  @TableId(type = IdType.AUTO)
  private Long id;
  private String suptel;
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String suppass;
  @JsonIgnore
  private String secode;
  private String supname;
  private String cardnum;
  @JsonIgnore
  private String signkey;
  private String logo;
  private String descript;
  private Integer auditStatus;
  private Integer status;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
  @TableLogic
  @JsonIgnore
  private Integer isDelete;
}
