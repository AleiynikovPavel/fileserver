package com.sabd.fileserver.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.sabd.fileserver.model.FileEntity;

public interface FileRepository extends ReactiveCrudRepository<FileEntity, Long> {

}
