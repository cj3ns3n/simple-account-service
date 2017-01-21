package org.jensens.auth.storage;

public interface Accessor {
    Account getAccount(long accountId);
}
