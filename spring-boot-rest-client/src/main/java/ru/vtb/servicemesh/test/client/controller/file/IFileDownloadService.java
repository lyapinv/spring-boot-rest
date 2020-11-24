package ru.vtb.servicemesh.test.client.controller.file;

import org.springframework.core.io.Resource;

public interface IFileDownloadService {
    void init();
    Resource loadFile(String fileName);
}
