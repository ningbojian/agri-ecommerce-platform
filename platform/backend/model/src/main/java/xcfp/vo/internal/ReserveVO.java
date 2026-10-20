package xcfp.vo.internal;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 库存预占成功后返回的商品快照，用于服务端定价和订单快照。
 */
@Data
public class ReserveVO {
    private Long id;
    private String proname;
    private String picurl;
    private Long supid;
    private String supname;
    private BigDecimal price;
    private Integer stockAfter;
}
