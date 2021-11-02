package com.sabd.fileserver.dto;

public class ChunkStatisitic {

    private int allChunkCount;
    private int newChunkCount;

    public float getProccentOfNew() {
        return newChunkCount / (float) allChunkCount;
    }

    public int getNewChunkCount() {
        return newChunkCount;
    }

    public void addNewChunk() {
        newChunkCount++;
        allChunkCount++;
    }

    public void addChunk() {
        allChunkCount++;
    }


}
