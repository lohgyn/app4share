package com.sunlifemalaysia.app4share.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class PlistInfo {

    public PlistInfo() {
        super();
    }

    public PlistInfo(IpaFile ipaFile, String key, String value) {
        this.ipaFile = ipaFile;
        this.key = key;
        this.value = value;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private IpaFile ipaFile;

    @Column(name = "info_key")
    private String key;

    @Column(name = "info_value_type")
    private String valueType;

    @Column(name = "info_value", columnDefinition = "TEXT")
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IpaFile getIpaFile() {
        return ipaFile;
    }

    public void setIpaFile(IpaFile ipaFile) {
        this.ipaFile = ipaFile;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
