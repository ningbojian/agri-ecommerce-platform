package xcfp.sup.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xcfp.common.result.Result;
import xcfp.common.utils.MD5;
import xcfp.model.Supinfo;
import xcfp.model.User;
import xcfp.sup.service.SupService;

import java.util.Random;

/**
 * @author Kerr
 * @create 2021-10-17 0:36
 */
@RestController
@Api(value = "商家信息")
@RequestMapping("admin/sup")
public class SupController {
    @Autowired
    private SupService supService;
    @PostMapping("savesup")
    public Result saveSup(@RequestBody Supinfo supinfo){
        Supinfo byTel = findByTel(supinfo.getSuptel());
        if(byTel!=null){
            return Result.fail();
        }
        Random random=new Random();
        supinfo.setSignkey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
        boolean b = supService.save(supinfo);
        if(b){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    @GetMapping("findByTel/{suptel}")
    public Result findBySuptel(@PathVariable String suptel){
        Supinfo supinfo = findByTel(suptel);
        if(supinfo==null){
            return Result.fail();
        }
        return Result.ok(supinfo);
    }
    @PostMapping("updateSup")
    public Result updateSup(@RequestBody Supinfo supinfo){
        boolean b = supService.updateById(supinfo);
        if(b){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    @DeleteMapping("removeSup/{id}")
    public Result removeSup(@PathVariable Integer id){
        boolean b = supService.removeById(id);
        if(b){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    private Supinfo findByTel(String suptel){
        QueryWrapper<Supinfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("suptel",suptel);
        Supinfo supinfo = supService.getOne(queryWrapper);
        return supinfo;
    }
}
