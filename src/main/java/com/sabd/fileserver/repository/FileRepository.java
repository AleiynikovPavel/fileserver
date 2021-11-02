package com.sabd.fileserver.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.sabd.fileserver.dto.FileEntity;

public interface FileRepository extends ReactiveCrudRepository<FileEntity, Long> {

}
