package xcfp.vo.feedback;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class FeedbackRequest {
    @NotBlank(message = "反馈内容不能为空")
    @Size(max = 500, message = "反馈内容不能超过 500 个字符")
    private String content;

    @Size(max = 64, message = "联系方式过长")
    private String contact;
}
