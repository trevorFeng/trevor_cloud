package com.trevor.commom.domain.mysql;


import lombok.Data;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-05 23:33
 **/
@Data
public class Config {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 配置名字
     */
    private String configName;

    /**
     * 配置的值
     */
    private String configValue;

    private String configName1;

    private String configValue1;

    private String configName2;

    private String configValue2;

    private String configName3;

    private String configValue3;

    private String configName4;

    private String configValue4;

    /**
     * 1代表可用，0代表不可用
     */
    private Integer active;

}
