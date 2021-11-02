package com.sabd.fileserver.dto;

import com.sabd.fileserver.model.ChunkEntity;

public class ChunkWithPayload {

    private final String hash;
    private final String path;
    private final int size;
    private int position;
    private final int count;
    private long id;
    private final byte[] data;

    public ChunkWithPayload(String hash, String path, int size, int position, int count, byte[] data) {
        this.hash = hash;
        this.path = path;
        this.size = size;
        this.position = position;
        this.count = count;
        this.data = data;
    }

    public ChunkWithPayload(ChunkEntity chunk, byte[] data) {
        this.hash = chunk.getHash();
        this.path = chunk.getPath();
        this.size = chunk.getSize();
        this.position = chunk.getPosition();
        this.id = chunk.getId();
        this.count = chunk.getCount();
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public String getHash() {
        return hash;
    }

    public String getPath() {
        return path;
    }

    public int getSize() {
        return size;
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

    public long getId() {
        return id;
    }


}
