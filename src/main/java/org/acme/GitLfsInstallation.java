package org.acme;

import java.nio.file.Path;

public class GitLfsInstallation {


    public boolean check(Path repo) {
        int statusCode = new ExecWrapper().execute("git lfs", repo);

         return statusCode == 0;
    }

}
