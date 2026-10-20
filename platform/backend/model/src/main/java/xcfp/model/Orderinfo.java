package xcfp.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单状态机：created -> paid -> shipped -> completed；created 可 cancelled。
 */
@Data
@TableName("orderinfo")
public class Orderinfo {

    public static final String STATE_CREATED = "created";
    public static final String STATE_PAID = "paid";
    public static final String STATE_SHIPPED = "shipped";
    public static final String STATE_COMPLETED = "completed";
    public static final String STATE_CANCELLED = "cancelled";

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userid;
    private Long supid;
    private String orderid;
    private Long productId;
    private String proname;
    private String picurl;
    private String supname;
    private BigDecimal price;
    private BigDecimal pay;
    private Integer num;
    private Long addid;
    private String receiverName;
    private String receiverTel;
    private String receiverAddress;
    private String state;
    private String express;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime completeTime;
    private LocalDateTime cancelTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    @JsonIgnore
    private Integer isDelete;

    public String getStateText() {
        if (state == null) {
            return "";
        }
        switch (state) {
            case STATE_CREATED:
                return "待付款";
            case STATE_PAID:
                return "待发货";
            case STATE_SHIPPED:
                return "待收货";
            case STATE_COMPLETED:
                return "已完成";
            case STATE_CANCELLED:
                return "已取消";
            default:
                return state;
        }
    }
}
