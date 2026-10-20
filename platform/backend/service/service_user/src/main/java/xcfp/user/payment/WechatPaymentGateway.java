package xcfp.user.payment;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import xcfp.common.exception.XcfpException;
import xcfp.common.result.ResultCodeEnum;
import xcfp.model.Orderinfo;

/**
 * 微信支付占位实现。配置 xcfp.pay.provider=wechat 时启用。
 *
 * <p>接入真实支付时需要补充：商户号、证书、统一下单、回调验签与对账。
 * 本期不接入商户号，调用会明确报错。</p>
 */
@Component
@ConditionalOnProperty(name = "xcfp.pay.provider", havingValue = "wechat")
public class WechatPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult pay(Orderinfo order) {
        throw new XcfpException("未接入微信支付，请使用模拟支付", ResultCodeEnum.SERVICE_ERROR.getCode());
    }

    @Override
    public String provider() {
        return "WECHAT";
    }
}
