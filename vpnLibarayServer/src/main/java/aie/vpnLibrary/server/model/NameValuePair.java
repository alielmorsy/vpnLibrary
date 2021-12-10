package aie.vpnLibrary.server.model;

public class NameValuePair {

    private String key;
    private String value;

    public NameValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public NameValuePair setKey(String key) {
        this.key = key;
        return this;
    }

    public String getValue() {
        return value;
    }

    public NameValuePair setValue(String value) {
        this.value = value;
        return this;
    }
}
