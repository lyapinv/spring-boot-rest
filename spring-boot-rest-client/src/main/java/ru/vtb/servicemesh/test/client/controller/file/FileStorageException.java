package ru.vtb.servicemesh.test.client.controller.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileStorageException extends RuntimeException {
    private String message;
    private Throwable cause;
}