package com.nicolasmesa.springboot.testcommon;

@FunctionalInterface
public interface CheckedRunnable {
    void run() throws Exception;
}
