package com.zk.arthas;

import java.util.Random;

public class ArthasTest {

    public static void main(String[] args) {
        new Thread(()->{
            while (true){
                ArthasTest a = new ArthasTest();
            }
        }).start();
    }
}
