package com.trevor.general.exception;

import lombok.Data;

/**
 * @author
 * @date 05/17/19 09:23
 */
@Data
public class BizException extends RuntimeException {

    private Integer errorCode;

    private String errorMessage;

    public BizException(Integer errorCode , String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
