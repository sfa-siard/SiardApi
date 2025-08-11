package ch.admin.bar.siard2.api;

import java.util.Properties;

public class ConfigurationProperties extends Properties {
    private static final long serialVersionUID = 5204423170460249028L;

    public String getLobsFolder() {
        return "./build/tmp/test-lobs";
    }
}
