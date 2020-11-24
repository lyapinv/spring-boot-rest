package ru.vtb.servicemesh.test.client.controller.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class FileUploadService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FileDownloadService fileDownloadService;

    @Value("${FILE_UPLOAD_SERVER_URI:https://localhost:8082}")
    private String fileUploadServerURL;

    public void postFile(String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ContentDisposition contentDisposition = ContentDisposition
                .builder("form-data")
                .name("file")
                .filename(filename)
                .build();
        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
        HttpEntity<Resource> fileEntity = new HttpEntity<>(fileDownloadService.loadFile(filename), fileMap);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileEntity);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    fileUploadServerURL + "/singleFileUpload",
                    requestEntity,
                    String.class);
            log.info("Response code: {}", response.getStatusCode());
        } catch (HttpClientErrorException e) {
            log.error("Exception while processing request: ", e);
        }
    }
}
