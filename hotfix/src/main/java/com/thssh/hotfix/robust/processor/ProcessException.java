package com.thssh.hotfix.robust.processor;

public class ProcessException extends Exception {
    public ProcessException() {
        super("process patch error.");
    }

    public ProcessException(String message) {
        super(message);
    }
}
