package com.kevinsa.security.bom.maven.plugin.parser.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class DependencyNodeVO {
    private ArtifactVO artifactVO;
    private List<DependencyNodeVO> childrenVO;
}
