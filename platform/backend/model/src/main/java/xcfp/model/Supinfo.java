package xcfp.model;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("supinfo")
public class Supinfo {
  @TableId(type = IdType.AUTO)
  private long id;
  private String suptel;
  private String suppass;
  private String secode;
  private String supname;
  private String cardnum;
  private String signkey;
  @TableLogic
  private long isDelete;
}
