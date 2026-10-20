package xcfp.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("product_info")
@Data
public class ProductInfo {
  @TableId(type = IdType.AUTO)
  private Long id;
  private String proname;
  private Long supid;
  private String supname;
  private Long categoryId;
  private String delivery;
  private BigDecimal price;
  private Integer stock;
  private Integer sales;
  private String unit;
  private Integer publishStatus;
  private Integer auditStatus;
  private String descript;
  private String picurl;
  private String sort;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
  @TableLogic
  @JsonIgnore
  private Integer isDelete;
}
