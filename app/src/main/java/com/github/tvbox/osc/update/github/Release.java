package com.github.tvbox.osc.update.github;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;


public final class Release {
    @SerializedName("tag_name")
    public String tagName;

    public String body;
    public List<Asset> assets;

    @SerializedName("created_at")
    public Date createdAt;
}
