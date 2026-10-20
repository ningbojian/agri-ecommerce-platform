package xcfp.vo.product;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ArticleSaveRequest {
    @NotBlank(message = "标题不能为空")
    @Size(max = 128, message = "标题过长")
    private String title;

    @Size(max = 255, message = "摘要过长")
    private String summary;

    @NotBlank(message = "正文不能为空")
    private String content;

    @Size(max = 512, message = "封面地址过长")
    private String coverUrl;

    @Size(max = 64, message = "作者名过长")
    private String author;

    private Integer status;
}
