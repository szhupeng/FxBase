package space.zhupeng.arch.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * 通用工具方法
 *
 * @author zhupeng
 * @date 2017/1/14
 */

public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException("this method can't be called");
    }

    /**
     * 判断Activity是否被销毁
     *
     * @param activity
     * @return
     */
    public static boolean isActivityDestroyed(final Activity activity) {
        if (null == activity) return true;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            return activity.isDestroyed();
        }

        if (activity instanceof AppCompatActivity) {
            return ((AppCompatActivity) activity).getSupportFragmentManager().isDestroyed();
        }

        return activity.isFinishing();
    }

    /**
     * 任务是否空闲
     *
     * @param task
     * @return
     */
    public static boolean isTaskIdle(final AsyncTask task) {
        return null == task || AsyncTask.Status.FINISHED == task.getStatus();
    }

    /**
     * 取消异步任务
     *
     * @param task
     */
    public static void cancelTask(final AsyncTask task) {
        if (task != null && AsyncTask.Status.RUNNING == task.getStatus()) {
            task.cancel(true);
        }
    }

    /**
     * 在主线程安全执行
     *
     * @param handler
     * @param runnable
     */
    public static void postSafely(final Handler handler, final Runnable runnable) {
        if (null == handler) return;

        handler.post(new Runnable() {
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    Log.e("Utils", e.getMessage());
                }
            }
        });
    }

    /**
     * 在主线程安全延迟执行
     *
     * @param handler
     * @param runnable
     */
    public static void postDelayedSafely(final Handler handler, final Runnable runnable, final long delayMillis) {
        if (null == handler) return;

        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    Log.e("Utils", e.getMessage());
                }
            }
        }, delayMillis);
    }

    /**
     * 在主线程安全执行
     *
     * @param activity
     * @param runnable
     */
    public static void runOnUiThreadSafely(final Activity activity, final Runnable runnable) {
        if (null == runnable || null == activity || activity.isFinishing()) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())
            return;

        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            try {
                runnable.run();
            } catch (Exception e) {
                Log.e("Utils", e.getMessage());
            }
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        Log.e("Utils", e.getMessage());
                    }
                }
            });
        }
    }

    /**
     * 在主线程安全执行
     *
     * @param runnable
     */
    public static void runOnUiThreadSafely(final Runnable runnable) {
        if (null == runnable) return;

        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            try {
                runnable.run();
            } catch (Exception e) {
                Log.e("Utils", e.getMessage());
            }
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        Log.e("Utils", e.getMessage());
                    }
                }
            });
        }
    }

    /**
     * 在后台安全执行
     *
     * @param runnable
     */
    public static void runOnBackgroundSafely(final Runnable runnable) {
        if (null == runnable) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    Log.e("Utils", e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 根据key从Activity中返回的Bundle中获取value
     *
     * @param key
     * @param defValue
     * @return
     */
    public static String getActivityMetaData(Activity activity, String key, String defValue) {
        Bundle bundle = getActivityMetaDataBundle(activity.getPackageManager(), activity.getComponentName());
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getString(key);
        }
        return defValue;
    }

    /**
     * 获取Activity中的meta-data
     *
     * @param packageManager
     * @param component
     * @return
     */
    public static Bundle getActivityMetaDataBundle(PackageManager packageManager, ComponentName component) {
        Bundle bundle = null;
        try {
            ActivityInfo ai = packageManager.getActivityInfo(component,
                    PackageManager.GET_META_DATA);
            bundle = ai.metaData;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Utils", e.getMessage());
        }
        return bundle;
    }

    /**
     * 根据key从Application中返回的Bundle中获取value
     *
     * @param key
     * @param defValue
     * @return
     */
    private static String getAppMetaData(Context context, String key, String defValue) {
        Bundle bundle = getAppMetaDataBundle(context.getPackageManager(), context.getPackageName());
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getString(key);
        }
        return defValue;
    }

    /**
     * 获取Application中的meta-data
     *
     * @param packageManager
     * @param packageName
     * @return
     */
    private static Bundle getAppMetaDataBundle(PackageManager packageManager, String packageName) {
        Bundle bundle = null;
        try {
            ApplicationInfo ai = packageManager.getApplicationInfo(packageName,
                    PackageManager.GET_META_DATA);
            bundle = ai.metaData;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Utils", e.getMessage());
        }
        return bundle;
    }

    /**
     * 调用系统相机拍照
     *
     * @param activity
     * @param imageUri
     * @param requestCode
     */
    public static void takePicture(Activity activity, Uri imageUri, int requestCode) {
        //调用系统相机
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //将拍照结果保存至photo_file的Uri中，不保留在相册中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开系统相册
     *
     * @param activity    当前activity
     * @param requestCode 请求码
     */
    public static void openAlbum(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setType("image/*");
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            intent.setData(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 复制
     *
     * @param context
     * @param text
     */
    public static void copy(Context context, String text) {
        ClipboardManager mClipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text copy", text);
        mClipboard.setPrimaryClip(clip);
    }

    public static boolean isSpace(String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
