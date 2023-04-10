package top.ticho.trace.server.service;

import top.ticho.trace.common.bean.LogInfo;

import java.util.List;
import java.util.Map;

/**
 * 日志服务 接口
 *
 * @author zhajianjun
 * @date 2023-04-02 11:29:59
 */
public interface LogService {


    /**
     * 收集
     *
     * @param logs 日志
     * @return int
     */
    int collect(List<LogInfo> logs);

}
