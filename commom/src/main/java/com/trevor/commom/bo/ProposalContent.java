package com.trevor.commom.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-09 16:53
 **/
@Data
@ApiModel
public class ProposalContent {

    @ApiModelProperty("举报内容")
    private String content;

    @ApiModelProperty("文件url列表")
    private List<String> fileUrls;

}
