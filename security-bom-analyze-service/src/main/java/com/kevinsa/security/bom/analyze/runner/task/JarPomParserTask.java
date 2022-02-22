package com.kevinsa.security.bom.analyze.runner.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.kevinsa.security.bom.analyze.runner.consumer.GraphMavenParserConsumer;
import com.kevinsa.security.bom.analyze.service.common.impl.KafkaCommonServiceImpl;
import com.kevinsa.security.bom.analyze.service.common.impl.MavenCommonServiceImpl;
import com.kevinsa.security.bom.analyze.utils.FileCommonUtils;
import com.kevinsa.security.bom.analyze.utils.ObjectMapperUtils;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.ArtifactVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.JarMavenVO;

import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kevinsa.security.bom.analyze.utils.EncryptUtils;


@Component
public class JarPomParserTask {

    private static final Logger logger = LoggerFactory.getLogger(GraphMavenParserConsumer.class);

    @Autowired
    private EncryptUtils encryptUtils;

    @Autowired
    private FileCommonUtils fileCommonUtils;

    @Autowired
    private KafkaCommonServiceImpl kafkaCommonService;

    @Autowired
    private MavenCommonServiceImpl mavenCommonService;

    private static final String TYPE = "pom.xml";


    @Value("${kafka.topic.jar-maven}")
    private String TOPIC;


    protected String getUnzipCmd(String fileName, String fileHash) {
        return "unzip -q " + fileName + " -d /tmp/" + fileHash + "/";
    }

    /**
     * 1、先判断文件是否存在
     * 2、写入tmp临时文件下 /tmp/ + hash/
     * 3、做遍历扫描，获取目录下的pom文件
     * 4、读取pom文件获取有用信息，kafka send message
     * 5、删除对应的临时文件
     *
     * @param path
     * @param fileName
     */
    public void execute(String path, String fileName) {
        try {
            if (!fileCommonUtils.isFileExists(path + fileName)) {
                return;
            }
            String hash = encryptUtils.md5Encrypt(path + fileName);
            if (!fileCommonUtils.isFileExists("/tmp/" + hash + "/")) {
                String unZipCmd = getUnzipCmd(fileName, hash);
                Process process = Runtime.getRuntime().exec(unZipCmd, null, new File(path));
                process.waitFor();
                process.destroy();
            }
            List<File> files = fileCommonUtils.fileFind(new File("/tmp/" + hash + "/"), TYPE);
            if (files.size() > 1) {
                logger.info("JarPomParserTask jar:{} pom.xml file > 1", fileName);
            }
            if (files.size() == 0) {
                logger.info("JarPomParserTask jar:{} pom.xml file is 0", fileName);
            }
            // 是否默认只去第一个
            for (File file : files) {
                Model model = mavenCommonService.getPomInfo(fileCommonUtils.getBasePath(file, TYPE), TYPE);

                ArtifactVO parent = null;
                if (model.getParent() != null) {
                    parent = ArtifactVO.builder()
                            .artifactId(model.getParent().getArtifactId())
                            .groupId(model.getParent().getGroupId())
                            .version(model.getParent().getVersion())
                            .build();
                }
                List<ArtifactVO> dependencies = new ArrayList<>();
                model.getDependencies().forEach(tmp -> dependencies.add(ArtifactVO.builder()
                        .artifactId(tmp.getArtifactId())
                        .groupId(tmp.getGroupId())
                        .version(tmp.getVersion())
                        .scope(tmp.getScope())
                        .type(tmp.getType())
                        .build()));
                JarMavenVO jarMavenVO = JarMavenVO.builder()
                        .packageName(fileName)
                        .parent(parent)
                        .artifactId(model.getArtifactId())
                        .groupId(model.getGroupId())
                        .version(model.getVersion())
                        .dependencies(dependencies)
                        .build();
                logger.debug("send mesg jar name:{}", jarMavenVO.getPackageName());
                kafkaCommonService.sendMsg(TOPIC, ObjectMapperUtils.toJSON(jarMavenVO));
            }
            Process process1 = Runtime.getRuntime().exec("rm -rf " + hash, null, new File("/tmp"));
            process1.waitFor();
            process1.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
