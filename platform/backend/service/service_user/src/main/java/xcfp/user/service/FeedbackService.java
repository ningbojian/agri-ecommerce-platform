package xcfp.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xcfp.common.exception.XcfpException;
import xcfp.common.result.ResultCodeEnum;
import xcfp.model.Feedback;
import xcfp.user.mapper.FeedbackMapper;
import xcfp.vo.feedback.FeedbackRequest;
import xcfp.vo.feedback.ReplyRequest;

import java.time.LocalDateTime;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    public Feedback submit(Long userId, FeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setUserid(userId);
        feedback.setContent(request.getContent().trim());
        feedback.setContact(request.getContact());
        feedback.setStatus(0);
        feedback.setIsDelete(0);
        feedbackMapper.insert(feedback);
        return feedback;
    }

    public Page<Feedback> pageMine(Long userId, long current, long limit) {
        Page<Feedback> page = new Page<>(Math.max(current, 1), Math.min(Math.max(limit, 1), 50));
        QueryWrapper<Feedback> wrapper = new QueryWrapper<>();
        wrapper.eq("userid", userId).orderByDesc("create_time").orderByDesc("id");
        return feedbackMapper.selectPage(page, wrapper);
    }

    public Page<Feedback> pageAll(long current, long limit, Integer status) {
        Page<Feedback> page = new Page<>(Math.max(current, 1), Math.min(Math.max(limit, 1), 50));
        QueryWrapper<Feedback> wrapper = new QueryWrapper<>();
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time").orderByDesc("id");
        return feedbackMapper.selectPage(page, wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public Feedback reply(Long id, ReplyRequest request) {
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            throw new XcfpException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        if (!StringUtils.hasText(request.getReply())) {
            throw new XcfpException(ResultCodeEnum.PARAM_ERROR);
        }
        feedback.setReply(request.getReply().trim());
        feedback.setStatus(1);
        feedback.setReplyTime(LocalDateTime.now());
        feedbackMapper.updateById(feedback);
        return feedback;
    }
}
