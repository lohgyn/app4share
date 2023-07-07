package com.sunlifemalaysia.app4share.model;

import jakarta.persistence.Entity;

@Entity
public class IpaAppInfo extends PlistInfo {

    public IpaAppInfo() {
        super();
    }

    public IpaAppInfo(IpaFile ipaFile, String key, String value) {
        super(ipaFile, key, value);
    }
}
