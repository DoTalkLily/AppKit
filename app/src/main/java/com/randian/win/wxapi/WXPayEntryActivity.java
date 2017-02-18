package com.randian.win.wxapi;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.randian.win.R;
import com.randian.win.utils.Consts;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	
	private final String TAG = WXPayEntryActivity.this.getClass().getSimpleName();
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, Consts.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	/**
	 * errCode :
	 *          0 成功
	 * 			－1 错误  可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
	 * 		    －2 用户取消 	无需处理。发生场景：用户不支付了，点击取消，返回APP。
	 * @param resp
	 */
	@Override
	public void onResp(BaseResp resp) {
		Log.d("lili", "onPayFinish, errCode = " + resp.errCode+" msg:"+resp.errStr);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			Log.d(TAG,"onPayFinish,errCode="+resp.errCode);
			Intent mIntent = new Intent(Consts.WX_PAY_ACTION);
			mIntent.putExtra("status", resp.errCode);
			sendBroadcast(mIntent);//发送广播
			finish();
		}
	}
}