package com.sabd.fileserver.chunkstore;

public interface HashCodeCalculator {

    String getStringHash(byte[] data);

}
