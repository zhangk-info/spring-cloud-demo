package com.zk.designpattern.observer;

import java.util.concurrent.Flow;

/**
 * 观察者模式
 * 定义对象之间的一对多依赖，让多个观察者对象同时监听某一个主题对象，当主题对象发生变化时，它的所有依赖者都会收到通知并更新
 */
public class Observer {

    public static void main(String[] args) throws InterruptedException {
        Publisher publisher = new Publisher();
        Subscriber subscriber = new Subscriber();
        publisher.subscribe(subscriber);


    }


}

/**
 * 发布者
 */
class Publisher implements Flow.Publisher<String> {


    public Publisher() {
    }

    @Override
    public void subscribe(Flow.Subscriber<? super String> subscriber) {
        new Thread(() -> {
            try {
                Subscription subscription = new Subscription();
                subscriber.onSubscribe(subscription);
                System.out.println("消息发送");
                subscriber.onNext("1");
                subscriber.onNext("2");
                subscriber.onNext("3");
                subscription.cancel();
                subscriber.onNext("4");

                subscriber.onComplete();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).start();
    }


}

class Subscription implements Flow.Subscription {

    private Boolean cancel = Boolean.FALSE;

    @Override
    public void request(long n) {
        if (cancel) {
            throw new RuntimeException("取消接收了");
        }
        System.out.println(" 接收到第" + n + "条消息 ");
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

        System.out.println("接收到消息 :" + item);
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