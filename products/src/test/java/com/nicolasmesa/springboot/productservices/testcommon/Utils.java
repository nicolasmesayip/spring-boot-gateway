package com.nicolasmesa.springboot.productservices.testcommon;

public class Utils {
    public static void withClue(String clue, CheckedRunnable testBlock) {
        try {
            testBlock.run();
        } catch (AssertionError e) {
            throw new AssertionError(clue + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(clue + e.getMessage(), e);
        }
    }
}
