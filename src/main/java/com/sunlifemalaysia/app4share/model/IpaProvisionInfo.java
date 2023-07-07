package com.sunlifemalaysia.app4share.model;

import jakarta.persistence.Entity;

@Entity
public class IpaProvisionInfo extends PlistInfo {

    public IpaProvisionInfo() {
        super();
    }

    public IpaProvisionInfo(IpaFile ipaFile, String key, String value) {
        super(ipaFile, key, value);
    }
}
