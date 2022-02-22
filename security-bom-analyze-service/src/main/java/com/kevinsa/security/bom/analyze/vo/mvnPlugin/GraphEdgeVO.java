package com.kevinsa.security.bom.analyze.vo.mvnPlugin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GraphEdgeVO {
    private long sourceVid;
    private long sinkVid;
}
