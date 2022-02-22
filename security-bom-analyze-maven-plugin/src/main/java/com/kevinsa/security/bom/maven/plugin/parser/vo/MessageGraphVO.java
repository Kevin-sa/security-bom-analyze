package com.kevinsa.security.bom.maven.plugin.parser.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 是否要做通用性的消息封装
 * 看gitAddress\branch是否可以通过命令行方式传递，如果不可以只能做路径到git、branch的缓存关系
 */
@Data
@Builder
public class MessageGraphVO {
    private String gitAddress;
    private String branch;
    private String commitId;
    private String type;
    private ArtifactVO artifactVO;
    private DependencyNodeVO dependencyNodeVO;
    private String createTime;
}
