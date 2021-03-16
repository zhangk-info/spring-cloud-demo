package com.zk.designpattern.observer;

import java.util.concurrent.Flow;

/**
 * 观察者模式
 * 定义对象之间的一对多依赖，让多个观察者对象同时监听某一个主题对象，当主题对象发生变化时，它的所有依赖者都会收到通知并更新
 */
public class Observer {

    public static void main(String[] args) throws InterruptedException {
        // 建立发布者
        Publisher publisher = new Publisher();
        // 建立订阅者
        Subscriber subscriber = new Subscriber();

        // 订阅者绑定背压
        Subscription subscription = new Subscription();
        subscriber.onSubscribe(subscription);

        // 发布者绑定订阅者
        publisher.subscribe(subscriber);


        // 发送数据
        for (int i = 0; i < 100; i++) {
            publisher.submit(i + "- - ");
        }

        // 发送完成关闭
        publisher.close();


    }


}

/**
 * 发布者
 */
class Publisher implements Flow.Publisher<String> {

    Flow.Subscriber subscriber;

    public Publisher() {
    }

    @Override
    public void subscribe(Flow.Subscriber<? super String> subscriber) {
        this.subscriber = subscriber;
    }

    public void submit(String s) {
        // 消息发送不阻塞
        try {
            subscriber.onNext(s);
        } catch (Exception e) {
            subscriber.onError(e);
        }
    }

    public void close() {
        subscriber.onComplete();
    }


}

/**
 * 回压的核心实现
 */
class Subscription implements Flow.Subscription {

    private volatile Boolean cancel = Boolean.FALSE;

    @Override
    public void request(long n) {
        if (cancel) {
            throw new RuntimeException("取消接收了" + n);
        }
//        System.out.println(" 接收到第" + n + "条消息 ");
    }

    @Override
    public void cancel() {
        System.out.println(" 接收到停止接收消息命令 ");
        this.cancel = true;
    }
}

/**
 * 订阅者
 */
class Subscriber implements Flow.Subscriber<String> {

    private Flow.Subscription subscription;

    private Long count = 0L;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        System.out.println("定义消息消费者");
    }

    @Override
    public void onNext(String item) {
        count++;
        subscription.request(count);
        System.out.println("接收到消息 - 处理消息 :" + item);

    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("onError:" + throwable);
    }

    @Override
    public void onComplete() {
        System.out.println("onComplete...");
    }
}