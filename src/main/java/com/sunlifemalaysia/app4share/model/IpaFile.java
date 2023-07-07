package com.sunlifemalaysia.app4share.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.OneToMany;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class IpaFile extends AppFile {

    // Info.plist
    private String appDisplayName;
    private String appVersion;
    private String appBuild;
    private String bundleId;
    private String bundleExecutable;
    private String capabilities;
    private String supportedPlatforms;
    private String minimumVersionRequired;
    private String deviceFamily;

    // embedded.mobileprovision
    private String provisionAppIdName;
    private String provisionPlatform;
    private String provisionName;
    private String provisionTeamName;
    private String provisionUuid;
    private Date provisionCreationDate;
    private Date provisionExpirationDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ipaFile")
    private List<IpaAppInfo> ipaAppInfos;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ipaFile")
    private List<IpaProvisionInfo> ipaProvisionInfos;

    public String getAppDisplayName() {
        return appDisplayName;
    }

    public void setAppDisplayName(String appDisplayName) {
        this.appDisplayName = appDisplayName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppBuild() {
        return appBuild;
    }

    public void setAppBuild(String appBuild) {
        this.appBuild = appBuild;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String getBundleExecutable() {
        return bundleExecutable;
    }

    public void setBundleExecutable(String bundleExecutable) {
        this.bundleExecutable = bundleExecutable;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public String getSupportedPlatforms() {
        return supportedPlatforms;
    }

    public void setSupportedPlatforms(String supportedPlatforms) {
        this.supportedPlatforms = supportedPlatforms;
    }

    public String getMinimumVersionRequired() {
        return minimumVersionRequired;
    }

    public void setMinimumVersionRequired(String minimumVersionRequired) {
        this.minimumVersionRequired = minimumVersionRequired;
    }

    public String getDeviceFamily() {
        return deviceFamily;
    }

    public void setDeviceFamily(String deviceFamily) {
        this.deviceFamily = deviceFamily;
    }

    public String getProvisionAppIdName() {
        return provisionAppIdName;
    }

    public void setProvisionAppIdName(String provisionAppIdName) {
        this.provisionAppIdName = provisionAppIdName;
    }

    public String getProvisionPlatform() {
        return provisionPlatform;
    }

    public void setProvisionPlatform(String provisionPlatform) {
        this.provisionPlatform = provisionPlatform;
    }

    public String getProvisionName() {
        return provisionName;
    }

    public void setProvisionName(String provisionName) {
        this.provisionName = provisionName;
    }

    public String getProvisionTeamName() {
        return provisionTeamName;
    }

    public void setProvisionTeamName(String provisionTeamName) {
        this.provisionTeamName = provisionTeamName;
    }

    public String getProvisionUuid() {
        return provisionUuid;
    }

    public void setProvisionUuid(String provisionUuid) {
        this.provisionUuid = provisionUuid;
    }

    public Date getProvisionCreationDate() {
        return provisionCreationDate;
    }

    public void setProvisionCreationDate(Date provisionCreationDate) {
        this.provisionCreationDate = provisionCreationDate;
    }

    public Date getProvisionExpirationDate() {
        return provisionExpirationDate;
    }

    public void setProvisionExpirationDate(Date provisionExpirationDate) {
        this.provisionExpirationDate = provisionExpirationDate;
    }

    public List<IpaAppInfo> getIpaAppInfos() {
        return ipaAppInfos;
    }

    public void setIpaAppInfos(List<IpaAppInfo> ipaAppInfos) {
        this.ipaAppInfos = ipaAppInfos;
    }

    public List<IpaProvisionInfo> getIpaProvisionInfos() {
        return ipaProvisionInfos;
    }

    public void setIpaProvisionInfos(List<IpaProvisionInfo> ipaProvisionInfos) {
        this.ipaProvisionInfos = ipaProvisionInfos;
    }
}
