package com.ticho.trace.client.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 城市信息
 *
 * @author zhajianjun
 * @date 2023-04-08 22:04:45
 */
@Data
@ApiModel("城市信息")
public class CityDTO {

    @ApiModelProperty(value = "编码", position = 10)
    private String code;

    @ApiModelProperty(value = "名称", position = 20)
    private String name;

}
