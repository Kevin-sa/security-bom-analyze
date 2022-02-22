package com.kevinsa.security.bom.maven.plugin.parser.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ArtifactVO {
    private String groupId;
    private String artifactId;
    private String type;
    private String scope;
    private String version;
}
