package com.kevinsa.security.bom.analyze.constant.nebula;

import lombok.Getter;

@Getter
public enum GraphSpace {
    SECURITY_JAVA_MAVEN_SPACE("java_maven");

    private final String spaceName;

    GraphSpace(String spaceName) {
        this.spaceName = spaceName;
    }
}
