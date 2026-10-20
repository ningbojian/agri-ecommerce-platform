package xcfp.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xcfp.common.result.Result;
import xcfp.model.Feedback;
import xcfp.user.service.FeedbackService;
import xcfp.vo.feedback.ReplyRequest;

import javax.validation.Valid;

@Api(tags = "管理端反馈")
@RestController
@RequestMapping("/admin/feedback")
public class AdminFeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @ApiOperation("反馈分页")
    @GetMapping("/page")
    public Result<Page<Feedback>> page(@RequestParam(defaultValue = "1") long current,
                                       @RequestParam(defaultValue = "10") long limit,
                                       @RequestParam(required = false) Integer status) {
        return Result.ok(feedbackService.pageAll(current, limit, status));
    }

    @ApiOperation("回复反馈")
    @PostMapping("/{id}/reply")
    public Result<Feedback> reply(@PathVariable Long id,
                                  @Valid @RequestBody ReplyRequest request) {
        return Result.ok(feedbackService.reply(id, request));
    }
}
