package com.github.tvbox.osc.update;

import static com.xuexiang.xupdate.entity.UpdateError.ERROR.CHECK_NO_NEW_VERSION;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.proxy.impl.DefaultUpdateParser;


public class Update {
    public static final String TAG = "Update";

    public static void init(Application application) {
        XUpdate.get()
                .debug(true)
                //默认设置只在wifi下检查版本更新
                .isWifiOnly(false)
                //默认设置使用get请求检查版本
                .isGet(true)
                //默认设置非自动模式，可根据具体使用配置
                .isAutoMode(false)
                //设置默认公共请求参数
                .param(DefaultUpdateParser.APIKeyUpper.CODE, 0)
                .param(DefaultUpdateParser.APIKeyUpper.UPDATE_STATUS, 1)
                //设置版本更新出错的监听
                .setOnUpdateFailureListener(new OnUpdateFailureListener() {
                    @Override
                    public void onFailure(UpdateError error) {
                        error.printStackTrace();
                        //对不同错误进行处理
                        if (error.getCode() != CHECK_NO_NEW_VERSION) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(application.getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                })
                //设置是否支持静默安装，默认是true，需要 root 权限
                .supportSilentInstall(false)
                //这个必须设置！实现网络请求功能。
                .setIUpdateHttpService(new UpdateHttpService())
                //这个必须初始化
                .init(application);

        update(application);
    }

    public static void update(Application application) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                XUpdate.newBuild(application).updateUrl("https://api.github.com/repos/SturdyCat/TVBox/releases/latest").update();
            }
        }).start();
    }
}
