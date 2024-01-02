package top.ticho.trace.server.interfaces.dto;

import top.ticho.boot.view.core.BaseSecurityUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhajianjun
 * @date 2022-09-26 10:32
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SecurityUser extends BaseSecurityUser {

    @ApiModelProperty(value = "账户", position = 10)
    private String username;

    @ApiModelProperty(value = "用户状态;1-正常,2-未激活,3-已锁定,4-已注销", position = 40)
    private Integer status = 2;

    @Override
    public String toString() {
        return super.toString();
    }

}
