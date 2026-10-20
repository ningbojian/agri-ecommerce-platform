package xcfp.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment_record")
public class PaymentRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String payNo;
    private String orderid;
    private Long userid;
    private BigDecimal amount;
    private String channel;
    private String status;
    private String transactionId;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
}
