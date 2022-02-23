package com.kevinsa.security.bom.analyze.runner.task;

import java.io.File;
import java.util.ArrayList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kevinsa.security.bom.analyze.runner.consumer.GraphMavenParserConsumer;
import com.kevinsa.security.bom.analyze.service.common.impl.KafkaCommonServiceImpl;
import com.kevinsa.security.bom.analyze.service.common.impl.MavenCommonServiceImpl;
import com.kevinsa.security.bom.analyze.utils.EncryptUtils;
import com.kevinsa.security.bom.analyze.utils.ExecUtils;
import com.kevinsa.security.bom.analyze.utils.FileCommonUtils;
import com.kevinsa.security.bom.analyze.utils.ObjectMapperUtils;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.ArtifactVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.JarMavenVO;


@Component
public class JarPomParserTask {

    private static final Logger logger = LoggerFactory.getLogger(GraphMavenParserConsumer.class);

    @Autowired
    private EncryptUtils encryptUtils;

    @Autowired
    private ExecUtils execUtils;

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
     * <p>
     * 获取parent中的properties以此获取依赖的版本信息
     * 1、判断是否存在<parent></>
     * 2、读取parent中的groupId\artifactId，在本地仓库目录下获取其类型为jar、pom
     * 3、jar类型做jar类型处理、pom文件直接读取，同时判断是否存在<properties></>缓存处理，用于后续填充
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
                execUtils.exec(unZipCmd, path);
            }
            List<File> files = fileCommonUtils.fileFind(new File("/tmp/" + hash + "/"), TYPE);
            if (files.size() > 1) {
                logger.info("JarPomParserTask jar:{} pom.xml file > 1", fileName);
            }
            if (files.size() == 0) {
                logger.info("JarPomParserTask jar:{} pom.xml file is 0", fileName);
            }
            List<ArtifactVO> dependencyList = new ArrayList<>();
            // 是否默认只去第一个
            for (File file : files) {
                JarMavenVO jarMavenVO = mavenCommonService.getJarMvnInfoByPom(new File(fileCommonUtils.getBasePath(file, TYPE)), TYPE, dependencyList);
                jarMavenVO.setPackageName(fileName);
                logger.debug("send msg jar name:{}", jarMavenVO.getPackageName());
                kafkaCommonService.sendMsg(TOPIC, ObjectMapperUtils.toJSON(jarMavenVO));
                break;
            }
            execUtils.exec("rm -rf " + hash, "/tmp");
        } catch (Exception e) {
            logger.error("execute error", e);
        }
    }

}
