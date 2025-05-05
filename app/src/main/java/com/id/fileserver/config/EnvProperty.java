package com.id.fileserver.config;

import java.util.Properties;
import lombok.Getter;

@Getter
public enum EnvProperty {
    OS_NAME("os.name"),
    OS_VERSION("os.version"),
    OS_ARCH("os.arch"),
    USER_NAME("user.name"),
    USER_HOME("user.home"),
    USER_DIRECTORY("user.dir"),
    JAVA_HOME("java.home"),
    JAVA_VERSION("java.version"),
    JAVA_RUNTIME_VERSION("java.runtime.version"),
    JAVA_VENDOR("java.vendor");

    private static final String NOT_AVAILABLE = "N/A";
    private static final Properties PROPS = System.getProperties();

    private final String key;

    EnvProperty(String key) {
        this.key = key;
    }

    public static String getEnvProperties() {
        StringBuilder sb = new StringBuilder();
        for (EnvProperty property : EnvProperty.values()) {
            sb.append(System.lineSeparator());
            String value = PROPS.getProperty(property.getKey(), NOT_AVAILABLE);
            sb.append("  ").append(property.name()).append("=").append(value);
        }
        return sb.toString();
    }

}