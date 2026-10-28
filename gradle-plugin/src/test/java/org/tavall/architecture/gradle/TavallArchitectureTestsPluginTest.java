package org.tavall.architecture.gradle;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class TavallArchitectureTestsPluginTest {
    @Test
    void tavallGitHubTokenIsUsedWhenGithubActionsTokenIsAbsent() {
        assertEquals("tavall-token", TavallArchitectureTestsPlugin.githubToken(Map.of("GH_TOKEN", "tavall-token")));
        assertEquals(
                "github-actions-token",
                TavallArchitectureTestsPlugin.githubToken(Map.of(
                        "GITHUB_TOKEN", "github-actions-token",
                        "GH_TOKEN", "tavall-token"
                ))
        );
    }

    @Test
    void applyingPluginRegistersOptInArchitectureTask() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(TavallArchitectureTestsPlugin.class);

        assertNotNull(project.getTasks().findByName("architectureTest"));
        assertTrue(project.getTasks().getByName("architectureTest") instanceof org.gradle.api.tasks.testing.Test);
        assertFalse(project.getTasks().getByName("check").getTaskDependencies()
                .getDependencies(project.getTasks().getByName("check"))
                .contains(project.getTasks().getByName("architectureTest")));
    }


    @Test
    void webArchitectureModuleIsSelectable(@TempDir Path projectDirectory) throws IOException {
        Files.writeString(
                projectDirectory.resolve("settings.gradle.kts"),
                "rootProject.name = \"web-rule-consumer\"\n"
        );
        Files.writeString(
                projectDirectory.resolve("build.gradle.kts"),
                """
                plugins {
                    java
                    id("org.tavall.architecture-tests")
                }

                architectureTests {
                    modules.set(listOf("web"))
                }
                """
        );

        BuildResult result = runner(projectDirectory)
                .withArguments("tasks", "-PtavallArchitectureVersion=" + architectureVersion())
                .build();

        assertNotNull(result.task(":tasks"));
        assertEquals(TaskOutcome.SUCCESS, result.task(":tasks").getOutcome());
    }


    private static String architectureVersion() {
        String version = System.getProperty("tavall.architecture.testVersion");
        assertNotNull(version, "The functional test requires the local architecture module version");
        return version;
    }

    private static GradleRunner runner(Path projectDirectory) {
        return GradleRunner.create()
                .withProjectDir(projectDirectory.toFile())
                .withPluginClasspath()
                .withTestKitDir(projectDirectory.resolve(".gradle-test-kit").toFile())
                .withArguments("--no-daemon")
                .forwardOutput();
    }
}
