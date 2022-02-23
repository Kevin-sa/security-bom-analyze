package com.kevinsa.security.bom.analyze.utils;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Component
public class ExecUtils {

    public String execOut(String cmd, String path) throws IOException {
        Process p;
        String[] cmdArr = new String[]{"sh", "-c", cmd};
        p = Runtime.getRuntime().exec(cmdArr, null, new File(path));
        //取得命令结果的输出流
        InputStream fis = p.getInputStream();
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;

        while ((len = fis.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }

        outSteam.close();
        return outSteam.toString();
    }

    public void exec(String cmd, String path) throws IOException, InterruptedException {
        Process p;
        String[] cmdArr = new String[]{"sh", "-c", cmd};
        p = Runtime.getRuntime().exec(cmdArr, null, new File(path));
        p.waitFor(5, TimeUnit.SECONDS);
        p.destroy();
    }

}
