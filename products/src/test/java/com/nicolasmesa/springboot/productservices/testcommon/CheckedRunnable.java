package com.nicolasmesa.springboot.productservices.testcommon;

@FunctionalInterface
public interface CheckedRunnable {
    void run() throws Exception;
}
