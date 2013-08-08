package com.milestone.login;

import org.json.JSONObject;

import com.milestone.entity.Administrator;
import com.milestone.tools.BaseProtocol;

public class Login extends BaseProtocol {

	Login() {
		super();
	}

	private final static String URL = BaseProtocol.BASE_PATH + "androidLogin";
	public final static String EMAIL_ERROR = "邮箱错误";
	public final static String LOGIN_SUCCESS = "登录成功";
	public final static String PASSWORD_INCORRECT = "密码错误";
	public final static String SYSTEM_ERROR = "系统错误,请重试";
	public String checkLogin(Administrator admin) {
		try {
			addNameValuePair("email", admin.getEmail());
			addNameValuePair("password", admin.getPassword());
			pack(URL);
			parse();
			JSONObject obj = this.getJSON();
			return obj.getString("result");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
