package com.trevor.common.enums;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-08 0:24
 **/

public enum MessageCodeEnum {

    /*****************************************               http返回消息                    *********************/

    /**
     * 您的房卡数量不足
     */
    USER_ROOMCARD_NOT_ENOUGH(-1 ,"您的房卡数量不足"),

    /**
     * 交易已关闭
     */
    TRANS_CLOSE(-2 ,"交易已关闭"),


    /**
     * 授权失败
     */
    AUTH_FAILED(-3 ,"授权失败"),

    /**
     * 手机号未注册
     */
    PHONE_NOT_EXIST(-4 ,"手机号未注册"),

    /**
     * 发送验证码失败
     */
    CODE_FILED(-5 ,"发送验证码失败"),

    /**
     * 验证码错误
     */
    CODE_ERROR(-5 ,"验证码错误"),

    /**
     * 验证码错误
     */
    CODE_EXPIRE(-5 ,"验证码已过期或不存在"),

    /**
     * 草，报异常了
     */
    SYSTEM_ERROR(-6 ,"鸡巴，报错了"),

    /**
     * 操作失败
     */
    HANDLER_FAILED(-7, "操作失败"),

    /**
     * 文件格式错误
     */
    FILE_NAME_ERROR(-8 ,"文件格式错误"),

    /**
     * 操你妈，想黑爸爸呢
     */
    ERROR_NUM_MAX(-9 ,"临时凭证不存在或已过期"),


    /**
     * 草，参数错误
     */
    PARAM_ERROR(-50 ,"草，参数错误"),

    /**
     * session过期
     */
    SESSION_TIRED(0 ,"session 过期"),


    /**
     * 创建成功
     */
    CREATE_SUCCESS(1 ,"创建成功"),

    /**
     * 领取成功
     */
    RECEIVE_SUCCESS(2 ,"领取成功"),

    /**
     * 查询成功
     */
    QUERY_SUCCESS(3 ,"查询成功"),

    /**
     * 授权成功
     */
    AUTH_SUCCESS(4 ,"授权成功"),

    /**
     * 退出登录成功
     */
    LOGIN_OUT_SUCCESS(5 ,"退出登录成功"),

    /**
     * 发送验证码成功
     */
    SEND_MESSAGE(6 ,"发送验证码成功"),

    /**
     * 绑定手机号成功
     */
    BINDING_SUCCESS(7 ,"绑定手机号成功"),

    /**
     * 操作成功
     */
    HANDLER_SUCCESS(8, "操作成功"),


    /*****************************************               websocket返回消息                    *********************/

    SYSTEM_ERROT(-1 ,"系统错误"),

    /**
     * 房间不存在
     */
    ROOM_NOT_EXIST(-101 ,"房间不存在"),

    /**
     * 房间已关闭
     */
    ROOM_CLOSE(-102 ,"房间已关闭"),

    /**
     * 房间人数已满
     */
    ROOM_FULL(-103 ,"房间人数已满"),

    /**
     * 您不是房主的好友
     */
    NOT_FRIEND(-104 ,"您不是房主的好友"),

    /**
     * 进入房间成功
     */
    ENTER_ROOM(100 ,"进入房间成功"),

    /**
     * 准备成功
     */
    READY(101 ,"准备成功"),

    /**
     * 抢庄
     */
    QIANG_ZHUANG(102 ,"抢庄"),

    /**
     * 闲家下注
     */
    XIAN_JIA_XIA_ZHU(103, "闲家下注"),

    /**
     * token 错误
     */
    TOKEN_ERROR(-401 ,"token 错误");


    private Integer code;

    private String message;

    MessageCodeEnum(Integer code , String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
