package xcfp.proinfo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xcfp.common.exception.XcfpException;
import xcfp.common.result.Result;
import xcfp.common.result.ResultCodeEnum;
import xcfp.model.Article;
import xcfp.proinfo.service.ArticleService;
import xcfp.vo.product.ArticleSaveRequest;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Api(tags = "管理端-资讯")
@Validated
@RestController
@RequestMapping("/admin/article")
public class AdminArticleController {

    @Autowired
    private ArticleService articleService;

    @ApiOperation("资讯分页")
    @GetMapping("page")
    public Result page(@RequestParam(defaultValue = "1") long current,
                       @RequestParam(defaultValue = "10") long limit,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Integer status) {
        Page<Article> page = new Page<>(Math.max(current, 1), Math.min(Math.max(limit, 1), 50));
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like("title", keyword.trim());
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time").orderByDesc("id");
        return Result.ok(articleService.page(page, wrapper));
    }

    @ApiOperation("资讯详情")
    @GetMapping("{id}")
    public Result detail(@PathVariable Long id) {
        return Result.ok(requireArticle(id));
    }

    @ApiOperation("新增资讯")
    @PostMapping("save")
    public Result save(@Valid @RequestBody ArticleSaveRequest request) {
        Article article = new Article();
        applyRequest(article, request);
        article.setIsDelete(0);
        articleService.save(article);
        return Result.ok(article);
    }

    @ApiOperation("更新资讯")
    @PostMapping("update/{id}")
    public Result update(@PathVariable Long id, @Valid @RequestBody ArticleSaveRequest request) {
        Article article = requireArticle(id);
        applyRequest(article, request);
        articleService.updateById(article);
        return Result.ok(article);
    }

    @ApiOperation("删除资讯")
    @DeleteMapping("{id}")
    public Result remove(@PathVariable Long id) {
        requireArticle(id);
        articleService.removeById(id);
        return Result.ok();
    }

    private Article requireArticle(Long id) {
        Article article = articleService.getById(id);
        if (article == null) {
            throw new XcfpException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        return article;
    }

    private void applyRequest(Article article, ArticleSaveRequest request) {
        article.setTitle(request.getTitle().trim());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCoverUrl(request.getCoverUrl());
        article.setAuthor(request.getAuthor());
        int status = request.getStatus() == null ? 0 : request.getStatus();
        article.setStatus(status);
        if (status == 1 && article.getPublishTime() == null) {
            article.setPublishTime(LocalDateTime.now());
        }
    }
}
