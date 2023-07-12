package com.sunlifemalaysia.app4share.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

@MappedSuperclass
public class AppFile extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String fileUuid;

    private String appName;

    private String iconName;

    @Column(columnDefinition = "TEXT")
    private String base64Icon;
    @Column(columnDefinition = "TEXT")
    private String base64ReleaseNotes;

    private Long fileSize;

    @Transient
    private String downloadUri;

    public String getFileUuid() {
        return fileUuid;
    }

    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getBase64Icon() {
        return base64Icon;
    }

    public void setBase64Icon(String base64Icon) {
        this.base64Icon = base64Icon;
    }

    public String getBase64ReleaseNotes() {
        return base64ReleaseNotes;
    }

    public void setBase64ReleaseNotes(String base64ReleaseNotes) {
        this.base64ReleaseNotes = base64ReleaseNotes;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }

}
