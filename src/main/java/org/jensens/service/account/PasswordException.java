package org.jensens.service.account;

public class PasswordException extends RuntimeException {
    public PasswordException(Exception ex) {
        super(ex);
    }
}
