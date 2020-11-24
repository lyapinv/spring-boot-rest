package ru.vtb.servicemesh.test.client.controller.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

@RestController
@Slf4j
public class FileDownloadController {

    @Autowired
    private FileUploadService fileDownloadService;

    // http://localhost:8081/downloadFile/one_and_half_mb.png/2/5
    @RequestMapping({"/downloadFile/{fileName}/{threads}", "/downloadFile/{fileName}/{threads}/{sec}"})
    public String downloadFile(@PathVariable("threads") int threadsCount,
                               @PathVariable("fileName") String fileName,
                               @PathVariable(value = "sec", required = false) Integer runSeconds) {
        log.info("Start files downloading. threadsCount: {}, fileName: {}", threadsCount, fileName);
        ThreadPoolExecutor executorService = getBlockingThreadPoolExecutor(threadsCount);

        long runUntil = System.currentTimeMillis() + (runSeconds == null ? 1 : runSeconds) * 1000;
        int cnt = 0;
        while (System.currentTimeMillis() < runUntil) {
            final int loopCount = cnt++;
            executorService.execute(() -> {
                fileDownloadService.postFile(fileName);
                log.info("!!! Thread {}, cnt: {}, file uploaded", Thread.currentThread().getName(), loopCount);
            });
        }
        executorService.shutdown();
        return "Files download finished. cnt: " + cnt;
    }

    private ThreadPoolExecutor getBlockingThreadPoolExecutor(int threadsCount) {
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(threadsCount,
                threadsCount,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1));
        // when the blocking queue is full, this tries to put into the queue which blocks
        executorService.setRejectedExecutionHandler((r, executor) -> {
            try {
                // block until there's room
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RejectedExecutionException("Producer thread interrupted", e);
            }
        });
        return executorService;
    }
}
