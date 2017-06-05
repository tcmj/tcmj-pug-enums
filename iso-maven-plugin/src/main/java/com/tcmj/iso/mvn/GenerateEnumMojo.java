package com.tcmj.iso.mvn;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/** tcmj iso generator maven plugin. */
@Mojo(name = "generate-enum", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class GenerateEnumMojo extends AbstractMojo {
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("x x x x x x x x x x x x x x x x x x x x x x x x x x x x x");
    getLog().info("x   Welcome to tcmj iso enum generator maven plugin!    x");
    getLog().info("x x x x x x x x x x x x x x x x x x x x x x x x x x x x x");
  }
}
