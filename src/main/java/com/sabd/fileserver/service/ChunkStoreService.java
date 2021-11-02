package com.sabd.fileserver.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sabd.fileserver.chunkstore.ChunkProducer;
import com.sabd.fileserver.chunkstore.ChunkWriter;
import com.sabd.fileserver.chunkstore.HashCodeCalculator;
import com.sabd.fileserver.dto.ChunkWithPayload;
import com.sabd.fileserver.repository.ChunkRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class ChunkStoreService {

    Logger logger = LoggerFactory.getLogger(ChunkStoreService.class);
    @Autowired
    HashCodeCalculator hashCodeCalculator;
    @Autowired
    private ChunkRepository chunkRepository;
    @Value("${store.chunk.path}")
    private String storePath;
    @Value("${chunk.size}")
    private int chunkSize;

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

    public Mono<byte[]> getChunkData(long reference) {
        return chunkRepository.findById(reference).map(chunkDB -> {
            if (chunkDB.getPosition() == -1) {
                throw new RuntimeException("Chunk not ready");
            }
            byte[] result = null;
            RandomAccessFile aFile = null;
            try {
                aFile = new RandomAccessFile(chunkDB.getPath(), "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ByteBuffer buffer = ByteBuffer.allocate(chunkDB.getSize());
            try {
                aFile.getChannel().read(buffer, chunkDB.getPosition());
                result = buffer.array();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        });
    }

    public Flux<ChunkWithPayload> saveChunks(Flux<byte[]> chunks) {
        Path chunksFile = Paths.get(storePath, UUID.randomUUID().toString());
        ChunkWriter chunkWriter = new ChunkWriter(chunksFile, chunk -> chunkRepository.setChunkPosition(chunk.getId(), chunk.getPosition())
                .map(result -> chunk));

        return chunks.map(chunkData -> {
            var chunkRegistration = chunkRepository
                    .findChunkOrInsert(hashCodeCalculator.getStringHash(chunkData), chunksFile.toString(), chunkData.length, -1);
            return chunkRegistration.map(chunk -> new ChunkWithPayload(chunk, chunkData));
        }).concatMap(Function.identity())
                .map(chunkWriter::saveToChunkFile);
    }

    public ChunkProducer getChunkProducer() {
        return new ChunkProducer(chunkSize);
    }


}
