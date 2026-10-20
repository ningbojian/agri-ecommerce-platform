package xcfp.proinfo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xcfp.common.exception.XcfpException;
import xcfp.common.result.Result;
import xcfp.common.result.ResultCodeEnum;
import xcfp.model.Article;
import xcfp.proinfo.service.ArticleService;

@Api(tags = "小程序-资讯")
@RestController
@RequestMapping("/app/article")
public class AppArticleController {

    @Autowired
    private ArticleService articleService;

    @ApiOperation("已发布资讯分页")
    @GetMapping("page")
    public Result page(@RequestParam(defaultValue = "1") long current,
                       @RequestParam(defaultValue = "10") long limit) {
        Page<Article> page = new Page<>(Math.max(current, 1), Math.min(Math.max(limit, 1), 50));
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).orderByDesc("publish_time").orderByDesc("id");
        return Result.ok(articleService.page(page, wrapper));
    }

    @ApiOperation("资讯详情")
    @GetMapping("{id}")
    public Result detail(@PathVariable Long id) {
        Article article = articleService.getById(id);
        if (article == null || !Integer.valueOf(1).equals(article.getStatus())) {
            throw new XcfpException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        return Result.ok(article);
    }
}
