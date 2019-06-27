package com.trevor.commom.bo;

import lombok.Data;

@Data
public class UserScore {

    private Long userId;

    private Integer score = 0;
}
