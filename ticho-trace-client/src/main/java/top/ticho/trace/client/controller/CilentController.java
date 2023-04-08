package top.ticho.trace.client.controller;

import com.ticho.boot.view.core.Result;
import com.ticho.boot.web.util.valid.ValidUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.ticho.trace.client.dto.CityDTO;
import top.ticho.trace.client.dto.FileDTO;
import top.ticho.trace.client.dto.FileSimpleDTO;

/**
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@RestController
@RequestMapping("client")
@Slf4j
@Api(tags = "测试")
public class CilentController {


    @PostMapping("upload")
    @ApiOperation(value = "文件上传", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Result<FileSimpleDTO> upload(FileDTO fileDTO) {
        ValidUtil.valid(fileDTO);
        MultipartFile file = fileDTO.getFile();
        FileSimpleDTO fileSimpleDTO = new FileSimpleDTO();
        fileSimpleDTO.setFileId(fileDTO.getFileId());
        fileSimpleDTO.setFileName(file.getOriginalFilename());
        fileSimpleDTO.setContentType(file.getContentType());
        fileSimpleDTO.setFileSize(file.getSize() / 1024);
        return Result.ok(fileSimpleDTO);
    }

    @PostMapping("city")
    @ApiOperation(value = "城市信息保存")
    public Result<CityDTO> city(@RequestBody CityDTO cityDTO) {
        return Result.ok(cityDTO);
    }

    @GetMapping("get/{id}")
    @ApiOperation(value = "根据城市id获取")
    public Result<String> city(@PathVariable String id) {
        return Result.ok(id);
    }

}
