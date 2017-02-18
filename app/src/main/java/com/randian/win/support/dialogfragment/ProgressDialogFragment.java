package com.randian.win.support.dialogfragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created with IntelliJ IDEA.
 * User: zeyiwu
 * Date: 13-9-4
 * Time: 下午1:56
 */
public class ProgressDialogFragment extends BaseDialogFragment {
    private static final String TAG = ProgressDialogFragment.class.getSimpleName();
    private String mMessage;

    public ProgressDialogFragment() {}

    public static ProgressDialogFragment show(FragmentActivity activity, int messageID, int requestCode) {
        return show(activity, activity.getString(messageID), requestCode);
    }

    public static ProgressDialogFragment show(FragmentActivity activity, String message, int requestCode) {
        Bundle bundle = createArguments(null, message, requestCode);
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        show(activity, fragment, bundle, ProgressDialogFragment.class.getSimpleName());
        return fragment;
    }

    public static void dismiss(FragmentActivity activity) {
        if (activity != null && !activity.isFinishing()) {
            dismiss(activity, ProgressDialogFragment.class.getSimpleName());
        }
    }

    public ProgressDialogFragment(String message) {
        mMessage = message;
    }

    public ProgressDialogFragment(Context context, int resId) {
        new ProgressDialogFragment(context.getString(resId));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mMessage = getMessage();
        }
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(mMessage);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return progressDialog;
    }

}
