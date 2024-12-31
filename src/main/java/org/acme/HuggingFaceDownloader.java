package org.acme;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.attributes.FilterCommandRegistry;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lfs.BuiltinLFS;
import org.eclipse.jgit.lfs.CleanFilter;
import org.eclipse.jgit.lfs.SmudgeFilter;
import org.eclipse.jgit.lfs.lib.Constants;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.util.LfsFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HuggingFaceDownloader {

    private static final String SMUDGE_NAME = org.eclipse.jgit.lib.Constants.BUILTIN_FILTER_PREFIX
            + Constants.ATTR_FILTER_DRIVER_PREFIX
            + org.eclipse.jgit.lib.Constants.ATTR_FILTER_TYPE_SMUDGE;

    private static final String CLEAN_NAME = org.eclipse.jgit.lib.Constants.BUILTIN_FILTER_PREFIX
            + Constants.ATTR_FILTER_DRIVER_PREFIX
            + org.eclipse.jgit.lib.Constants.ATTR_FILTER_TYPE_CLEAN;


    public Path clone() {

        String baseDirectory = System.getProperty("user.home") + "/huggingface";
        final String modelName = "test";

        Path modelDir = Paths.get(baseDirectory, modelName);

        try {
            Files.deleteIfExists(modelDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {

            Git master = Git.cloneRepository()
                    .setURI("https://github.com/Apress/repo-with-large-file-storage.git")
                    .setDirectory(modelDir.toAbsolutePath().toFile())
                    .setBranch("master")
                    .call();


            BuiltinLFS.register();
            FilterCommandRegistry.register(SMUDGE_NAME, SmudgeFilter.FACTORY);
            FilterCommandRegistry.register(CLEAN_NAME, CleanFilter.FACTORY);

            final LfsFactory instance = LfsFactory.getInstance();

            Repository repository = new FileRepository(new File(modelDir.toAbsolutePath().toFile(), ".git"));
            Git git = new Git(repository);

            /**LfsFactory.LfsInstallCommand installCommand = BuiltinLFS.getInstance().getInstallCommand();
            try {
                installCommand.setRepository(repository).call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }**/

            StoredConfig config = repository.getConfig();
            config.setBoolean(ConfigConstants.CONFIG_FILTER_SECTION,
                    ConfigConstants.CONFIG_SECTION_LFS,
                    ConfigConstants.CONFIG_KEY_USEJGITBUILTIN, true);
            config.setBoolean(ConfigConstants.CONFIG_FILTER_SECTION,
                    ConfigConstants.CONFIG_SECTION_LFS,
                    ConfigConstants.CONFIG_KEY_REQUIRED, false);
            config.setString("filter", "lfs", "clean", CLEAN_NAME);
            config.setString("filter", "lfs", "smudge", SMUDGE_NAME);
            config.setString("filter", "lfs", "required", "true");
            config.save();

            PullResult result = git.pull().call();
            System.out.println(result.isSuccessful());
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return modelDir;


    }

}
