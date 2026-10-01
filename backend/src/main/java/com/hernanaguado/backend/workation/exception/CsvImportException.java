package com.hernanaguado.backend.workation.exception;

public class CsvImportException extends RuntimeException {

    public CsvImportException(String message, Throwable cause) {
        super(message, cause);
    }
}