package com.sabd.fileserver.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("files")
public class FileEntity {

    @Id
    private long id;
    private String name;
    private String uuid;
    private LocalDateTime createAt;

    public FileEntity(long id, String name, String uuid, LocalDateTime createAt) {
        this.id = id;
        this.name = name;
        this.uuid = uuid;
        this.createAt = createAt;
    }

    public FileEntity() {
        this.uuid = UUID.randomUUID().toString();
        this.createAt = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

}
