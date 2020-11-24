package ru.vtb.servicemesh.test.client.controller.file;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "storage")
public class FileStorageProperties {
    private String location;
}
