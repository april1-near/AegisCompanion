package com.aegis.companion.exception;

public class OptimisticLockException extends RuntimeException {
    public OptimisticLockException(String s) {
        super(s);
    }
}
