package com.kevinsa.security.bom.analyze.utils;

import com.kevinsa.security.bom.analyze.vo.mvnPlugin.ArtifactVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class FileCommonUtils {

    @Value("${path.mvn.repo}")
    private String MVNREPO;

    public String getMvnRepoPath(ArtifactVO parent) {
        return MVNREPO + parent.getGroupId().replace(".", "/") + "/" + parent.getArtifactId() + "/" + parent.getVersion();
    }

    public boolean isFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public List<File> fileFind(File path, String type) {
        List<File> result = new ArrayList<>();
        for (File inner : Objects.requireNonNull(path.listFiles(pathname -> pathname.isDirectory() || pathname.getName().equals(type)))) {
            if (inner.isDirectory()) {
                result.addAll(fileFind(inner, type));
            } else if (inner.getName().equals(type)) {
                result.add(inner);
            }
        }
        return result;
    }

    public String getBasePath(File path, String file) {
        return path.toString().substring(0, path.toString().lastIndexOf(file));
    }

}
