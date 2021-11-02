package com.sabd.fileserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("chunks")
public class ChunkEntity {

    @Id
    private long id;
    private String hash;
    private String path;
    private Integer size;
    private Integer position;
    private Integer count;

    public ChunkEntity(Long id, String hash, String path, Integer size, Integer position, Integer count) {
        this.id = id;
        this.hash = hash;
        this.path = path;
        this.size = size;
        this.position = position;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ChunkEntity withId(Long id) {
        return new ChunkEntity(id, hash, path, size, position, count);
    }

}
