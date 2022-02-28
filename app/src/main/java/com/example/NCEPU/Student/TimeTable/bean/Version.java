package com.example.NCEPU.Student.TimeTable.bean;

/**
 * 用于app更新
 */
public class Version {
    private long versionCode;//版本号
    private String releaseName;//文件名

    public long getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(long versionCode) {
        this.versionCode = versionCode;
    }

    public String getReleaseName() {
        return releaseName;
    }

    public void setReleaseName(String releaseName) {
        this.releaseName = releaseName;
    }
}
