package com.kevinsa.security.bom.analyze.service.common.impl;

import static com.kevinsa.security.bom.analyze.constant.ApplicationConstants.MAVEN_POM_FILE;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kevinsa.security.bom.analyze.service.common.MavenCommonService;
import com.kevinsa.security.bom.analyze.utils.FileCommonUtils;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.ArtifactVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.JarMavenVO;

@Service
public class MavenCommonServiceImpl implements MavenCommonService {

    @Autowired
    private FileCommonUtils fileCommonUtils;

    private static final String POMTYPE = ".pom";

    /**
     * 获取制定路径下的pom文件相关信息
     * todo:是否需要返回三元组project的groupId、artifactId、version,需要缓存<properties></properties>中的信息
     * todo:怎么处理多个模块之间的关系
     *
     * @throws Exception
     */
    @Override
    public Model getPomInfo(File filePath) throws Exception {
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model;
        try {
            Reader reader = ReaderFactory.newXmlReader(filePath);
            model = pomReader.read(reader);
        } catch (IOException | XmlPullParserException e) {
            throw new Exception(e);
        }
        return model;
    }

    /**
     * 向上查询parent，补齐dependencies中version的缺失
     * 从parent即.pom文件中<dependencyManagement>和<dependencies>中获取所有的依赖信息
     * 如version为变量，根据
     * 其中.pom文件解析的信息不发送kafka
     *
     * @param file
     * @param fileName
     * @return
     * @throws Exception
     */
    @Override
    public JarMavenVO getJarMvnInfoByPom(File file, String fileName, List<ArtifactVO> dependencyList) throws Exception {
        Model model = getPomInfo(file);

        ArtifactVO parent = null;
        if (model.getParent() != null) {
            // 构建parent
            parent = ArtifactVO.builder()
                    .artifactId(model.getParent().getArtifactId())
                    .groupId(model.getParent().getGroupId())
                    .version(model.getParent().getVersion())
                    .build();
            String mvnPath = fileCommonUtils.getMvnRepoPath(parent);
            getParentInfo(mvnPath, dependencyList);
            // 根据parent信息获取构建路径判断存在jar、pom
        }

        Consumer<Dependency> consumer = (tmp) -> {
            if (tmp.getVersion() != null && tmp.getVersion().matches("(.*)\\$(.*)")) {
                String key = tmp.getVersion().replaceAll("(\\$|\\{|\\})", "");
                tmp.setVersion(model.getProperties().getProperty(key));
            }
            if (tmp.getVersion() == null) {
                dependencyList.forEach(tmpDep -> {
                    if (tmpDep.getGroupId().equals(tmp.getGroupId()) && tmpDep.getArtifactId().equals(tmp.getArtifactId())) {
                        tmp.setVersion(tmpDep.getVersion());
                        return;
                    }
                });
            }
            dependencyList.add(ArtifactVO.builder()
                    .artifactId(tmp.getArtifactId())
                    .groupId(tmp.getGroupId())
                    .version(tmp.getVersion())
                    .scope(tmp.getScope())
                    .type(tmp.getType())
                    .build());
        };

        model.getDependencies().forEach(consumer);
        if ( model.getDependencyManagement() != null && model.getDependencyManagement().getDependencies() != null){
            model.getDependencyManagement().getDependencies().forEach(consumer);
        }

        if (fileName.equals(MAVEN_POM_FILE)) {
            List<ArtifactVO> dependencies = new ArrayList<>();
            model.getDependencies().forEach(tmp -> {
                dependencies.add(ArtifactVO.builder()
                        .artifactId(tmp.getArtifactId())
                        .groupId(tmp.getGroupId())
                        .version(tmp.getVersion())
                        .scope(tmp.getScope())
                        .type(tmp.getType())
                        .build());
            });
            return JarMavenVO.builder()
                    .parent(parent)
                    .artifactId(model.getArtifactId())
                    .groupId(model.getGroupId())
                    .version(model.getVersion())
                    .dependencies(dependencies)
                    .build();
        }
        return null;
    }

    protected void getParentInfo(String mvnPath, List<ArtifactVO> dependencyList) throws Exception {
        File mvnPathFile = new File(mvnPath);
        if (!mvnPathFile.exists()) {
            return;
        }
        for (File inner : Objects.requireNonNull(mvnPathFile.listFiles())) {
            if (inner.getName().endsWith(POMTYPE)) {
                // 走pom解析流程
                getJarMvnInfoByPom(new File(inner.getPath()), inner.getName(), dependencyList);
            }
            // todo: 如果还是jar包，是否要继续做jar解析
        }
    }


}
