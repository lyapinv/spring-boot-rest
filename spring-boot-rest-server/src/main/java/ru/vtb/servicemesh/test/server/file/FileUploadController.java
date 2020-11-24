package ru.vtb.servicemesh.test.server.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
public class FileUploadController {

    @RequestMapping("/fileUploadPing")
    public String ping() {
        return "pong";
    }

    @PostMapping("/singleFileUpload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            log.info("Received file. bytes: {}, length: {}, name: {}",
                    file.getBytes().length, file.getSize(), file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.OK).body("File uploaded. Bytes received: " + file.getSize());
    }
}
