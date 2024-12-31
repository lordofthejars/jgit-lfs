package org.acme;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GitLfsInstallationTest {

    @Test
    public void shouldDetectGitLfsInstallation() {
        Path currentDir = Paths.get(".");

        GitLfsInstallation gitLfsInstallation = new GitLfsInstallation();
        System.out.println(gitLfsInstallation.check(currentDir));
    }

}
