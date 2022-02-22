package com.kevinsa.security.bom.analyze.service.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ScaParseServiceUnitTemplate implements ScaParseServiceBaseUnit {

    private static final Logger logger = LoggerFactory.getLogger(ScaParseServiceUnitTemplate.class);


    @Override
    public Object execute(String message) {
        try {
            beforeBusiness();
            Object businessResult = doBusiness(message);
            afterBusiness(businessResult);
        } catch (Exception e) {
            logger.error("ScaParseServiceUnitTemplate error:", e);
        }
        return null;
    }

    /**
     * 是否需要做提前校验，暂时列出
     * @param message
     * @return
     */
    protected abstract Object paramCheck(String message);

    /**
     * 实际执行逻辑后的扩展函数
     */
    protected abstract void beforeBusiness();

    /**
     * @return 实际执行逻辑
     */
    protected abstract Object doBusiness(String message);

    /**
     * 实际执行逻辑后的扩展函数
     */
    protected abstract void afterBusiness(Object object);
}
