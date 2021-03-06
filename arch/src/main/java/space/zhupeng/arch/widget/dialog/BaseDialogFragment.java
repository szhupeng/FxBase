package space.zhupeng.arch.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import space.zhupeng.arch.R;

/**
 * 自定义对话框基类
 *
 * @author zhupeng
 * @date 2016/12/11
 */

public abstract class BaseDialogFragment extends AppCompatDialogFragment {

    protected Bundle mArgs;
    protected Activity mActivity;
    private int width;
    private int gravity = Gravity.CENTER;

    private View mRootView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mArgs = getArguments();
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mRootView = inflater.inflate(getLayoutResId(), container, false);
        initialize();
        initView(savedInstanceState);
        bindEvent();
        return mRootView;
    }

    private void initialize() {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BaseDialog);
        getDialog().getWindow().getAttributes().windowAnimations = getAnimStyles();

        //设置对话框背景色，否则有虚框
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(isCancelable());
        getDialog().setCanceledOnTouchOutside(false);

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (isCancelable()) {
                        onCancel();
                    }
                    return !isCancelable();
                }

                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(this.gravity);
        if (0 == this.width) {
            this.width = getResources().getDimensionPixelSize(R.dimen.custom_dialog_width);
        }
        getDialog().getWindow().setLayout(this.width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public BaseDialogFragment setWidth(int width) {
        this.width = width;
        return this;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    /**
     * 初始化对话框视图
     *
     * @param savedInstanceState
     */
    protected void initView(@Nullable Bundle savedInstanceState) {
    }

    /**
     * 绑定事件监听
     */
    protected void bindEvent() {
    }

    /**
     * 当对话框消失时的监听事件
     */
    protected void onCancel() {
    }

    @LayoutRes
    protected abstract int getLayoutResId();

    @StyleRes
    protected int getAnimStyles() {
        return 0;
    }

    @Override
    public void dismiss() {
        if (this.mActivity != null && !this.mActivity.isFinishing()) {
            //防止窗体句柄泄漏
            super.dismiss();
        }
    }

    protected final <T extends View> T findById(@IdRes int id) {
        if (null == this.mRootView) return null;

        return (T) this.mRootView.findViewById(id);
    }

    public void show(FragmentManager manager) {
        super.show(manager, "dialog");
    }
}
