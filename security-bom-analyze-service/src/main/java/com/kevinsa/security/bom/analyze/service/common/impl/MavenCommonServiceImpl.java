package com.kevinsa.security.bom.analyze.service.common.impl;

import com.kevinsa.security.bom.analyze.service.common.MavenCommonService;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

@Service
public class MavenCommonServiceImpl implements MavenCommonService {

    /**
     * 获取制定路径下的pom文件相关信息
     * todo:是否需要返回三元组project的groupId、artifactId、version,需要缓存<properties></properties>中的信息
     * todo:怎么处理多个模块之间的关系
     *
     * @param baseDir
     * @throws Exception
     */
    @Override
    public Model getPomInfo(String baseDir, String type) throws Exception {
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model;
        try {
            Reader reader = ReaderFactory.newXmlReader(new File(baseDir, type));
            model = pomReader.read(reader);
        } catch (IOException | XmlPullParserException e) {
            throw new Exception(e);
        }
        return model;
    }
}
