package com.trevor.general.exception;

import com.trevor.common.bo.JsonEntity;
import com.trevor.common.bo.ResponseHelper;
import com.trevor.common.enums.MessageCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;

/**
 * @author trevor
 * @date 2019/3/4 12:47
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonEntity<Object> logicExceptionHandler(Exception e) {
        if (e instanceof ConstraintViolationException) {
            String message = ((ConstraintViolationException) e).getConstraintViolations().iterator().next().getMessage();
            JsonEntity<Object> objectJsonEntity = ResponseHelper.withErrorInstance(MessageCodeEnum.PARAM_ERROR);
            objectJsonEntity.setMessage(message);
            return objectJsonEntity;
        } else if (e instanceof MethodArgumentNotValidException){
            String defaultMessage = ((MethodArgumentNotValidException) e).getBindingResult().getFieldError().getDefaultMessage();
            JsonEntity<Object> objectJsonEntity = ResponseHelper.withErrorInstance(MessageCodeEnum.PARAM_ERROR);
            objectJsonEntity.setMessage(defaultMessage);
            return objectJsonEntity;
        } else if (e instanceof BizException){
            BizException bizException = (BizException) e;
            log.error("鸡巴，报错了:" + bizException.getErrorMessage(), bizException);
            return ResponseHelper.withExceptionInstance(bizException.getErrorCode() ,bizException.getErrorMessage());
        }else {
            log.error("鸡巴，报错了:" + e.getMessage(), e);
            return ResponseHelper.createInstance(e.getMessage() ,MessageCodeEnum.SYSTEM_ERROR);
        }
    }
}
