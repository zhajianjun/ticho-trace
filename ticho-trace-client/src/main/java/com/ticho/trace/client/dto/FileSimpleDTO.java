package top.ticho.trace.client.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件信息
 *
 * @author zhajianjun
 * @date 2023-04-08 22:02:07
 */
@Data
@ApiModel("文件信息")
public class FileSimpleDTO {

    @ApiModelProperty(value = "文件id", position = 10)
    private String fileId;

    @ApiModelProperty(value = "文件名称", position = 20)
    private String fileName;

    @ApiModelProperty(value = "文件类型", position = 30)
    private String contentType;

    @ApiModelProperty(value = "文件大小", position = 40)
    private Long fileSize;


}
