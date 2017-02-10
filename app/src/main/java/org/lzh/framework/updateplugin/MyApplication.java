package org.lzh.framework.updateplugin;

import android.app.Application;
import android.widget.Toast;

import org.lzh.framework.updatepluginlib.UpdateConfig;
import org.lzh.framework.updatepluginlib.callback.UpdateCheckCB;
import org.lzh.framework.updatepluginlib.callback.UpdateDownloadCB;
import org.lzh.framework.updatepluginlib.model.Update;
import org.lzh.framework.updatepluginlib.model.UpdateParser;

import java.io.File;

/**
 * @author Administrator
 */
public class MyApplication extends Application {
    private String apkFile = "http://m.shouji.360tpcdn.com/151023/2acc9ecca7ca75e925b9131f964f169f/com.baidu.tieba_mini_101253632.apk";

    @Override
    public void onCreate() {
        super.onCreate();
        // UpdateConfig为全局配置。当在其他页面中。使用UpdateBuilder进行检查更新时。
        // 对于没传的参数，会默认使用UpdateConfig中的全局配置
        UpdateConfig.getConfig()
                // 必填：初始化一个Application框架内使用
                .init(this)
                // 必填：数据更新接口,url与checkEntity两种方式任选一种填写
                .url("https://www.baidu.com")
//                .checkEntity(new CheckEntity().setMethod(HttpMethod.GET).setUrl("http://www.baidu.com"))
                // 必填：用于从数据更新接口获取的数据response中。解析出Update实例。以便框架内部处理
                .jsonParser(new UpdateParser() {
                    @Override
                    public Update parse(String response) {
                        /* 此处根据上面url或者checkEntity设置的检查更新接口的返回数据response解析出
                         * 一个update对象返回即可。更新启动时框架内部即可根据update对象的数据进行处理
                         */
                        // 此处模拟一个Update对象
                        Update update = new Update(response);
                        // 此apk包的更新时间
                        update.setUpdateTime(System.currentTimeMillis());
                        // 此apk包的下载地址
                        update.setUpdateUrl(apkFile);
                        // 此apk包的版本号
                        update.setVersionCode(2);
                        // 此apk包的版本名称
                        update.setVersionName("2.0");
                        // 此apk包的更新内容
                        update.setUpdateContent("测试更新");
                        // 此apk包是否为强制更新
                        update.setForced(false);
                        // 是否显示忽略此次版本更新按钮
                        update.setIgnore(true);
                        return update;
                    }
                })
                // TODO: 2016/5/11 除了以上两个参数为必填。以下的参数均为非必填项。
                .checkCB(new UpdateCheckCB() {

                    @Override
                    public void onCheckError(int code, String errorMsg) {
                        Toast.makeText(MyApplication.this, "更新失败：code:" + code + ",errorMsg:" + errorMsg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUserCancel() {
                        Toast.makeText(MyApplication.this, "用户取消更新", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCheckIgnore(Update update) {
                        Toast.makeText(MyApplication.this, "用户忽略此版本更新", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void hasUpdate(Update update) {
                        Toast.makeText(MyApplication.this, "检查到有更新", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void noUpdate() {
                        Toast.makeText(MyApplication.this, "无更新", Toast.LENGTH_SHORT).show();
                    }
                })
                // apk下载的回调
                .downloadCB(new UpdateDownloadCB(){
                    @Override
                    public void onUpdateStart() {
                        Toast.makeText(MyApplication.this, "下载开始", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUpdateComplete(File file) {
                        Toast.makeText(MyApplication.this, "下载完成", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUpdateProgress(long current, long total) {
                    }

                    @Override
                    public void onUpdateError(int code, String errorMsg) {
                        Toast.makeText(MyApplication.this, "下载失败：code:" + code + ",errorMsg:" + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                })
                // 自定义更新检查器。
//                .updateChecker(new UpdateChecker() {
//                    @Override
//                    public boolean check(Update update) {
                          // 此处根据上面jsonParser解析出的update对象来判断是否此update代表的
                          // 版本应该被更新。返回true为需要更新。返回false代表不需要更新
//                        return false;
//                    }
//                })
                /* // 自定义更新接口的访问任务
                .checkWorker(new UpdateWorker() {
                    @Override
                    protected String check(String url) throws Exception {
                        // TODO: 2016/5/11 此处运行于子线程。在此进行更新接口访问
                        return null;
                    }
                })
                // 自定义apk下载任务
                .downloadWorker(new DownloadWorker() {
                    @Override
                    protected void download(String url, File file) throws Exception {
                        // TODO: 2016/5/11 此处运行于子线程，在此进行文件下载任务
                    }
                })
                // 自定义下载文件缓存,默认下载至系统自带的缓存目录下
                .fileCreator(new ApkFileCreator() {
                    @Override
                    public File create(String versionName) {
                        // TODO: 2016/5/11 versionName 为解析的Update实例中的update_url数据。在些可自定义下载文件缓存路径及文件名。放置于File中
                        return null;
                    }
                })
                // 自定义更新策略，默认WIFI下自动下载更新
                .strategy(new UpdateStrategy() {
                    @Override
                    public boolean isShowUpdateDialog(Update update) {
                        // 是否在检查到有新版本更新时展示Dialog。
                        return false;
                    }

                    @Override
                    public boolean isAutoInstall() {
                        // 是否自动更新。此属性与是否isShowInstallDialog互斥
                        return false;
                    }

                    @Override
                    public boolean isShowInstallDialog() {
                        // 下载完成后。是否显示提示安装的Dialog
                        return false;
                    }

                    @Override
                    public boolean isShowDownloadDialog() {
                        // 在APK下载时。是否显示下载进度的Dialog
                        return false;
                    }
                })
                        // 自定义检查出更新后显示的Dialog，
                .updateDialogCreator(new DialogCreator() {
                    @Override
                    public Dialog create(Update update, Activity activity, UpdateBuilder updateBuilder) {
                        // TODO: 2016/5/11 此处为检查出有新版本需要更新时的回调。运行于主线程，在此进行更新Dialog的创建
                        return null;
                    }
                })
                        // 自定义下载时的进度条Dialog
                .downloadDialogCreator(new DownloadCreator() {
                    @Override
                    public UpdateDownloadCB create(Update update, Activity activity) {
                        // TODO: 2016/5/11 此处为正在下载APK时的回调。运行于主线程。在此进行Dialog自定义与显示操作。
                        // TODO: 2016/5/11 需要在此创建并返回一个UpdateDownloadCB回调。用于对Dialog进行更新。
                        return null;
                    }
                })
                        // 自定义下载完成后。显示的Dialog
                .installDialogCreator(new InstallCreator() {
                    @Override
                    public Dialog create(Update update, String s, Activity activity) {
                        // TODO: 2016/5/11 此处为下载APK完成后的回调。运行于主线程。在此创建Dialog
                        return null;
                    }
                })*/;

    }
}
