package top.ticho.trace.server.infrastructure.entity;

import cn.easyes.annotation.IndexField;
import cn.easyes.annotation.IndexId;
import cn.easyes.annotation.IndexName;
import cn.easyes.annotation.rely.FieldType;
import cn.easyes.annotation.rely.IdType;
import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息
 *
 * @author zhajianjun
 * @date 2023-04-20 23:10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@IndexName(value = "user", keepGlobalPrefix = true)
public class UserBO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键编号 */
    @IndexId(type = IdType.CUSTOMIZE)
    private String id;
    /** 账户;账户具有唯一性 */
    private String username;
    /** 密码 */
    private String password;
    /** 真实姓名 */
    private String realname;
    /** 邮箱 */
    private String email;
    /** 手机号码 */
    private String mobile;
    /** 头像地址 */
    private String photo;
    /** 最后登录ip地址 */
    private String lastIp;
    /** 最后登录时间 */
    private LocalDateTime lastTime;
    /** 用户状态;1-正常,2-未激活,3-已锁定,4-已注销 */
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
    /** 系统id列表 */
    private List<String> systemIds;

}