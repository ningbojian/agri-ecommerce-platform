package xcfp.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import xcfp.common.result.Result;
import xcfp.common.result.ResultCodeEnum;

import javax.validation.ConstraintViolationException;

/**
 * 统一异常处理：业务异常带出自身 code/message，参数校验返回 202。
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(XcfpException.class)
    @ResponseBody
    public Result handleBusinessException(XcfpException e) {
        log.info("Business exception: code={}, message={}", e.getCode(), e.getMessage());
        return Result.build(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        return Result.build(ResultCodeEnum.PARAM_ERROR.getCode(), firstError(e.getBindingResult().getFieldError()));
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public Result handleBindException(BindException e) {
        return Result.build(ResultCodeEnum.PARAM_ERROR.getCode(), firstError(e.getBindingResult().getFieldError()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public Result handleConstraintViolation(ConstraintViolationException e) {
        return Result.build(ResultCodeEnum.PARAM_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Result handleUnreadableBody(HttpMessageNotReadableException e) {
        return Result.build(ResultCodeEnum.PARAM_ERROR.getCode(), "请求参数格式不正确");
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handleException(Exception e) {
        log.error("Unhandled exception", e);
        return Result.build(ResultCodeEnum.SERVICE_ERROR.getCode(), ResultCodeEnum.SERVICE_ERROR.getMessage());
    }

    private String firstError(FieldError fieldError) {
        if (fieldError == null) {
            return ResultCodeEnum.PARAM_ERROR.getMessage();
        }
        return fieldError.getDefaultMessage();
    }
}
