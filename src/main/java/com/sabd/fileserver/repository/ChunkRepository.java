package com.sabd.fileserver.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.sabd.fileserver.model.ChunkEntity;

import reactor.core.publisher.Mono;

public interface ChunkRepository extends ReactiveCrudRepository<ChunkEntity, Long> {


    @Query("SELECT * FROM find_chunk_or_insert_new(:hash, :path, :size, :position);")
    Mono<ChunkEntity> findChunkOrInsert(String hash, String path, Integer size, Integer position);

    @Query("UPDATE chunks SET position=:offset WHERE id=:id")
    Mono<Integer> setChunkPosition(Long id, int offset);

}
