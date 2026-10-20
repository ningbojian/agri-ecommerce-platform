package xcfp.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xcfp.common.auth.UserContext;
import xcfp.common.result.Result;
import xcfp.model.Feedback;
import xcfp.user.service.FeedbackService;
import xcfp.vo.feedback.FeedbackRequest;

import javax.validation.Valid;

@Api(tags = "小程序反馈")
@RestController
@RequestMapping("/app/feedback")
public class AppFeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @ApiOperation("提交反馈")
    @PostMapping({"", "/submit"})
    public Result<Feedback> submit(@Valid @RequestBody FeedbackRequest request) {
        return Result.ok(feedbackService.submit(UserContext.getUserId(), request));
    }

    @ApiOperation("我的反馈")
    @GetMapping("/my")
    public Result<Page<Feedback>> mine(@RequestParam(defaultValue = "1") long current,
                                       @RequestParam(defaultValue = "10") long limit) {
        return Result.ok(feedbackService.pageMine(UserContext.getUserId(), current, limit));
    }
}
