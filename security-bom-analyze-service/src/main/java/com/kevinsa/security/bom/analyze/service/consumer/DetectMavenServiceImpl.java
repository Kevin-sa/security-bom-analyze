package com.kevinsa.security.bom.analyze.service.consumer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.kevinsa.security.bom.analyze.service.base.ScaParseServiceUnitTemplate;
import com.kevinsa.security.bom.analyze.utils.ObjectMapperUtils;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.ArtifactVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.DependencyNodeVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.MessageGraphVO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * pom文件解析获取依赖后判断是否存在漏洞实现类
 * afterBusiness() 函数做告警处理
 * detectArtifact() 函数为具体判断当前依赖是否存在漏洞
 */
@Component
@Slf4j
public class DetectMavenServiceImpl extends ScaParseServiceUnitTemplate {

    private List<ArtifactVO> vulList = new ArrayList<>();

    @Override
    protected Object paramCheck(String message) {
        return null;
    }

    @Override
    protected Object beforeBusiness(String message) {
        Assert.notNull(message, "DetectMavenServiceImpl message is null");
        return ObjectMapperUtils.fromJSON(message, MessageGraphVO.class);
    }

    @Override
    protected Object doBusiness(Object beforeResult) {
        MessageGraphVO request = (MessageGraphVO) beforeResult;
        if (detectArtifact(request.getArtifactVO())) {
            vulList.add(request.getArtifactVO());
        }

        parserDeps(request.getDependencyNodeVO());
        return vulList.size() != 0;
    }

    @Override
    protected void afterBusiness(Object object) {
        if ((Boolean) object) {
            // 告警处理
            return;
        }
    }

    protected void parserDeps(DependencyNodeVO dependencyNodeVO) {
        if (detectArtifact(dependencyNodeVO.getArtifactVO())) {
            vulList.add(dependencyNodeVO.getArtifactVO());
        }

        if (dependencyNodeVO.getChildrenVO().size() != 0) {
            dependencyNodeVO.getChildrenVO().forEach(this::parserDeps);
        }
    }

    protected boolean detectArtifact(ArtifactVO artifactVO) {
        return true;
    }

}

