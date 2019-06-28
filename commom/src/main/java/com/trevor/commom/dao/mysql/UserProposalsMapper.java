package com.trevor.commom.dao.mysql;

import com.trevor.commom.domain.mysql.UserProposals;
import org.apache.ibatis.annotations.Param;

public interface UserProposalsMapper {

    /**
     * 新增一条记录
     * @param userProposals
     */
    void insertOne(@Param("userProposals") UserProposals userProposals);
}
