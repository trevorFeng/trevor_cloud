package com.trevor.general.init;

import com.trevor.commom.dao.mysql.RoomMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
