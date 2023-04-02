package top.ticho.trace.server.entity;

import lombok.Data;

/**
 * @author zhajianjun
 * @date 2023-04-02 01:40:22
 */
@Data
public class Document {
    /**
     * es中的唯一id
     */
    private String id;
    /**
     * 文档标题
     */
    private String title;
    /**
     * 文档内容
     */
    private String content;
}
