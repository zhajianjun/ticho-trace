package com.ticho.trace.server.interfaces.dto;

import com.ticho.boot.web.util.valid.ValidGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 系统信息DTO
 *
 * @author zhajianjun
 * @date 2023-04-23 14:22
 */
@Data
@ApiModel(value = "系统信息DTO")
public class SystemDTO {

    /** id */
    @NotBlank(message = "id不能为空", groups = ValidGroup.Upd.class)
    @ApiModelProperty(value = "id", position = 10)
    private String id;
    /** 系统id */
    @NotBlank(message = "系统id不能为空", groups = ValidGroup.Add.class)
    @ApiModelProperty(value = "系统id", position = 20)
    private String systemId;
    /** 系统名称 */
    @NotBlank(message = "系统名称不能为空", groups = ValidGroup.Add.class)
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
