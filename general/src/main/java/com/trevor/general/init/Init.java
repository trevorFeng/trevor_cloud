package com.trevor.general.init;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.trevor.bo.RoomPoke;
import com.trevor.bo.UserPokesIndex;
import com.trevor.bo.UserScore;
import com.trevor.commom.dao.mysql.RoomMapper;
import com.trevor.dao.RoomPokeInitMapper;
import com.trevor.domain.RoomPokeInit;
import com.trevor.enums.GameStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author trevor
 * @date 05/14/19 17:58
 */
@Component
@Slf4j
public class Init implements ApplicationRunner {


    @Resource
    private RoomMapper roomMapper;


    /**
     * 初始化roomPoke到roomPokeMap中,初始化sessionsMap
     * @param args
     */
    @Override
    public void run(ApplicationArguments args) {

    }


}
