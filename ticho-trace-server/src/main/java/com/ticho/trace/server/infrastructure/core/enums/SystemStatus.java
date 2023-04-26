package com.ticho.trace.server.infrastructure.core.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统状态
 *
 * @author zhajianjun
 * @date 2023-04-26 14:21
 */
public enum SystemStatus {

    /** 正常 */
    NORMAL(1, "正常"),

    /** 未激活 */
    NOT_ACTIVE(2, "未激活"),

    /** 锁定 */
    LOCKED(3, "已锁定"),

    /** 注销 */
    LOG_OUT(4, "已注销"),
    ;

    private final int code;
    private final String message;

    SystemStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    private static final Map<Integer, String> map;

    static {
        map = Arrays.stream(values()).collect(Collectors.toMap(SystemStatus::code, SystemStatus::message));
    }

    public static Map<Integer, String> get() {
        return map;
    }

    public static String getByCode(Integer code) {
        return map.get(code);
    }

}
