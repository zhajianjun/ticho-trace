package com.ticho.trace.server.infrastructure.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-04-23 14:21
 */
@Data
public class SystemBO {

    /** 主键编号 */
    private String id;
    /** 系统id */
    private String systemId;
    /** 系统名称 */
    private String systemName;
    /** 秘钥 */
    private String secret;
    /** 系统状态;1-正常,2-未激活,3-已锁定,4-已注销 */
    private Integer status;
    /** 备注信息 */
    private String remark;
    /** 创建人 */
    private String createBy;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新人 */
    private String updateBy;
    /** 更新时间 */
    private LocalDateTime updateTime;

}
