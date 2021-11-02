package com.sabd.fileserver.chunkstore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;

import com.sabd.fileserver.service.ChunkStoreService;
import com.sabd.fileserver.utils.ProcessorSubscriber;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class ChunkProducer {

    final Sinks.Many<byte[]> sink;
    byte[] residue;
    int chunkSize;
    Logger logger = LoggerFactory.getLogger(ChunkStoreService.class);

    public ChunkProducer(int chunkSize) {
        this.chunkSize = chunkSize;
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    public Flux<byte[]> getChunks(FilePart filePartFlux) {
        ProcessorSubscriber<byte[]> subscriber = new ProcessorSubscriber<byte[]>() {
            private Subscription s;

            @Override
            public void onSubscribe(Subscription s) {
                this.s = s;
                s.request(1);
            }

            @Override
            public void onNext(byte[] chunk) {
                var res = sink.tryEmitNext(chunk);
                if (res != Sinks.EmitResult.OK) {
                    logger.error("Emmit error {}", res);
                }
            }

            @Override
            public void onError(Throwable t) {
                sink.tryEmitError(t);
            }

            @Override
            public void onComplete() {
                if (residue != null) {
                    var res = sink.tryEmitNext(residue);
                    if (res != Sinks.EmitResult.OK) {
                        logger.error("Emmit error {}", res);
                    }
                }
                var res = sink.tryEmitComplete();
                if (res != Sinks.EmitResult.OK) {
                    logger.error("Complete error {}", res);
                }
            }

            public void request(long n) {
                s.request(n);
            }
        };
        produceChunks(filePartFlux).subscribe(subscriber);
        return sink.asFlux().doOnRequest(subscriber::request);
    }

    private Flux<byte[]> produceChunks(FilePart filePart) {
        return filePart.content().map(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);
            return makeChunkSplitting(bytes);
        }).flatMapIterable(Function.identity());
    }

    private List<byte[]> makeChunkSplitting(byte[] input) {
        List<byte[]> chunks = new ArrayList<>();
        int currentIndex = 0;
        while (input.length - currentIndex + ((residue == null) ? 0 : residue.length) >= chunkSize) {
            if (residue != null) {
                chunks.add(ArrayUtils.addAll(residue, ArrayUtils.subarray(input, currentIndex, currentIndex + chunkSize - residue.length)));
                currentIndex += chunkSize - residue.length;
                residue = null;
            } else {
                chunks.add(ArrayUtils.subarray(input, currentIndex, currentIndex + chunkSize));
                currentIndex += chunkSize;
            }
        }
        if (input.length - currentIndex > 0) {
            if (residue == null) {
                residue = ArrayUtils.subarray(input, currentIndex, input.length);
            } else {
                residue = ArrayUtils.addAll(residue, ArrayUtils.subarray(input, currentIndex, input.length));
            }
        }
        return chunks;
    }

}
