package com.kevinsa.security.bom.analyze.service.common.impl;


import static com.kevinsa.security.bom.analyze.constant.redis.RedisConstant.STATUSCODEEXPIRE;

import java.util.Calendar;

import com.kevinsa.security.bom.analyze.enums.TaskStatusCode;
import com.kevinsa.security.bom.analyze.service.common.BizCommonService;
import com.kevinsa.security.bom.analyze.utils.DateUtils;
import com.kevinsa.security.bom.analyze.utils.EncryptUtils;
import com.kevinsa.security.bom.analyze.utils.ObjectMapperUtils;
import com.kevinsa.security.bom.analyze.utils.RedisUtils;
import com.kevinsa.security.bom.analyze.vo.biz.StatusCodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BizCommonServiceImpl implements BizCommonService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private DateUtils dateUtils;

    private Calendar calendar;

    @Override
    public void updateStatusCode(TaskStatusCode code, String key, String position) {
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
        StatusCodeVO statusCodeVO = StatusCodeVO.builder()
                .status(code.getMsg())
                .position(position)
                .updateTime(dateUtils.currentTime())
                .build();

        redisUtils.set(key, ObjectMapperUtils.toJSON(statusCodeVO));
        redisUtils.expire(key, STATUSCODEEXPIRE);
    }

    @Override
    public Object getStatusCode(String key) {
        return redisUtils.get(key);
    }
}
