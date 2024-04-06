package com.github.tvbox.osc.update.github;

import com.google.gson.annotations.SerializedName;

public final class Asset {
    public String name;

    public int size;

    @SerializedName("browser_download_url")
    public String browserDownloadUrl;
}

