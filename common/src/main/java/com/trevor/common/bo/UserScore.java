package com.trevor.common.bo;

import lombok.Data;

@Data
public class UserScore {

    private Long userId;

    private Integer score = 0;
}
