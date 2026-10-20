package xcfp.user.payment;

import xcfp.model.Orderinfo;

/**
 * 支付渠道抽象。当前默认使用模拟支付，后续接入微信支付时新增实现即可。
 */
public interface PaymentGateway {

    PaymentResult pay(Orderinfo order);

    String provider();
}
