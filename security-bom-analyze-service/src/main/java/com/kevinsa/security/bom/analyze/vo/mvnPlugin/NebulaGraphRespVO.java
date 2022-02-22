package com.kevinsa.security.bom.analyze.vo.mvnPlugin;

import com.vesoft.nebula.client.graph.data.ResultSet;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NebulaGraphRespVO {
    private boolean isSucceeded;
    private long vid;
    private ResultSet resp;
}
