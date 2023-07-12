package com.sunlifemalaysia.app4share.model.auth;

import java.io.Serializable;
import java.util.List;

import com.sunlifemalaysia.app4share.model.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

@Entity
public class Role extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<UserAccount> userAccounts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserAccount> getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(List<UserAccount> userAccounts) {
        this.userAccounts = userAccounts;
    }
}
