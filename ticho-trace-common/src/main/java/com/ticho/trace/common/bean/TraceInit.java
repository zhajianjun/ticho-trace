package com.ticho.trace.common.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 链路初始化
 *
 * @author zhajianjun
 * @date 2023-06-09 10:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TraceInit {

    /** 链路id */
    private String traceId;
    /** 跨度id */
    private String spanId;
    /** 当前应用名称 */
    private String appName;
    /** 当前ip */
    private String ip;
    /** 上个链路的应用名称 */
    private String preAppName;
    /** 上个链路的Ip */
    private String preIp;
    /** 链路 */
    private String trace;

}
