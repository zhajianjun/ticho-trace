package com.ticho.trace.server.infrastructure.entity;

import cn.easyes.annotation.IndexField;
import cn.easyes.annotation.IndexName;
import cn.easyes.annotation.rely.FieldType;
import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统信息
 *
 * @author zhajianjun
 * @date 2023-04-23 14:21
 */
@Data
@IndexName(value = "system", keepGlobalPrefix = true)
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
    @IndexField(fieldType = FieldType.DATE, dateFormat = DatePattern.NORM_DATETIME_PATTERN)
    @JSONField(format = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;
    /** 更新人 */
    private String updateBy;
    /** 更新时间 */
    @IndexField(fieldType = FieldType.DATE, dateFormat = DatePattern.NORM_DATETIME_PATTERN)
    @JSONField(format = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updateTime;

}
