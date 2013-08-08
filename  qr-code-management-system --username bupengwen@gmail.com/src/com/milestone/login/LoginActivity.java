package com.milestone.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.milestone.CaptureActivity;
import com.milestone.R;
import com.milestone.entity.Administrator;

public class LoginActivity extends Activity {
	private EditText uName;
	private EditText uPass;
	private Button loginBtn;
	private String loginResult;
	private final static String UNAME_HINT = "输入用户名";
	private final static String UPASS_HINT = "输入密码";
	private ProgressDialog pdg = null;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				Toast.makeText(LoginActivity.this, loginResult,
						Toast.LENGTH_LONG).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// no title
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		setUpViews();
		GradientDrawable gd = new GradientDrawable(Orientation.TOP_BOTTOM,
				new int[] { Color.RED, Color.YELLOW });
		getWindow().setBackgroundDrawable(gd);

	}

	@Override
	protected void onResume() {
		super.onResume();
		uName.setText(LoginActivity.UNAME_HINT);
		uName.setTextColor(Color.parseColor("#888888"));
		uPass.setText(LoginActivity.UPASS_HINT);
		uPass.setInputType(InputType.TYPE_CLASS_TEXT);
		uPass.setTextColor(Color.parseColor("#888888"));
	}

	private void setUpViews() {
		uName = (EditText) findViewById(R.id.userNameTxt);
		uPass = (EditText) findViewById(R.id.userPassTxt);
		uName.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (uName.getText().toString().equals(LoginActivity.UNAME_HINT)) {
					if (arg1) {
						uName.setTextColor(Color.BLACK);
						uName.setText("");
					}
				} else if (uName.getText().toString().length() == 0) {
					uName.setTextColor(Color.parseColor("#888888"));
					uName.setText(LoginActivity.UNAME_HINT);

				}
			}
		});
		uPass.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (uPass.getText().toString().equals(LoginActivity.UPASS_HINT)) {
					if (arg1) {
						uPass.setTransformationMethod(PasswordTransformationMethod
								.getInstance());
						uPass.setTextColor(Color.BLACK);
						uPass.setText("");
					}
				} else if (uPass.getText().toString().length() == 0) {
					uPass.setTextColor(Color.parseColor("#888888"));
					uPass.setText(LoginActivity.UPASS_HINT);

				}
			}
		});
		loginBtn = (Button) findViewById(R.id.loginBtn);
		loginBtn.setFocusableInTouchMode(true);
		loginBtn.requestFocus();
		loginBtn.setOnClickListener(new OnClickListener() {
			private String pass;
			private String name;

			@Override
			public void onClick(View v) {
				name = uName.getText().toString();
				pass = uPass.getText().toString();
				pdg = ProgressDialog.show(LoginActivity.this, "进度", "正在登录...",
						true);
				new Thread() {
					@Override
					public void run() {
						super.run();
						Administrator admin = new Administrator(name, pass);
						loginResult = new Login().checkLogin(admin);
						if (Login.LOGIN_SUCCESS.equals(loginResult)) {
							Intent intent = new Intent(LoginActivity.this,
									CaptureActivity.class);
							startActivity(intent);
							LoginActivity.this.finish();
						} else {
							handler.sendEmptyMessage(1);
						}
						pdg.dismiss();// 如果没有这句则不关闭
					}

				}.start();
			}
		});

	}
}
