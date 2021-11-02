package com.sabd.fileserver.chunkstore;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Function;

import com.sabd.fileserver.dto.ChunkWithPayload;

import reactor.core.publisher.Mono;

public class ChunkWriter {

    boolean isFirst = true;
    int offset = 0;
    Function<ChunkWithPayload, Mono<ChunkWithPayload>> positionRegistrar;
    Path filePath;

    public ChunkWriter(Path path, Function<ChunkWithPayload, Mono<ChunkWithPayload>> positionRegistrar) {
        this.filePath = path;
        this.positionRegistrar = positionRegistrar;
    }


    public ChunkWithPayload saveToChunkFile(ChunkWithPayload chunk) {
        if (chunk.getCount() == 1) {
            try {
                Files.write(filePath, chunk.getData(), isFirst ? StandardOpenOption.CREATE : StandardOpenOption.APPEND);
                if (isFirst) {
                    isFirst = false;
                }
                chunk.setPosition(offset);
                offset += chunk.getSize();
                positionRegistrar.apply(chunk).subscribe();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return chunk;
    }

}
