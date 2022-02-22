package com.kevinsa.security.bom.analyze.constant.nebula;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;

import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


/**
 * todo：mybatis、jdbc的单例模式。
 * 枚举模式不建议在构造函数中抛出异常，暂忽略
 */

@Service
public class GraphClient {
    private Session session = null;

    @Value("${nebula.host}")
    private String host;

    @Value("${nebula.port}")
    private int port;

    @Value("${nebula.username}")
    private String username;

    @Value("${nebula.password}")
    private String password;

    public Session getInstance() throws Exception {
        if (session == null) {
            try {
                NebulaPool pool = new NebulaPool();
                NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
                nebulaPoolConfig.setMaxConnSize(100);
                List<HostAddress> addresses = Collections.singletonList(new HostAddress(host, port));
                pool.init(addresses, nebulaPoolConfig);
                session = pool.getSession(username, username, false);
            } catch (Exception e) {
                throw new Exception("graph init error", e);
            }
        }
        return session;
    }

}