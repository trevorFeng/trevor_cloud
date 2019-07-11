package com.trevor.common.bo;

import lombok.Data;

@Data
public class FeignResult {

    /**
     * 返回信息码
     */
    private Integer code;

    /**
     * 主题,正确返回数据
     */
    private Object data;

    /**
     * 错误返回的文字信息
     */
    private String message;
}
