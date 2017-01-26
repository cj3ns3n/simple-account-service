package org.jensens.service.account.storage;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Account {
    public long id;
    public String loginName;
    public String firstName;
    public String lastName;
    @JsonIgnore // Shouldn't publish the password salt
    public String hashSalt;
    @JsonIgnore  // Shouldn't publish the password hash
    public String passwordHash;
    //public List<String> roles = new ArrayList<String>();

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (! (otherObject instanceof Account)) {
            return false;
        }

        Account otherAccount = (Account)otherObject;
        if (this.id != otherAccount.id) {
            return false;
        }

        if ( ! this.loginName.equals(otherAccount.loginName)) {
            return false;
        }

        if ( ! this.firstName.equals(otherAccount.firstName)) {
            return false;
        }

        if ( ! this.lastName.equals(otherAccount.lastName)) {
            return false;
        }

        // password hash and salt are not in every account object, don't compare.
        return true;
    }
}
