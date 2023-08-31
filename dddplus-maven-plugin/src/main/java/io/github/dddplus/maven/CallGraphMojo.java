/*
 * Copyright DDDplus Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.github.dddplus.maven;

import io.github.dddplus.ast.report.CallGraphReport;
import io.github.dddplus.ast.view.CallGraphRenderer;
import io.github.dddplus.bce.CallGraphConfig;
import io.github.dddplus.bce.CallGraphParser;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * 无需标注，类的调用关系图可视化.
 *
 * <p></p>
 * <ul>IDEA提供了 {@code [Navigate | Call Hierarchy]} 功能，但它有两个核心问题：
 * <li>它不能指定过滤条件，导致噪音过载</li>
 * <li>必须先选中一个具体的方法才能操作，而我们希望提供全局视角的调用关系图</li>
 * </ul>
 */
@Mojo(name = "call", aggregator = true)
public class CallGraphMojo extends AbstractMojo {

    /**
     * Colon separated directories.
     */
    @Parameter(property = "jars", required = true)
    String jars;

    @Parameter(property = "targetFile", required = true)
    String targetFile;

    @Parameter(property = "configFile", required = true)
    String configFile;

    /**
     * ortho | spline | polyline | curved
     */
    @Parameter(property = "splines")
    String splines = "curved";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Generating call graph ...");
        try {
            String[] jarFiles = jars.split(":");
            CallGraphReport report = CallGraphParser.parse(jarFiles,
                    CallGraphConfig.fromFile(configFile));
            new CallGraphRenderer()
                    .targetCallGraphDotFile(targetFile)
                    .splines(splines)
                    .withReport(report)
                    .render();
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

}
