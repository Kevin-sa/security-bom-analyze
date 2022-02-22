package com.kevinsa.security.bom.maven.plugin.status;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "status")
public class StatusMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        String status = "security-bom-java-maven-plugin status:success";
        getLog().info(status);
    }
}
