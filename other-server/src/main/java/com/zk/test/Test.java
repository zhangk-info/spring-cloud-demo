package com.zk.test;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        Test t = new Test();
        t.test((String[])list.toArray(new String[0]));
    }

    public String test(String... t){
        return t.toString();
    }

}
