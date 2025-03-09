package com.smartcommunity.smart_community_platform.exception;

public class OptimisticLockException extends RuntimeException {
    public OptimisticLockException(String s) {
        super(s);
    }
}
