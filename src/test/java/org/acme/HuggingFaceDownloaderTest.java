package org.acme;

import org.junit.jupiter.api.Test;

public class HuggingFaceDownloaderTest {

    @Test
    public void download() {
        HuggingFaceDownloader huggingFaceDownloader = new HuggingFaceDownloader();
        System.out.println(huggingFaceDownloader.clone());
    }


}