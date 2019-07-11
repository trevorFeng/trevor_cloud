package com.trevor.message.feign;


import com.trevor.common.bo.JsonEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "service-play")
public interface PlayFeign {

    @RequestMapping(value = "/api/niuniu/two/{roomId}" ,method = RequestMethod.GET)
    JsonEntity<Object> niuniuEqualsTwo(@PathVariable("roomId") String roomId);

    @RequestMapping(value = "/api/niuniu/over/two/{roomId}" ,method = RequestMethod.GET)
    JsonEntity<Object> niuniuOverTwo(@PathVariable("roomId") String roomId);
}
