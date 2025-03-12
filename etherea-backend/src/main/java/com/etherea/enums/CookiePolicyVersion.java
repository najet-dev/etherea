package com.etherea.enums;
public enum CookiePolicyVersion {
    V1_0("1.0");
    private final String version;
    CookiePolicyVersion(String version) {
        this.version = version;
    }
    public String getVersion() {
        return version;
    }
    public static CookiePolicyVersion fromString(String version) {
        for (CookiePolicyVersion v : CookiePolicyVersion.values()) {
            if (v.version.equals(version)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Version inconnue: " + version);
    }
}