package com.trevor.message.socket;

import com.google.common.collect.Maps;
import com.trevor.common.bo.PaiXing;
import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.bo.WebKeys;
import com.trevor.common.domain.mysql.Room;
import com.trevor.common.domain.mysql.User;
import com.trevor.common.enums.FriendManageEnum;
import com.trevor.common.enums.GameStatusEnum;
import com.trevor.common.enums.RoomTypeEnum;
import com.trevor.common.enums.SpecialEnum;
import com.trevor.common.util.JsonUtil;
import com.trevor.common.util.ObjectUtil;
import com.trevor.message.bo.SocketMessage;
import com.trevor.message.decoder.MessageDecoder;
import com.trevor.message.encoder.MessageEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;


/**
 * 一句话描述该类作用:【牛牛服务端,每次建立链接就新建了一个对象】
 *
 * @author: trevor
 * @create: 2019-03-05 22:29
 **/
@ServerEndpoint(
        value = "/niuniu/{roomId}",
        //encoders = {MessageEncoder.class},
        decoders = {MessageDecoder.class}
)
@Component
@Slf4j
public class NiuniuSocket extends BaseServer {

    public Session session;

    public String userId;

    public String roomId;

    /**
     * 连接建立成功调用的方法
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String roomId) {
        //roomId合法性检查
        Long roomIdLong = Long.valueOf(roomId);
        Room room = roomService.findOneById(roomIdLong);
        if (room == null) {
            directSendMessage(new SocketResult(507) ,session);
            close(session);
            return;
        }
        //是否激活,0为未激活,1为激活，2为房间使用完成后关闭，3为房间未使用关闭
        if (!Objects.equals(room.getStatus() ,0) && !Objects.equals(room.getStatus() ,1)) {
            directSendMessage(new SocketResult(506) ,session);
            close(session);
            return;
        }
        //token合法性检查
        List<String> params = session.getRequestParameterMap().get(WebKeys.TOKEN);
        if (ObjectUtil.isEmpty(params)) {
            directSendMessage(new SocketResult(400) ,session);
            close(session);
            return;
        }
        String token = session.getRequestParameterMap().get(WebKeys.TOKEN).get(0);
        User user = userService.getUserByToken(token);
        if (ObjectUtil.isEmpty(user)) {
            directSendMessage(new SocketResult(404) ,session);
            close(session);
            return;
        }

        SocketResult soc = checkRoom(room ,user);
        if (soc.getHead() != null) {
            directSendMessage(soc ,session);
            close(session);
            return;
        }

        this.roomId = roomId;
        this.userId = String.valueOf(user.getId());
        roomSocketService.join(roomId ,this);
        session.setMaxIdleTimeout(1000 * 60 * 45);
        this.session = session;
        redisService.delete(RedisConstant.MESSAGES_QUEUE + userId);

        soc.setHead(1000);
        Integer runingNum = Integer.valueOf(redisService.getHashValue(RedisConstant.BASE_ROOM_INFO + roomId ,RedisConstant.RUNING_NUM));
        Integer totalNum = Integer.valueOf(redisService.getHashValue(RedisConstant.BASE_ROOM_INFO + roomId ,RedisConstant.TOTAL_NUM));
        soc.setRuningAndTotal((runingNum + 1) + "/" + totalNum);
        redisService.setDeleteMember(RedisConstant.DIS_CONNECTION + roomId ,userId);
        soc.setDisConnectionPlayerIds(redisService.getSetMembers(RedisConstant.DIS_CONNECTION + roomId));

        if (!soc.getIsChiGuaPeople()) {
            redisService.setAdd(RedisConstant.REAL_ROOM_PLAYER + roomId ,userId);
        }
        soc.setPlayers(roomSocketService.getRealRoomPlayerCount(this.roomId));
        //广播新人加入，前端需要比较useId是否与断线的玩家id（断线重连，断线时会给玩家一个消息谁断线了）、网络不好的玩家是否相等（网络不好重连），不相等则未新加入的玩家
        roomSocketService.broadcast(roomId ,soc);
        //发送房间状态消息
        welcome(roomId);
    }

    /**
     * 接受用户消息
     */
    @OnMessage
    public void onMessage(SocketMessage socketMessage){
        if (Objects.equals(socketMessage.getMessageCode() ,1)) {
            playService.dealReadyMessage(roomId ,this);
        }else if (Objects.equals(socketMessage.getMessageCode() ,2)) {
            playService.dealQiangZhuangMessage(roomId ,this ,socketMessage);
        }else if (Objects.equals(socketMessage.getMessageCode() ,3)) {
            playService.dealXiaZhuMessage(roomId ,this ,socketMessage);
        }else if (Objects.equals(socketMessage.getMessageCode() ,4)) {
            playService.dealTanPaiMessage(roomId ,this);
        }else if (Objects.equals(socketMessage.getMessageCode() ,5)) {
            playService.dealShuoHuaMessage(roomId ,this ,socketMessage);
        }
    }

    /**
     * 关闭连接调用的方法
     */
    @OnClose
    public void onClose(){
        if (!ObjectUtil.isEmpty(userId)) {
            roomSocketService.leave(roomId ,this);
            //如果是真正的玩家则广播消息
            if (redisService.getSetMembers(RedisConstant.REAL_ROOM_PLAYER + roomId).contains(userId)) {
                redisService.setAdd(RedisConstant.DIS_CONNECTION + roomId ,userId);
                SocketResult res = new SocketResult(1001 ,userId);
                roomSocketService.broadcast(roomId ,res);
            }

        }
    }

    /**
     * 发生错误时调用的方法
     */
    @OnError
    public void onError(Throwable t){
        log.error(t.getMessage() ,t);
    }

    /**
     * 向客户端发消息
     * @param pack
     */
    public void sendMessage(SocketResult pack) {
        redisService.listRightPush(RedisConstant.MESSAGES_QUEUE + userId ,JsonUtil.toJsonString(pack));
    }

    /**
     * 向客户端发消息
     * @param pack
     */
    public void directSendMessage(SocketResult pack ,Session s) {
        RemoteEndpoint.Async async = s.getAsyncRemote();
        if (s.isOpen()) {
            async.sendText(JsonUtil.toJsonString(pack));
        } else {
            close(s);
        }
    }

    /**
     * 向客户端刷消息
     */
    public void flush(){
        try {
            List<String> messages = redisService.getListMembersAndDelete(RedisConstant.MESSAGES_QUEUE + userId);
            if (!messages.isEmpty()) {
                StringBuffer stringBuffer = new StringBuffer("[");
                for (String mess : messages) {
                    stringBuffer.append(mess).append(",");
                }
                stringBuffer.setLength(stringBuffer.length() - 1);
                stringBuffer.append("]");
                synchronized (session) {
                    RemoteEndpoint.Async async = session.getAsyncRemote();
                    if (session.isOpen()) {
                        async.sendText(stringBuffer.toString());
                    } else {
                        close(session);
                    }
                }
            }
        }catch (Exception e) {
            log.error(e.getMessage() ,e);
        }
    }

    /**
     * 关闭连接
     *
     * @param session
     */
    public void close(Session session) {
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                log.error("close", e.getMessage(), e);
            }
        }
    }


    public void stop(){
        redisService.delete(RedisConstant.MESSAGES_QUEUE + userId);
    }


    private SocketResult checkRoom(Room room ,User user){
        //房主是否开启好友管理功能
        Boolean isFriendManage = Objects.equals(userService.isFriendManage(room.getRoomAuth()) , FriendManageEnum.YES.getCode());
        List<Integer> special = JsonUtil.parseJavaList(redisService.getHashValue(RedisConstant.BASE_ROOM_INFO ,RedisConstant.SPECIAL), Integer.class);
        //开通
        if (isFriendManage) {
            //配置仅限好友
            if (special.contains(SpecialEnum.JUST_FRIENDS.getCode())) {
                Long count = friendManageMapper.countRoomAuthFriendAllow(room.getRoomAuth(), user.getId());
                //不是房主的好友
                if (Objects.equals(count ,0L)) {
                    return new SocketResult(508);
                    //是房主的好友
                }else {
                    return this.dealCanSee(user ,special ,room);
                }
            }
            //未配置仅限好友
            else {
                return this.dealCanSee(user ,special ,room);
            }
            // 未开通
        }else {
            return this.dealCanSee( user ,special ,room);
        }

    }

    /**
     * 处理是否可以观战
     * @throws IOException
     */
    private SocketResult dealCanSee(User user, List<Integer> special ,Room room){
        SocketResult socketResult = new SocketResult();
        socketResult.setUserId(String.valueOf(user.getId()));
        socketResult.setName(user.getAppName());
        socketResult.setPictureUrl(user.getAppPictureUrl());
        Boolean bo = redisService.getSetSize(RedisConstant.REAL_ROOM_PLAYER + room.getId()) <
                RoomTypeEnum.getRoomNumByType(
                        Integer.valueOf(
                                redisService.getHashValue(RedisConstant.BASE_ROOM_INFO + room.getId() ,RedisConstant.ROOM_TYPE)));
        //允许观战
        if (special!= null && special.contains(SpecialEnum.CAN_SEE.getCode())) {
            if (bo) {
                socketResult.setIsChiGuaPeople(Boolean.FALSE);
            }else {
                socketResult.setIsChiGuaPeople(Boolean.TRUE);
            }
            return socketResult;
        //不允许观战
        }else {
            if (bo) {
                socketResult.setIsChiGuaPeople(Boolean.FALSE);
                return socketResult;
            }else {
                return new SocketResult(509);
            }

        }
    }

    /**
     * 欢迎玩家加入，发送房间状态信息
     */
    private void welcome(String roomId){
        SocketResult socketResult = new SocketResult();
        socketResult.setHead(2002);
        String gameStatus = redisService.getHashValue(RedisConstant.BASE_ROOM_INFO + roomId ,RedisConstant.GAME_STATUS);
        //设置准备的玩家
        if (Objects.equals(gameStatus ,GameStatusEnum.BEFORE_READY.getCode()) || Objects.equals(gameStatus ,GameStatusEnum.BEFORE_FAPAI_4.getCode())) {
            socketResult.setGameStatus(1);
            socketResult.setReadyPlayerIds(getReadyPlayers());
        }
        //设置玩家先发的4张牌
        else if (Objects.equals(gameStatus ,GameStatusEnum.BEFORE_QIANGZHUANG_COUNTDOWN.getCode())
                || Objects.equals(gameStatus ,GameStatusEnum.BEFORE_SELECT_ZHUANGJIA.getCode())) {
//            socketResult.setGameStatus(2);
//            Set<String> readyPlayers = getReadyPlayers();
//            socketResult.setReadyPlayerIds(readyPlayers);
//            if (readyPlayers.contains(userId)) {
//                setPoke_4(socketResult ,userId);
//            }
            socketResult.setGameStatus(2);
            Set<String> readyPlayers = getReadyPlayers();
            socketResult.setReadyPlayerIds(readyPlayers);
            if (readyPlayers.contains(userId)) {
                setPoke_4(socketResult ,userId);
            }
            socketResult.setQiangZhuangMap(getQiangZhuangPlayers());
        }
//        //设置抢庄的玩家
//        else if (Objects.equals(gameStatus ,GameStatusEnum.BEFORE_SELECT_ZHUANGJIA.getCode())) {
//            socketResult.setGameStatus(3);
//            Set<String> readyPlayers = getReadyPlayers();
//            socketResult.setReadyPlayerIds(readyPlayers);
//            if (readyPlayers.contains(userId)) {
//                setPoke_4(socketResult ,userId);
//            }
//            socketResult.setQiangZhuangMap(getQiangZhuangPlayers());
//        }
        //设置庄家
        else if (Objects.equals(gameStatus ,GameStatusEnum.BEFORE_XIANJIA_XIAZHU.getCode())
                || Objects.equals(gameStatus ,GameStatusEnum.BEFORE_LAST_POKE.getCode())) {
//            socketResult.setGameStatus(3);
//            Set<String> readyPlayers = getReadyPlayers();
//            socketResult.setReadyPlayerIds(readyPlayers);
//            if (readyPlayers.contains(userId)) {
//                setPoke_4(socketResult ,userId);
//            }
//            socketResult.setZhuangJiaUserId(getZhuangJia());
            socketResult.setGameStatus(3);
            Set<String> readyPlayers = getReadyPlayers();
            socketResult.setReadyPlayerIds(readyPlayers);
            if (readyPlayers.contains(userId)) {
                setPoke_4(socketResult ,userId);
            }
            socketResult.setZhuangJiaUserId(getZhuangJia());
            socketResult.setXianJiaXiaZhuMap(getXianJiaXiaZhu());
        }
//        //设置闲家下注
//        else if (Objects.equals(gameStatus ,GameStatusEnum.BEFORE_LAST_POKE.getCode())) {
//            socketResult.setGameStatus(5);
//            Set<String> readyPlayers = getReadyPlayers();
//            socketResult.setReadyPlayerIds(readyPlayers);
//            if (readyPlayers.contains(userId)) {
//                setPoke_4(socketResult ,userId);
//            }
//            socketResult.setZhuangJiaUserId(getZhuangJia());
//            socketResult.setXianJiaXiaZhuMap(getXianJiaXiaZhu());
//        }
        //设置玩家发的最后一张牌
        else if (Objects.equals(gameStatus ,GameStatusEnum.BEFORE_TABPAI_COUNTDOWN.getCode())
                || Objects.equals(gameStatus ,GameStatusEnum.BEFORE_CALRESULT.getCode())) {
            socketResult.setGameStatus(4);
            //Set<String> readyPlayers = getReadyPlayers();
            //socketResult.setReadyPlayerIds(readyPlayers);
            socketResult.setZhuangJiaUserId(getZhuangJia());
            setPoke_5(socketResult);
            socketResult.setTanPaiPlayerUserIds(getTanPaiPlayer());
            setScoreAndPaiXing(socketResult);
        }
//        //设置谁摊牌了
//        else if (Objects.equals(gameStatus ,GameStatusEnum.BEFORE_CALRESULT.getCode())) {
//            socketResult.setGameStatus(7);
//            socketResult.setZhuangJiaUserId(getZhuangJia());
//            socketResult.setTanPaiPlayerUserIds(getTanPaiPlayer());
//            setPoke_5(socketResult);
//            setScoreAndPaiXing(socketResult);
//        }
        //下一句准备
        else if (Objects.equals(gameStatus ,GameStatusEnum.BEFORE_DELETE_KEYS.getCode())) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            socketResult.setGameStatus(1);
            socketResult.setReadyPlayerIds(getReadyPlayers());
        }
        sendMessage(socketResult);
        return;
    }

    /**
     * 得到准备的玩家
     * @return
     */
    private Set<String> getReadyPlayers(){
        Set<String> set = redisService.getSetMembers(RedisConstant.READY_PLAYER + roomId);
        if (!set.isEmpty()) {
            return set;
        }
        return null;
    }

    /**
     * 得到抢庄的玩家
     * @return
     */
    private Map<String ,String> getQiangZhuangPlayers(){
        Map<String ,String> qiangZhuangMap = redisService.getMap(RedisConstant.QIANGZHAUNG + roomId);
        if (!qiangZhuangMap.isEmpty()) {
            return qiangZhuangMap;
        }
        return null;
    }

    /**
     * 得到庄家
     * @return
     */
    private String getZhuangJia(){
        String zhuangJia = redisService.getValue(RedisConstant.ZHUANGJIA + roomId);
        return zhuangJia;
    }

    /**
     * 得到闲家下注
     * @return
     */
    private Map<String ,String> getXianJiaXiaZhu(){
        Map<String ,String> xianJiaXiaZhu = redisService.getMap(RedisConstant.XIANJIA_XIAZHU + roomId);
        if (!xianJiaXiaZhu.isEmpty()) {
            return xianJiaXiaZhu;
        }
        return null;
    }


    /**
     * 设置得分和牌型
     * @return
     */
    private void setScoreAndPaiXing(SocketResult socketResult){
        //设置本局得分
        Map<String ,String> scoreMap = redisService.getMap(RedisConstant.SCORE + roomId);
        Map<String ,Integer> stringIntegerMap = Maps.newHashMap();
        for (Map.Entry<String ,String> entry : scoreMap.entrySet()) {
            stringIntegerMap.put(entry.getKey() ,Integer.valueOf(entry.getValue()));
        }
        socketResult.setScoreMap(stringIntegerMap);

        Map<String ,String> paiXingMap = redisService.getMap(RedisConstant.PAI_XING + roomId);
        Map<String ,Integer> paiXingIntegerMap = Maps.newHashMap();
        for (Map.Entry<String ,String> entry : paiXingMap.entrySet()) {
            paiXingIntegerMap.put(entry.getKey() ,JsonUtil.parseJavaObject(entry.getValue() ,PaiXing.class).getPaixing());

        }
        socketResult.setPaiXing(paiXingIntegerMap);
    }

    /**
     * 设置4张牌
     * @param socketResult
     */
    public void setPoke_4(SocketResult socketResult ,String userId){
        Map<String, String> map = redisService.getMap(RedisConstant.POKES + roomId);
        Map<String ,List<String>> userPokeMap_5 = new HashMap<>(2<<4);
        List<String> pokeList_4 = JsonUtil.parseJavaList(map.get(userId) ,String.class).subList(0 ,4);
        socketResult.setUserPokeList_4(pokeList_4);
    }



    /**
     * 设置5张牌
     * @param socketResult
     */
    public void setPoke_5(SocketResult socketResult){
        Map<String, String> map = redisService.getMap(RedisConstant.POKES + roomId);
        Map<String ,List<String>> userPokeMap_5 = new HashMap<>(2<<4);
        for (Map.Entry<String ,String> entry : map.entrySet()) {
            userPokeMap_5.put(entry.getKey() ,JsonUtil.parseJavaList(entry.getValue() ,String.class));
        }
        socketResult.setUserPokeMap_5(userPokeMap_5);
    }

    /**
     * 得到摊牌的玩家
     * @return
     */
    private Set<String> getTanPaiPlayer(){
        return redisService.getSetMembers(RedisConstant.TANPAI + roomId);
    }


}
