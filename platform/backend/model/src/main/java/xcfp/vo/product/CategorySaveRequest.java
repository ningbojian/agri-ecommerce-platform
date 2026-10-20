package xcfp.vo.product;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CategorySaveRequest {
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 32, message = "分类名称过长")
    private String name;

    @Size(max = 512, message = "图标地址过长")
    private String icon;

    private Integer sortOrder;

    private Integer status;
}
