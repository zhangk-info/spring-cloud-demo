package com.zk;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

interface MyMessageListener<T> {

    public void onMessage(List<String> messages);
}

/**
 * https://projectreactor.io/docs/core/release/reference/
 */
public class FluxTest {

    public static void main(String[] args) {

        // Flux.generate 同步阻塞
        Flux<String> flux = Flux.generate(
                () -> 0,
                (state, sink) -> {
                    sink.next("3 x " + state + " = " + 3 * state);
                    if (state == 10) sink.complete();
                    return state + 1;
                }, (state) -> System.out.println("state: " + state));

        flux.subscribe(new BaseSubscriber<String>() {
            @Override
            protected void hookOnNext(String value) {
                System.out.println("value = [" + value + "]");
                super.hookOnNext(value);
            }
        });

        MyMessageProcessor myMessageProcessor = new MyMessageProcessor();

        Flux<String> bridge = Flux.create(sink -> {
            myMessageProcessor.register(
                    new MyMessageListener<String>() {

                        public void onMessage(List<String> messages) {
                            for (String s : messages) {
                                sink.next(s);
                            }
                        }
                    });
            sink.onRequest(n -> {
                List<String> messages = myMessageProcessor.getHistory(n);
                for (String s : messages) {
                    sink.next(s);
                }
            });
        });
        bridge.subscribe(new BaseSubscriber<String>() {
            @Override
            protected void hookOnNext(String value) {
                System.out.println("value = [" + value + "]");
                super.hookOnNext(value);
            }
        });

        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            new Thread(() -> {
                myMessageProcessor.addMsg(finalI + "");
            }).run();
        }


        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

}

class MyMessageProcessor {

    MyMessageListener<String> myMessageListener;
    List<String> strs = new ArrayList<>();

    public void register(MyMessageListener<String> myMessageListener) {
        this.myMessageListener = myMessageListener;
    }

    public List<String> getHistory(long n) {
        return strs;
    }

    public void addMsg(String s) {
        strs.add(s);
        myMessageListener.onMessage(strs);
        strs.clear();
    }
}
