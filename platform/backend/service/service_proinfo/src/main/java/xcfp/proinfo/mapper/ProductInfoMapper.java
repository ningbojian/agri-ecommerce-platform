package xcfp.proinfo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import xcfp.model.ProductInfo;

/**
 * @author Kerr
 * @create 2021-10-13 19:17
 */
public interface ProductInfoMapper extends BaseMapper<ProductInfo> {

    /**
     * 原子扣减库存并累加销量。只有可售且库存充足时才会更新。
     */
    @Update("UPDATE product_info SET stock = stock - #{num}, sales = sales + #{num} " +
            "WHERE id = #{productId} AND stock >= #{num} AND publish_status = 1 " +
            "AND audit_status = 1 AND is_delete = 0")
    int deductStock(@Param("productId") Long productId, @Param("num") Integer num);

    /**
     * 取消订单时恢复库存并回退销量。
     */
    @Update("UPDATE product_info SET stock = stock + #{num}, " +
            "sales = CASE WHEN sales >= #{num} THEN sales - #{num} ELSE 0 END " +
            "WHERE id = #{productId} AND is_delete = 0")
    int restoreStock(@Param("productId") Long productId, @Param("num") Integer num);
}
