package com.randian.win.utils;

import android.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.android.volley.VolleyError;
import com.randian.win.R;


/**
* User: 42
*/
public class ErrorHandler {

    private static final String TAG = "Volley";

   public static final class ErrorMsg {
       //API exception
       public static final String API_ERROR_TIMEOUT_ERROR = "com.android.volley.TimeoutError";

   }

    private static int handleGeneralException(Throwable throwable) {
        int err = R.string.error_unknown;
        try {
            throw throwable;
        } catch (IndexOutOfBoundsException e) {
            err = R.string.error_no_more;
        } catch (InterruptedException e) {
            err = R.string.error_wait;
        } catch (Exception e) {
            err = R.string.error_unknown;
        } catch (Throwable tr) {
            err = R.string.error_unknown;
        }
        return err;
    }

    private static int handleGeneralVolleyError(VolleyError volleyError) {
        int err = R.string.error_unknown;
        try {
            throw volleyError;
        } catch (VolleyError e) {
            if (ErrorMsg.API_ERROR_TIMEOUT_ERROR.equals(e.getMessage())) {
                err = R.string.error_timeout;
            } else {
                err = R.string.error_unknown;
            }
        }
        return err;
    }


    public static int getErrorMessage(Throwable throwable) {
        int messageId = 0;
        if (throwable instanceof VolleyError) {
            messageId = handleGeneralVolleyError((VolleyError) throwable);
        } else {
            messageId = handleGeneralException(throwable);
        }
        return messageId;
    }

    public static void showAlert(FragmentActivity activity, Throwable throwable) {
        int msg = getErrorMessage(throwable);
        if (R.string.error_wait != msg ) {
            Toaster.showShort(activity, getErrorMessage(throwable));
        }
    }

    public static void handleException(FragmentActivity activity, Throwable throwable) {
        if (activity != null) {
            LogUtils.d(TAG, "error:" + throwable.getMessage());
            showAlert(activity, throwable);
        }
    }
}
