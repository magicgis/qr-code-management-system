package com.milestone.result;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.milestone.tools.BaseProtocol;
import com.milestone.tools.DebugLog;

/**
 * @author vincent
 * @see 二维码需要的信息,为了减少扫描时信息交换量,这里尽量带更多信息,从原理来说,应该不会增加文件的大小
 * @param 序列号
 *            这个是主键(或者是由用户id和会议ID构成),这里有要求:<br/>
 *            1,不能简单破解或者找出规律,这里需要使用RSA加密,这个收取门票费是很必要的<br/>
 *            2,查询时客户端只提交加密后的结果,然后服务器反向成主键;
 * @param name
 *            用户名,这里为了快速显示放置在二维码中,
 * @param level
 *            用户等级,默认为"",为以后留着接口使用,头像做成字符串放置在二维码中;
 * @param description
 *            用户头衔/备注,默认为"";
 * 
 * */

public class ChangeState extends BaseProtocol {
	private static ChangeState changeState = null;

	private ChangeState() {

	}

	// 由于这个方法只会使用一个,而且使用频率很高,所以就用单例模式完成
	public static ChangeState getChangeStateInstance() {
		if (changeState != null) {
			return changeState;
		}
		return changeState = new ChangeState();
	}

	/**
	 * 从二维码获得信息
	 */
	public static String SPLIT_STRING = ",";
	public static final int SN_INDEX = 0;
	// private static final int USER_ID = 1;
	public static final int USER_NAME_INDEX = 1;
	public static final int USER_LEVEL_INDEX = 2;
	public static final int USER_DESCRIPTION = 3;

	public String getScanInfo(String binaryCodeResult, int index) {
		String[] codeInfo = binaryCodeResult.split(ChangeState.SPLIT_STRING);
		return codeInfo[index];
	}

	/**
	 * 提交到服务器信息
	 * 
	 * @param sn
	 *            扫描序号,RSA加密后的号码,制作成final static以后又不能的也很方便修改
	 * @param conference_id
	 *            会议id
	 * 
	 * */
	private static final String URL = BaseProtocol.BASE_PATH + "changeState";
	public static final String SN = "attendance.id";
	public static final String CONFERENCE_ID = "conference.id";

	// 用户改变状态
	public JSONObject changeState(String conferenceId, String sn) {
		addNameValuePair(ChangeState.SN, sn);
		addNameValuePair(ChangeState.CONFERENCE_ID, conferenceId);
		try {
			pack(URL);
			parse();
			JSONObject obj = this.getJSON();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			DebugLog.printError(e.toString());
			DebugLog.printError(e.getMessage());
			DebugLog.printError(e.getLocalizedMessage());
			return null;
		}
	}

	// 将英文的结果转化为中文结果
	/**
	 * 服务器返回状态
	 * 
	 * @param unRegistered
	 *            :不属于这个会议
	 * @param notFound
	 *            :数据库无此人
	 * @param undersigned
	 *            :已经签到
	 * @param late
	 *            :迟到
	 * @param changeSuccess
	 *            :成功
	 * @param changeFailure
	 *            :内部错误
	 * */
	public static final String UN_REGISTERED = "unRegistered";
	public static final String NOT_FOUND = "notFound";
	public static final String UNDERSIGNED = "undersigned";
	public static final String LATE = "late";
	public static final String CHANGE_SUCCESS = "changeSuccess";
	public static final String CHANGE_FAILURE = "changeFailure";

	public String changeResult(String result) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(ChangeState.UN_REGISTERED, "非法门票");
		map.put(ChangeState.CHANGE_FAILURE, "内部错误");
		map.put(ChangeState.NOT_FOUND, "非法识别码");
		map.put(ChangeState.LATE, "迟到");
		map.put(ChangeState.CHANGE_SUCCESS, "签到成功");
		map.put(ChangeState.UNDERSIGNED, "已经签到");
		return map.get(result);
	}
}
