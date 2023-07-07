package com.sunlifemalaysia.app4share.model.xml.androidmanifest;

import java.util.List;

public class Manifest {
    private String attrAndroidVersionCode;
    private String attrAndroidVersionName;
    private String attrAndroidCompileSdkVersion;
    private String attrAndroidCompileSdkVersionCodeName;
    private String attrPackage;
    private String attrPlatformBuildVersionCode;
    private String attrPlatformBuildVersionName;

    private Application application;
    private List<UsesPermission> usesPermissions;

    public String getAttrAndroidVersionCode() {
        return attrAndroidVersionCode;
    }

    public void setAttrAndroidVersionCode(String attrAndroidVersionCode) {
        this.attrAndroidVersionCode = attrAndroidVersionCode;
    }

    public String getAttrAndroidVersionName() {
        return attrAndroidVersionName;
    }

    public void setAttrAndroidVersionName(String attrAndroidVersionName) {
        this.attrAndroidVersionName = attrAndroidVersionName;
    }

    public String getAttrAndroidCompileSdkVersion() {
        return attrAndroidCompileSdkVersion;
    }

    public void setAttrAndroidCompileSdkVersion(String attrAndroidCompileSdkVersion) {
        this.attrAndroidCompileSdkVersion = attrAndroidCompileSdkVersion;
    }

    public String getAttrAndroidCompileSdkVersionCodeName() {
        return attrAndroidCompileSdkVersionCodeName;
    }

    public void setAttrAndroidCompileSdkVersionCodeName(String attrAndroidCompileSdkVersionCodeName) {
        this.attrAndroidCompileSdkVersionCodeName = attrAndroidCompileSdkVersionCodeName;
    }

    public String getAttrPackage() {
        return attrPackage;
    }

    public void setAttrPackage(String attrPackage) {
        this.attrPackage = attrPackage;
    }

    public String getAttrPlatformBuildVersionCode() {
        return attrPlatformBuildVersionCode;
    }

    public void setAttrPlatformBuildVersionCode(String attrPlatformBuildVersionCode) {
        this.attrPlatformBuildVersionCode = attrPlatformBuildVersionCode;
    }

    public String getAttrPlatformBuildVersionName() {
        return attrPlatformBuildVersionName;
    }

    public void setAttrPlatformBuildVersionName(String attrPlatformBuildVersionName) {
        this.attrPlatformBuildVersionName = attrPlatformBuildVersionName;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public List<UsesPermission> getUsesPermissions() {
        return usesPermissions;
    }

    public void setUsesPermissions(List<UsesPermission> usesPermissions) {
        this.usesPermissions = usesPermissions;
    }

}
