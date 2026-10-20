package xcfp.user.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import xcfp.common.exception.XcfpException;
import xcfp.common.result.Result;
import xcfp.common.result.ResultCodeEnum;
import xcfp.vo.internal.ReserveRequest;
import xcfp.vo.internal.ReserveVO;

/**
 * 调用商品服务的内部库存接口。
 */
@Component
public class ProductClient {

    private static final Logger log = LoggerFactory.getLogger(ProductClient.class);
    private static final String INTERNAL_HEADER = "X-Internal-Token";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${xcfp.product-service-url:http://127.0.0.1:8201}")
    private String productServiceUrl;

    @Value("${xcfp.internal-token:xcfp-local-internal-token}")
    private String internalToken;

    public ProductClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 预占库存。失败时抛出带业务码的 XcfpException（4201 库存不足 / 4203 商品不可售）。
     */
    public ReserveVO reserve(Long productId, Integer num) {
        Result result = post("/internal/product/reserve", productId, num);
        if (result.getCode() == null || !Integer.valueOf(200).equals(result.getCode())) {
            throw new XcfpException(
                    result.getMessage() == null ? "库存预占失败" : result.getMessage(),
                    result.getCode() == null ? ResultCodeEnum.SERVICE_ERROR.getCode() : result.getCode());
        }
        return objectMapper.convertValue(result.getData(), ReserveVO.class);
    }

    /**
     * 释放库存。订单落库失败或取消订单时调用。
     */
    public void release(Long productId, Integer num) {
        Result result = post("/internal/product/release", productId, num);
        if (result.getCode() == null || !Integer.valueOf(200).equals(result.getCode())) {
            log.warn("Release stock failed: productId={}, num={}, message={}", productId, num, result.getMessage());
            throw new XcfpException(
                    result.getMessage() == null ? "库存释放失败" : result.getMessage(),
                    result.getCode() == null ? ResultCodeEnum.SERVICE_ERROR.getCode() : result.getCode());
        }
    }

    private Result post(String path, Long productId, Integer num) {
        ReserveRequest request = new ReserveRequest();
        request.setProductId(productId);
        request.setNum(num);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(INTERNAL_HEADER, internalToken);

        try {
            ResponseEntity<Result> response = restTemplate.postForEntity(
                    productServiceUrl + path, new HttpEntity<>(request, headers), Result.class);
            Result body = response.getBody();
            if (body == null) {
                throw new XcfpException("商品服务返回为空", ResultCodeEnum.SERVICE_ERROR.getCode());
            }
            return body;
        } catch (RestClientException e) {
            log.error("Call product service failed: {}", path, e);
            throw new XcfpException("商品服务不可用，请稍后重试", ResultCodeEnum.SERVICE_ERROR.getCode());
        }
    }
}
