package org.jensens.service.account.storage;

public class DataAccessException extends RuntimeException{
    public DataAccessException(Exception baseException) {
        super(baseException);
    }
}
