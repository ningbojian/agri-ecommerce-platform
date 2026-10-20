package xcfp.user.payment;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import xcfp.model.Orderinfo;

import java.util.UUID;

/**
 * 模拟支付：不产生真实扣款，直接返回成功。
 * 配置 xcfp.pay.provider=mock（默认）时启用。
 */
@Component
@ConditionalOnProperty(name = "xcfp.pay.provider", havingValue = "mock", matchIfMissing = true)
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult pay(Orderinfo order) {
        return new PaymentResult(true, "MOCK", "MOCK-" + UUID.randomUUID().toString().replace("-", ""),
                "模拟支付成功");
    }

    @Override
    public String provider() {
        return "MOCK";
    }
}
