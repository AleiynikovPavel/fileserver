package com.sabd.fileserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;

import com.sabd.fileserver.model.FileEntity;
import com.sabd.fileserver.dto.NewFile;
import com.sabd.fileserver.service.FileStoreService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class FileController {

    @Autowired
    FileStoreService fileStoreService;

    @PostMapping("upload/")
    public Mono<NewFile> upload(@RequestPart("file") Mono<FilePart> filePart) {
        return fileStoreService.saveFile(filePart);
    }

    @PostMapping("download/")
    public Mono<ResponseEntity<InputStreamResource>> download(@RequestParam("filename") String filename) {
        return fileStoreService.getFile(filename)
                .map(it -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(new InputStreamResource(it)));
    }

    @GetMapping("list/")
    public Flux<FileEntity> list() {
        return fileStoreService.getFileList();
    }

}
