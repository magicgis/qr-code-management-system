package com.milestone;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.Result;
import com.milestone.result.ChangeState;
import com.milestone.result.ResultHandler;
import com.milestone.result.ResultHandlerFactory;
import com.milestone.tools.DebugLog;

public class CaptureActivity extends DecoderActivity {
	Bundle bundle = new Bundle();
	private static final String TAG = CaptureActivity.class.getSimpleName();
	private TextView statusView = null;
	private View resultView = null;
	private boolean inScanMode = false;
	JSONObject jsonObject = null;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				jsonObject = (JSONObject) msg.obj;
			}

		}
	};

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.sign);
		Log.v(TAG, "onCreate()");
		resultView = findViewById(R.id.result_view);
		statusView = (TextView) findViewById(R.id.status_view);
		inScanMode = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v(TAG, "onDestroy()");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, "onResume()");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.v(TAG, "onPause()");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (inScanMode)
				finish();
			else
				onResume();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void handleDecode(Result rawResult, Bitmap barcode) {
		drawResultPoints(barcode, rawResult);

		ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(
				this, rawResult);
		handleDecodeInternally(rawResult, resultHandler, barcode);
	}

	protected void showScanner() {
		inScanMode = true;
		resultView.setVisibility(View.GONE);
		statusView.setText(R.string.msg_default_status);
		statusView.setVisibility(View.VISIBLE);
		viewfinderView.setVisibility(View.VISIBLE);
	}

	protected void showResults(boolean isSuccess) {
		inScanMode = false;
		statusView.setVisibility(View.GONE);
		viewfinderView.setVisibility(View.GONE);
		// 成功则显示2s,不成功停止在本页面;
		if (isSuccess) {
			new Thread() {
				public void run() {
					try {
						Thread.sleep(2000);
						resultView.setVisibility(View.GONE);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
		} else
			resultView.setVisibility(View.VISIBLE);
	}

	// Put up our own UI for how to handle the decodBarcodeFormated contents.
	private void handleDecodeInternally(Result rawResult,
			ResultHandler resultHandler, Bitmap barcode) {
		onPause();
		String binaryCodeResult = resultHandler.getDisplayContents().toString();
		ChangeState changeState = ChangeState.getChangeStateInstance();
		String sn = changeState.getScanInfo(binaryCodeResult,
				ChangeState.SN_INDEX);
		String conferenceId = "1";
		bundle.putString(ChangeState.CONFERENCE_ID, conferenceId);
		bundle.putString(ChangeState.SN, sn);
		new Thread("checkCode") {
			public void run() {
				JSONObject jsonObject = ChangeState.getChangeStateInstance()
						.changeState(
								bundle.getString(ChangeState.CONFERENCE_ID),
								bundle.getString(ChangeState.SN));
				Message message = new Message();
				message.what = 1;
				message.obj = jsonObject;
				handler.sendMessage(message);
			};
		}.start();
		// 服务器查询后返回的结果
		String scanResult = null;
		try {
			if (jsonObject != null) {
				scanResult = jsonObject.getString("result");
			}
		} catch (JSONException e) {
			DebugLog.printError(e.getMessage());
		}
		boolean isSuccess = ChangeState.CHANGE_SUCCESS
				.equalsIgnoreCase(scanResult) ? true : false;
		showResults(isSuccess);
		if (!isSuccess) {
			return;
		}
		ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
		if (barcode == null) {
			barcodeImageView.setImageBitmap(BitmapFactory.decodeResource(
					getResources(), R.drawable.icon));
		} else {
			barcodeImageView.setImageBitmap(barcode);
		}
		TextView scanResultView = (TextView) findViewById(R.id.scan_result_view);
		scanResultView.setText(changeState.changeResult(scanResult));

		TextView typeTextView = (TextView) findViewById(R.id.attendance_name_view);

		typeTextView.setText(changeState.getScanInfo(binaryCodeResult,
				ChangeState.USER_NAME_INDEX));
		// 等级
		TextView levelView = (TextView) findViewById(R.id.attendace_level_view);
		levelView.setText(changeState.getScanInfo(binaryCodeResult,
				ChangeState.USER_LEVEL_INDEX));
		// 备注
		TextView descriptionView = (TextView) findViewById(R.id.attendance_description_view);
		descriptionView.setText(changeState.getScanInfo(binaryCodeResult,
				ChangeState.USER_DESCRIPTION));
		// 识别码
		TextView snView = (TextView) findViewById(R.id.sn_view);
		snView.setText(changeState.getScanInfo(binaryCodeResult,
				ChangeState.SN_INDEX));
	}
}
