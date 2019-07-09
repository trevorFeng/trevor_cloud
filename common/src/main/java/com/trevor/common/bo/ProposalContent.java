package com.trevor.common.bo;

import lombok.Data;

import java.util.List;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-09 16:53
 **/
@Data
public class ProposalContent {

    private String content;

    private List<String> fileUrls;

}
