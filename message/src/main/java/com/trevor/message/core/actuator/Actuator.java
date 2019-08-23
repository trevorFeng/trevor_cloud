package com.trevor.message.core.actuator;

import com.trevor.message.core.event.Event;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

@Service
public class Actuator {

    @Resource(name = "executor")
    private Executor executor;

    public void addEvent(Event event){
        executor.execute(event);
    }

}
