package com.kevinsa.security.bom.analyze.vo.mvnPlugin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JarMavenVO {
    private String packageName;
    private ArtifactVO parent;
    private String groupId;
    private String artifactId;
    private String version;
    private List<ArtifactVO> dependencies;

}
