package com.sabd.fileserver.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.sabd.fileserver.chunkstore.ChunkProducer;
import com.sabd.fileserver.dto.ChunkStatisitic;
import com.sabd.fileserver.dto.FileEntity;
import com.sabd.fileserver.dto.NewFile;
import com.sabd.fileserver.filestore.FileWriter;
import com.sabd.fileserver.repository.FileRepository;
import com.sabd.fileserver.utils.PipedStreamSubscriber;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class FileStoreService {

    Logger logger = LoggerFactory.getLogger(ChunkStoreService.class);

    @Autowired
    FileRepository fileRepository;
    @Autowired
    ChunkStoreService chunkStoreService;
    @Value("${store.file.path}")
    private String storePath;

    public static Mono<InputStream> createInputStream(Flux<byte[]> flux) {
        PipedInputStream in = new PipedInputStream();
        flux.subscribeOn(Schedulers.boundedElastic())
                .subscribe(new PipedStreamSubscriber(in));

        return Mono.just(in);
    }

    @PostConstruct
    private void init() {
        if (!Files.exists(Path.of(storePath))) {
            try {
                Files.createDirectories(Path.of(storePath));
                logger.info("Create file dir");
            } catch (IOException e) {
                logger.error("Can't create file dir");
            }
        }
    }

    public Mono<NewFile> saveFile(Mono<FilePart> file) {
        FileEntity fileDB = new FileEntity();
        FileWriter fileWriter = new FileWriter(Paths.get(storePath, fileDB.getUuid()));
        ChunkProducer chunkProducer = chunkStoreService.getChunkProducer();

        var processedChunks = chunkStoreService.saveChunks(
                file.doOnNext(fp -> fileDB.setName(fp.filename()))
                        .flatMapMany(chunkProducer::getChunks)
        );

        return processedChunks
                .map(chunk -> {
                    fileWriter.write(chunk.getId());
                    return chunk.getCount() == 1 ? 1 : 0;
                })
                .reduce(new ChunkStatisitic(), (accumulator, isNew) -> {
                    if (isNew == 1) {
                        accumulator.addNewChunk();
                    } else {
                        accumulator.addChunk();
                    }
                    return accumulator;
                })
                .flatMap(stat -> fileRepository.save(fileDB)
                        .map(registeredFile -> new NewFile(registeredFile, stat.getProccentOfNew())));
    }

    public Mono<InputStream> getFile(String filename) {
        return createInputStream(Flux.using(
                () -> Files.lines(Paths.get(storePath, filename)),
                Flux::fromStream,
                Stream::close
                ).map(Long::parseLong)
                        .concatMap(r -> chunkStoreService.getChunkData(r))
        );
    }

    public Flux<FileEntity> getFileList() {
        return fileRepository.findAll();
    }

}
