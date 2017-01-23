package org.jensens.service.account.storage;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Account {
    public long id;
    public String loginName;
    public String firstName;
    public String lastName;
    @JsonIgnore
    public String passwordHash;
    //public List<String> roles = new ArrayList<String>();
}
