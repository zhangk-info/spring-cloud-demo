package com.zk.auth.captch.services;


import com.zk.auth.captch.exception.ImageCodeException;

public interface ImageCodeStore {

    void set(String signature, String code);

    void validated(String signature, String code) throws ImageCodeException;

    boolean hasSignature(String signature);

    void remove(String signature);
}
