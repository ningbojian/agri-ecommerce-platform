package xcfp.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("product_info")
@Data
public class ProductInfo {
  @TableId(type = IdType.AUTO)
  private long id;
  private String proname;
  private long supid;
  private String supname;
  private String delivery;
  private double price;
  private long publishStatus;
  private long auditStatus;
  private String descript;
  private String picurl;
  private String sort;
  private java.sql.Timestamp createTime;
  private java.sql.Timestamp updateTime;
  @TableLogic
  private long isDelete;



}
