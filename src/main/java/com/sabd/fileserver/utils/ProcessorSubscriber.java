package com.sabd.fileserver.utils;

import org.reactivestreams.Subscriber;

public interface ProcessorSubscriber<T> extends Subscriber<T> {


    void request(long n);

}
