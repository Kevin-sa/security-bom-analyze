package com.kevinsa.security.bom.analyze.dao.graph.model;

import static com.kevinsa.security.bom.analyze.constant.nebula.GraphSpace.SECURITY_JAVA_MAVEN_SPACE;
import com.kevinsa.security.bom.analyze.utils.SnowFlakeUtils;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.ArtifactVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.GraphEdgeVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.NebulaGraphSqlVO;
import org.springframework.stereotype.Service;

@Service
public class MvnGraphTemp {

    private String getSpace() {
        return "USE " + SECURITY_JAVA_MAVEN_SPACE.getSpaceName() + ";";
    }

    public NebulaGraphSqlVO VInsertGitSql(String gitAddress) {
        long vid = SnowFlakeUtils.INSTANCE.getSnowFlakeId();
        String tagGitSql = String.format("%s INSERT VERTEX git(remote_url) VALUES %d :('%s')",
                getSpace(), vid, gitAddress);
        return NebulaGraphSqlVO.builder()
                .vid(vid)
                .graphSql(tagGitSql)
                .build();
    }

    public NebulaGraphSqlVO VInsertTModelSql(ArtifactVO artifactVO) {
        long vid = SnowFlakeUtils.INSTANCE.getSnowFlakeId();
        String tagModelSql = String.format("%s INSERT VERTEX model(group_id, artifact_id, type, version) VALUES %d :('%s', '%s', '%s', '%s')",
                getSpace(), vid, artifactVO.getGroupId(), artifactVO.getArtifactId(), artifactVO.getType(), artifactVO.getVersion());
        return NebulaGraphSqlVO.builder()
                .vid(vid)
                .graphSql(tagModelSql)
                .build();
    }

    public NebulaGraphSqlVO VInsertTParentModelSql(ArtifactVO artifactVO) {
        long vid = SnowFlakeUtils.INSTANCE.getSnowFlakeId();
        String tagModelSql = String.format("%s INSERT VERTEX parent_model(group_id, artifact_id, version) VALUES %d :('%s', '%s', '%s')",
                getSpace(), vid, artifactVO.getGroupId(), artifactVO.getArtifactId(), artifactVO.getVersion());
        return NebulaGraphSqlVO.builder()
                .vid(vid)
                .graphSql(tagModelSql)
                .build();
    }

    public NebulaGraphSqlVO VInsertTJarSql(String packageName) {
        long vid = SnowFlakeUtils.INSTANCE.getSnowFlakeId();
        String tagModelSql = String.format("%s INSERT VERTEX jar(name) VALUES %d :('%s')",
                getSpace(), vid, packageName);
        return NebulaGraphSqlVO.builder()
                .vid(vid)
                .graphSql(tagModelSql)
                .build();
    }

    public String EInsertDepMgmtSql(String branch, String commitId, GraphEdgeVO graphEdgeVO) {
        return String.format("%s INSERT EDGE dependency_management(branch, commit_id) VALUES %d->%d :('%s', '%s')",
                getSpace(), graphEdgeVO.getSourceVid(), graphEdgeVO.getSinkVid(), branch, commitId);

    }

    public String EInsertDepsSql(String commitId, GraphEdgeVO graphEdgeVO) {
        return String.format("%s INSERT EDGE dependencies(commit_id) VALUES %d->%d :('%s')",
                getSpace(), graphEdgeVO.getSourceVid(), graphEdgeVO.getSinkVid(), commitId);
    }

    public String EInsertDepSql(String scope, GraphEdgeVO graphEdgeVO) {
        return String.format("%s INSERT EDGE dependency(scope) VALUES %d->%d :('%s')",
                getSpace(), graphEdgeVO.getSourceVid(), graphEdgeVO.getSinkVid(), scope);
    }

    public String EInsertParentSql(GraphEdgeVO graphEdgeVO) {
        return String.format("%s INSERT EDGE parent() VALUES %d->%d :()",
                getSpace(), graphEdgeVO.getSourceVid(), graphEdgeVO.getSinkVid());
    }

    public String VLookUpGitSql(String gitUrl) {
        return String.format("%s LOOKUP ON git WHERE git.remote_url == '%s'", getSpace(), gitUrl);
    }

    public String VLookUpModelSql(ArtifactVO artifactVO) {
        return String.format("%s LOOKUP ON model WHERE model.group_id == '%s'" +
                        " AND model.artifact_id == '%s' AND model.type == '%s' AND model.version == '%s'",
                getSpace(), artifactVO.getGroupId(), artifactVO.getArtifactId(), artifactVO.getType(), artifactVO.getVersion());
    }

    public String VLookUpParentModelSql(ArtifactVO artifactVO) {
        return String.format("%s LOOKUP ON parent_model WHERE parent_model.group_id == '%s'" +
                        " AND parent_model.artifact_id == '%s' AND parent_model.version == '%s'",
                getSpace(), artifactVO.getGroupId(), artifactVO.getArtifactId(), artifactVO.getVersion());
    }

    public String VLookUpJarSql(String packageName) {
        return String.format("%s LOOKUP ON jar WHERE jar.name == '%s'", getSpace(), packageName);
    }

}
