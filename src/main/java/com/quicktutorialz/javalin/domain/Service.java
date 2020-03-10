package com.quicktutorialz.javalin.domain;

public class Service {
    String code;
    String hostUrl;

    public Service() {
    }

    public Service(String code, String hostUrl) {
        this.code = code;
        this.hostUrl = hostUrl;
    }

    public String getCode() {
        return code;
    }

    public String getHostUrl() {
        return hostUrl;
    }
}
