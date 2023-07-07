package com.sunlifemalaysia.app4share.model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class ApkFile extends AppFile {

    private String appPackage;
    private String versionCode;
    private String versionName;
    private String compileSdkVersion;
    private String compileSdkVersionCodename;
    private String platformBuildVersionCode;
    private String platformBuildVersionName;

    @Column(columnDefinition = "TEXT")
    private String permissions;

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getCompileSdkVersion() {
        return compileSdkVersion;
    }

    public void setCompileSdkVersion(String compileSdkVersion) {
        this.compileSdkVersion = compileSdkVersion;
    }

    public String getCompileSdkVersionCodename() {
        return compileSdkVersionCodename;
    }

    public void setCompileSdkVersionCodename(String compileSdkVersionCodename) {
        this.compileSdkVersionCodename = compileSdkVersionCodename;
    }

    public String getPlatformBuildVersionCode() {
        return platformBuildVersionCode;
    }

    public void setPlatformBuildVersionCode(String platformBuildVersionCode) {
        this.platformBuildVersionCode = platformBuildVersionCode;
    }

    public String getPlatformBuildVersionName() {
        return platformBuildVersionName;
    }

    public void setPlatformBuildVersionName(String platformBuildVersionName) {
        this.platformBuildVersionName = platformBuildVersionName;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

}
