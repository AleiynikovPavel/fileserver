package com.sabd.fileserver.utils;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Objects;

import org.reactivestreams.Subscription;

import reactor.core.publisher.BaseSubscriber;

public class PipedStreamSubscriber extends BaseSubscriber<byte[]> {

    //private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PipedInputStream in;
    private PipedOutputStream out;

    public PipedStreamSubscriber(PipedInputStream in) {
        Objects.requireNonNull(in, "The input stream must not be null");
        this.in = in;
    }

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        //change if you want to control back-pressure
        super.hookOnSubscribe(subscription);
        try {
            this.out = new PipedOutputStream(in);
        } catch (IOException e) {
            //TODO throw a contextual exception here
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void hookOnNext(byte[] payload) {
        try {
            out.write(payload);
        } catch (IOException e) {
            //TODO throw a contextual exception here
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void hookOnComplete() {
        close();
    }

    @Override
    protected void hookOnError(Throwable error) {
        //TODO handle the error or at least log it
        close();
        //throw new RuntimeException(error);
        //logger.error("Failure processing stream", error);
    }

    @Override
    protected void hookOnCancel() {
        close();
    }

    private void close() {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            //probably just ignore this one or simply  log it
        }
    }

}