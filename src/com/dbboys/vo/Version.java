package com.dbboys.vo;

import org.json.JSONObject;

public class Version {
    private String version;
    private int build;
    private String url;
    private String changelog;
    public Version(JSONObject json) {
        version = json.getString("version");
        build = json.getInt("build");
        url = json.getString("url");
        changelog = json.getString("changelog");
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getBuild() {
        return build;
    }

    public void setBuild(int build) {
        this.build = build;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }
}
