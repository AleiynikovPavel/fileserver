package com.sabd.fileserver.chunkstore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

@Service
public class HashCodeCalculatorImpl implements HashCodeCalculator {

    public HashCodeCalculatorImpl() {
        Security.addProvider(new BouncyCastleProvider());
    }

    public byte[] getHash(byte[] data) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return messageDigest.digest(data);
    }

    public String getStringHash(byte[] data) {
        return Hex.encodeHexString(getHash(data));
    }

}
