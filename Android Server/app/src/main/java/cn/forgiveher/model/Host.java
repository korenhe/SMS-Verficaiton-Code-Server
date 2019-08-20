package cn.forgiveher.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class Host extends LitePalSupport {

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    @Column(unique = true, defaultValue = "unknown")
    private String name;
    private String ip;

    public void setName(String name) {
        this.name = name;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }
}
