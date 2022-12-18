package com.example.mobiletranslator.db;

import androidx.annotation.NonNull;

import java.util.Objects;

public class LanguageItem {
    private final String name;
    private final String isoCode;
    private final boolean allowFormal;
    private final boolean downloaded;

    public LanguageItem(@NonNull String name, @NonNull String isoCode, boolean allowFormal, boolean downloaded) {
        this.name = name;
        this.isoCode = isoCode;
        this.allowFormal = allowFormal;
        this.downloaded = downloaded;
    }

    public LanguageItem(@NonNull String name, @NonNull String isoCode, String allowFormal, String downloaded) {
        this.name = name;
        this.isoCode = isoCode;
        this.allowFormal = allowFormal != null && (allowFormal.equals("true") || allowFormal.equals("1"));
        this.downloaded = downloaded != null && (downloaded.equals("true") || downloaded.equals("1"));
    }

    public String getName() {
        return name;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public boolean isAllowFormal() {
        return allowFormal;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public int isAllowFormalInt() {
        return allowFormal? 1: 0;
    }

    public int isDownloadedInt() {
        return downloaded? 1: 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LanguageItem that = (LanguageItem) o;
        return allowFormal == that.allowFormal && downloaded == that.downloaded && Objects.equals(name, that.name) && Objects.equals(isoCode, that.isoCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isoCode, allowFormal, downloaded);
    }
}
