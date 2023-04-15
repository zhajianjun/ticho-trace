package com.ticho.trace.client.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 文件信息
 *
 * @author zhajianjun
 * @date 2023-04-08 22:02:07
 */
@Data
@ApiModel("文件信息")
public class FileDTO {

    @ApiModelProperty(value = "文件id", position = 10)
    private String fileId;

    @ApiModelProperty(value = "文件", position = 20)
    @NotNull(message = "文件不能为空")
    private MultipartFile file;

    @ApiModelProperty(value = "文件列表", position = 30)
    @NotNull(message = "文件列表不能为空")
    private List<MultipartFile> files;

    @ApiModelProperty(value = "元数据信息", position = 40)
    private List<String> metadatas;

}
