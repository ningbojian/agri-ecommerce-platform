package xcfp.common.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import xcfp.common.result.Result;


/**
 * @author Kerr
 * @create 2021-09-04 22:27
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail();
    }
    @ExceptionHandler(XcfpException.class)
    @ResponseBody
    public Result error(XcfpException e){
        e.printStackTrace();
        return Result.fail();
    }
}
