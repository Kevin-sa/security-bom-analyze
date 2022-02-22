package com.kevinsa.security.bom.analyze.vo.mvnPlugin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtifactVO {
    private String groupId;
    private String artifactId;
    private String type;
    private String scope;
    private String version;
}
