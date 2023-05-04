package com.ticho.trace.server.application.service;

import com.ticho.boot.view.core.PageResult;
import com.ticho.trace.server.interfaces.dto.LogDTO;
import com.ticho.trace.server.interfaces.query.LogQuery;
import com.ticho.trace.server.interfaces.vo.LogVO;

import java.util.List;

/**
 * 日志服务 接口
 *
 * @author zhajianjun
 * @date 2023-04-02 11:29:59
 */
public interface LogService {


    /**
     * 日志收集
     *
     * @param secret 秘钥
     * @param logs 日志
     */
    void collect(String secret, List<LogDTO> logs);

    /**
     * 分页查询
     *
     * @param logQuery 日志查询
     * @return {@link PageResult}<{@link LogVO}>
     */
    PageResult<LogVO> page(LogQuery logQuery);

}
