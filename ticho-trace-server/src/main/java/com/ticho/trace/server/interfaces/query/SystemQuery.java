package com.ticho.trace.server.interfaces.query;

import com.ticho.boot.view.core.BasePageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 系统信息查询条件
 *
 * @author zhajianjun
 * @date 2023-04-23 14:22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemQuery extends BasePageQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键编号 */
    @ApiModelProperty(value = "主键编号", position = 10)
    private String id;
    /** 系统id */
    @ApiModelProperty(value = "系统id", position = 20)
    private String systemId;
    /** 系统名称 */
    @ApiModelProperty(value = "系统名称", position = 30)
    private String systemName;
    /** 秘钥 */
    @ApiModelProperty(value = "秘钥", position = 40)
    private String secret;
    /** 系统状态;1-正常,2-未激活,3-已锁定,4-已注销 */
    @ApiModelProperty(value = "系统状态", notes = "1-正常,2-未激活,3-已锁定,4-已注销", position = 50)
    private Integer status;
    /** 备注信息 */
    @ApiModelProperty(value = "备注信息", position = 60)
    private String remark;

}
