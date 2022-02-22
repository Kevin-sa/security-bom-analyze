package com.kevinsa.security.bom.maven.plugin.parser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.json.JSONObject;

import com.kevinsa.security.bom.maven.plugin.parser.constant.KafkaConf;
import com.kevinsa.security.bom.maven.plugin.parser.vo.ArtifactVO;
import com.kevinsa.security.bom.maven.plugin.parser.vo.DependencyNodeVO;
import com.kevinsa.security.bom.maven.plugin.parser.vo.MessageGraphVO;


@Mojo(name = "parser")
public class ParserMojo extends AbstractMojo {
    // -DgitAddress=
    @Parameter(defaultValue = "${gitAddress}", readonly = true, required = true)
    private String gitAddress;

    // -Dbranch=
    @Parameter(defaultValue = "${branch}", readonly = true, required = true)
    private String branch;

    @Parameter(defaultValue = "${commitId}", readonly = true, required = true)
    private String commitId;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "scope")
    private String scope;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Component(hint = "default")
    private DependencyGraphBuilder dependencyGraphBuilder;

    @Parameter(defaultValue = "${reactorProjects}", readonly = true, required = true)
    private List<MavenProject> reactorProjects;

    private DependencyNode rootNode;

    private KafkaProducer<String, String> produce;

    // todo:任务成功失败的状态码
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {

            // kafka init
            KafkaConf kafkaConf = new KafkaConf();
            produce = kafkaConf.getKafkaInstance();

            ProjectBuildingRequest buildingRequest =
                    new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());
            buildingRequest.setProject(project);

            rootNode = dependencyGraphBuilder.buildDependencyGraph(buildingRequest, null, reactorProjects);
            executeMessageParam();

            getLog().info("success");

        } catch (DependencyGraphBuilderException exception) {
            throw new MojoExecutionException("Cannot build project dependency graph", exception);
        }
    }

    // 参数组装，从DependencyNode中提取关心的参数to kafka
    private void executeMessageParam() {

        // 组装artifact
        ArtifactVO artifactVO = ArtifactVO.builder()
                .artifactId(rootNode.getArtifact().getArtifactId())
                .groupId(rootNode.getArtifact().getGroupId())
                .version(rootNode.getArtifact().getVersion())
                .scope(rootNode.getArtifact().getScope())
                .type(rootNode.getArtifact().getType())
                .build();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = format.format(calendar.getTime());

        rootNode.getChildren().forEach(dependencyNode -> {
            MessageGraphVO messageGraphVO = MessageGraphVO.builder()
                    .gitAddress(gitAddress)
                    .commitId(commitId)
                    .branch(branch)
                    .artifactVO(artifactVO)
                    .dependencyNodeVO(covertDependency(dependencyNode))
                    .createTime(createTime)
                    .build();
            kafkaSend(messageGraphVO);
        });
    }

    /**
     * 使用future.get()同步方法，在当前场景下可以接受kafka发送带来的linger
     * @param messageGraphVO
     */
    protected void kafkaSend(MessageGraphVO messageGraphVO) {
        try {
            JSONObject json = new JSONObject(messageGraphVO);
            ProducerRecord<String, String> record = new ProducerRecord<>("security_sca_pom", json.toString());
            Future<RecordMetadata> future = produce.send(record);
            future.get();
        } catch (Exception exception) {
            getLog().error(exception);
        }
    }

    private ArtifactVO convertArtifact(Artifact artifact) {
        return ArtifactVO.builder()
                .artifactId(artifact.getArtifactId())
                .groupId(artifact.getGroupId())
                .version(artifact.getVersion())
                .scope(artifact.getScope())
                .type(artifact.getType())
                .build();
    }

    private DependencyNodeVO covertDependency(DependencyNode dependencyNode) {
        if (dependencyNode == null) {
            return DependencyNodeVO.builder().build();
        }
        return DependencyNodeVO.builder()
                .artifactVO(convertArtifact(dependencyNode.getArtifact()))
                .childrenVO(covertChildren(dependencyNode.getChildren()))
                .build();
    }

    private List<DependencyNodeVO> covertChildren(List<DependencyNode> children) {
        List<DependencyNodeVO> dependencyNodeVOS = new ArrayList<>();
        if (children.size() == 0) {
            return dependencyNodeVOS;
        }
        children.forEach(item -> dependencyNodeVOS.add(covertDependency(item)));
        return dependencyNodeVOS;
    }

}
