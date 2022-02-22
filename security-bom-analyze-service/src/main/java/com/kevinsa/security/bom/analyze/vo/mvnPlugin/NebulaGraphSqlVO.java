package com.kevinsa.security.bom.analyze.vo.mvnPlugin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NebulaGraphSqlVO {
    private long vid;
    private String graphSql;
}