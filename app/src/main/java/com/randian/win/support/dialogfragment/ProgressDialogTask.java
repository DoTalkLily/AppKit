package com.randian.win.support.dialogfragment;

import android.support.v4.app.FragmentActivity;

import com.randian.win.support.SafeAsyncTask.SafeAsyncTask;
import com.randian.win.utils.ErrorHandler;

/**
 * Created with IntelliJ IDEA.
 * User: zeyiwu
 * Date: 13-9-24
 * Time: 下午2:16
 */
public abstract class ProgressDialogTask<T> extends SafeAsyncTask<T> {

    private FragmentActivity mActivity;
    private String mMessage;
    private DialogResultListener mListener;

    public ProgressDialogTask(FragmentActivity activity, int resID) {
        super();
        mActivity = activity;
        mMessage = activity.getString(resID);
    }

    public ProgressDialogTask(FragmentActivity activity, String message) {
        super();
        mActivity = activity;
        mMessage = message;
    }

    public void setDialogResultListener(DialogResultListener listener) {
        mListener = listener;
    }

    private void showIndeterminate(final String message, final int requestCode) {
        ProgressDialogFragment fragment = ProgressDialogFragment.show(mActivity, message, requestCode);
        fragment.setDialogResultListener(mListener);
    }

    private void showIndeterminate(final int messageID, final int requestCode) {
        ProgressDialogFragment fragment = ProgressDialogFragment.show(mActivity, messageID, requestCode);
        fragment.setDialogResultListener(mListener);
    }

    public void start(final int requestCode) {
        showIndeterminate(mMessage, requestCode);
        execute();
    }

    public void start() {
        start(0);
    }

    @Override
    public T call() throws Exception {
        return run();
    }

    @Override
    protected void onSuccess(T t) throws Exception {
            ProgressDialogFragment.dismiss(mActivity);
    }

    @Override
    protected void onException(Exception e) throws RuntimeException {
        ProgressDialogFragment.dismiss(mActivity);
        ErrorHandler.handleException(mActivity, e);
    }

    protected abstract T run() throws Exception;
}
