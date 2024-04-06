package com.github.tvbox.osc.update;

import static java.lang.Integer.parseInt;

import android.os.Build;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.update.github.Asset;
import com.github.tvbox.osc.update.github.Release;
import com.github.tvbox.osc.util.urlhttp.CallBackUtil;
import com.github.tvbox.osc.util.urlhttp.UrlHttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lzy.okgo.OkGo;
import com.xuexiang.xupdate.proxy.IUpdateHttpService;
import com.xuexiang.xupdate.proxy.impl.DefaultUpdateParser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * 使用okhttp
 */
public class UpdateHttpService implements IUpdateHttpService {
    public static final String TAG = "Update";

    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, Object> params, final @NonNull Callback callBack) {
        try {
            String json = OkGo.<String>get(url).execute().body().string();
            Release release = new Gson().fromJson(json, Release.class);
            String cpuABI = Build.CPU_ABI;
            JsonObject updateJson = new Gson().fromJson(params.toString(), JsonObject.class);
            for (Asset item : release.assets) {
                if (item.name.contains(cpuABI)) {
                    if (item.name.contains("md5")) {
                        String md5 = OkGo.<String>get(item.browserDownloadUrl).execute().body().string().toUpperCase().trim();
                        updateJson.addProperty(DefaultUpdateParser.APIKeyUpper.APK_MD5, md5);
                    } else {
                        updateJson.addProperty(DefaultUpdateParser.APIKeyUpper.DOWNLOAD_URL, item.browserDownloadUrl);
                        updateJson.addProperty(DefaultUpdateParser.APIKeyUpper.APK_SIZE, Math.ceil(item.size / 1024));
                    }
                }
            }
            updateJson.addProperty(DefaultUpdateParser.APIKeyUpper.MODIFY_CONTENT, Pattern.compile("^.*(?=Changelog)", Pattern.DOTALL).matcher(release.body).replaceAll(""));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            int versionCode = parseInt(sdf.format(release.createdAt));
            updateJson.addProperty(DefaultUpdateParser.APIKeyUpper.VERSION_CODE, versionCode);
            updateJson.addProperty(DefaultUpdateParser.APIKeyUpper.VERSION_NAME, versionCode);
            callBack.onSuccess(updateJson.toString());
        } catch (IOException e) {
            callBack.onError(e);
        }
    }

    @Override
    public void asyncPost(@NonNull String url, @NonNull Map<String, Object> params, final @NonNull Callback callBack) {
//        TODO("Not yet implemented");
    }

    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, final @NonNull DownloadCallback callback) {
        UrlHttpUtil.downloadFile(url, new CallBackUtil.CallBackFile(path, fileName) {
            @Override
            public void onProgress(float progress, long total) {
                callback.onProgress(progress, total);
            }


            @Override
            public void onFailure(int code, String errorMessage, Exception e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(File response) {
                callback.onSuccess(response);
            }
        });
    }

    @Override
    public void cancelDownload(@NonNull String url) {
//        TODO("Not yet implemented");
    }
}