package com.sabd.fileserver.dto;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import com.sabd.fileserver.model.FileEntity;

public class NewFile {

    @Id
    private long id;
    private String name;
    private String uuid;
    private LocalDateTime createAt;
    private float newChunksPercent;

    public NewFile(long id, String name, String uuid, LocalDateTime createAt, float newChunksPercent) {
        this.id = id;
        this.name = name;
        this.uuid = uuid;
        this.createAt = createAt;
        this.newChunksPercent = newChunksPercent;
    }

    public NewFile(FileEntity file, float newChunksPercent) {
        this.id = file.getId();
        this.name = file.getName();
        this.uuid = file.getUuid();
        this.createAt = file.getCreateAt();
        this.newChunksPercent = newChunksPercent;
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

    public float getNewChunksPercent() {
        return newChunksPercent;
    }

    public void setNewChunksPercent(float newChunksPercent) {
        this.newChunksPercent = newChunksPercent;
    }


}
