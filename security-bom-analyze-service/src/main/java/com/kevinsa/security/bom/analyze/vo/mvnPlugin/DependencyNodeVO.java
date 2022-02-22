package com.kevinsa.security.bom.analyze.vo.mvnPlugin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DependencyNodeVO {
    private ArtifactVO artifactVO;
    private List<DependencyNodeVO> childrenVO;
}
