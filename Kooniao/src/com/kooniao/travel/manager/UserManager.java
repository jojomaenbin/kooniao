package com.kooniao.travel.manager;

import java.util.List;

import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.R;
import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.api.ApiCaller.APIAgreementResultCallback;
import com.kooniao.travel.api.ApiCaller.APICustomerDetailResultCallback;
import com.kooniao.travel.api.ApiCaller.APIGetVerificationCodeResultCallback;
import com.kooniao.travel.api.ApiCaller.APIMessageResultCallback;
import com.kooniao.travel.api.ApiCaller.APIRoleListResultCallback;
import com.kooniao.travel.api.ApiCaller.APIRollCallDetailResultCallback;
import com.kooniao.travel.api.ApiCaller.APIRollCallListResultCallback;
import com.kooniao.travel.api.ApiCaller.APIStringResultCallback;
import com.kooniao.travel.api.ApiCaller.APISubMessageResultCallback;
import com.kooniao.travel.api.ApiCaller.APIUserRegisterResultCallback;
import com.kooniao.travel.api.ApiCaller.ApiUserInfoResultCallback;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.model.CustomerDetail;
import com.kooniao.travel.model.Message;
import com.kooniao.travel.model.Role;
import com.kooniao.travel.model.RollCall;
import com.kooniao.travel.model.RollCallDetail;
import com.kooniao.travel.model.SubMessage;
import com.kooniao.travel.model.TeamCustomer;
import com.kooniao.travel.model.UserInfo;
import com.kooniao.travel.model.RollCallDetail.Tourist;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.EncryptUtil;
import com.kooniao.travel.utils.StringUtil;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;

/**
 * 用户管理
 * 
 * @author ke.wei.quan
 * 
 */
public class UserManager {

	UserManager() {
	}

	private static UserManager instance;

	public static UserManager getInstance() {
		if (instance == null) {
			synchronized (ApiCaller.class) {
				if (instance == null) {
					instance = new UserManager();
				}
			}
		}

		return instance;
	}

	/**************************************************************************************************************************
	 * 
	 * 回调接口
	 * 
	 * *************************************************************************
	 * ************************************************
	 */

	public interface UserInfoResultCallback {
		void result(String errMsg, UserInfo userInfo);
	}

	public interface StringResultCallback {
		void result(String errMsg);
	}

	public interface GetVerificationCodeResultCallback {
		void result(String errMsg, int resultCode);
	}

	public interface CustomerDetailResultCallback {
		void result(String errMsg, CustomerDetail customerDetail, int pageCount);
	}
	
	public interface RoleListResultCallback {
		void result(String errMsg, List<Role> roles);
	}
	
	public interface MessageResultCallback {
		void result(String errMsg, List<Message> messages);
	}
	
	public interface SubMessageResultCallback {
		void result(String errMsg, List<SubMessage> subMessages);
	}
	
	public interface AgreementResultCallback {
		void result(String errMsg, String content);
	}

	public interface RollCallListResultCallback {
		void result(String errMsg, List<RollCall> rollCalls, int pageCount);
	}

	public interface RollCallDetailResultCallback {
		void result(String errMsg, RollCallDetail rollCallDetail);
	}
	
	/**************************************************************************************************************************
	 * 
	 * 请求访问处理
	 * 
	 * *************************************************************************
	 * ************************************************
	 */

	/**
	 * 验证登录名
	 * 
	 * @param loginKey
	 * @return
	 */
	public String verifyLoginKey(String loginKey) {
		final int minLoginKeyLength = 3; // 最小登录名长度
		// 错误提示
		String errTips = null;
		if (loginKey.length() == 0) {
			errTips = KooniaoApplication.getInstance().getResources().getString(R.string.user_key_empty);
		} else {
			if (loginKey.length() < minLoginKeyLength) {
				errTips = KooniaoApplication.getInstance().getResources().getString(R.string.user_key_short);
			}
		}

		return errTips;
	}

	/**
	 * 验证登录密码
	 * 
	 * @param loginPassword
	 * @return
	 */
	public String verifyLoginPassword(String loginPassword) {
		final int minLoginPasswordLength = 6; // 最小密码长度
		// 错误提示
		String errTips = null;
		if (loginPassword.length() == 0) {
			errTips = KooniaoApplication.getInstance().getResources().getString(R.string.password_empty);
		} else {
			if (loginPassword.length() < minLoginPasswordLength) {
				errTips = KooniaoApplication.getInstance().getResources().getString(R.string.password_short);
			}
		}

		return errTips;
	}

	/**
	 * 用户登录
	 * 
	 * @param loginName
	 * @param passwd
	 * @param callback
	 */
	public void userLogin(String loginName, String passwd, final UserInfoResultCallback callback) {
		ApiCaller.getInstance().userLogin(loginName, passwd, new ApiUserInfoResultCallback() {

			@Override
			public void result(String errMsg, UserInfo userInfo) {
				if (userInfo != null) {
					/**
					 * 保存apiKey、apiKeySecret和uid到本地SharePreference
					 */
					String apiKey = userInfo.getApiKey();
					String apiKeySecret = userInfo.getApiKeySecret();
					int uid = userInfo.getUid();
					AppSetting.getInstance().saveStringPreferencesByKey(Define.ApiKey, apiKey);
					AppSetting.getInstance().saveStringPreferencesByKey(Define.ApiKeySecret, apiKeySecret);
					AppSetting.getInstance().saveIntPreferencesByKey(Define.UID, uid);

					/**
					 * 保存用户信息到数据库
					 */
					saveOrUpdateUserInfo(userInfo);
				}
				callback.result(errMsg, userInfo);
			}
		});
	}

	/**
	 * 退出登录
	 * 
	 * @param type
	 * @param callback
	 */
	public void userLogout(final StringResultCallback callback) {
		ApiCaller.getInstance().userLogout(new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				if (errMsg == null) {
					// 清除用户相关的SharePreference信息
					AppSetting.getInstance().clearUserPreference();
				}
				callback.result(errMsg);
			}
		});
	}

	private boolean isFirstTimeLoadUserDetailInfo = true;

	/**
	 * 获取用户详细信息
	 * 
	 * @param callback
	 */
	public void loadUserDetailInfo(final UserInfoResultCallback callback) {
		if (isFirstTimeLoadUserDetailInfo) {
			UserInfo localUserInfo = getCurrentUserInfo();
			if (localUserInfo != null) {
				callback.result(null, localUserInfo);
			}
		}

		ApiCaller.getInstance().loadUserDetailInfo(new ApiUserInfoResultCallback() {

			@Override
			public void result(String errMsg, UserInfo userInfo) {
				if (userInfo != null) {
					saveOrUpdateUserInfo(userInfo);
				}
				callback.result(errMsg, userInfo);
			}
		});
	}

	/**
	 * 更新用户性别
	 * 
	 * @param sex
	 * @param callback
	 */
	public void updateSex(int sex, final StringResultCallback callback) {
		ApiCaller.getInstance().updateSex(sex, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 更新用户昵称
	 * 
	 * @param nickName
	 * @param callback
	 */
	public void updateNickName(String nickName, String sexString, final StringResultCallback callback) {
		int sex = StringUtil.getStringFromR(R.string.sex_man).equals(sexString) ? 1 : 0;
		ApiCaller.getInstance().updateNickName(nickName, sex, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 更改用户头像
	 * 
	 * @param filePath
	 * @param callback
	 */
	public void uploadUserAvatar(String filePath, final StringResultCallback callback) {
		ApiCaller.getInstance().uploadUserAvatar(filePath, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取客户详细
	 * 
	 * @param pageNum
	 * @param clientId
	 * @param callback
	 */
	public void loadStoreClientDetail(int pageNum, int clientId, final CustomerDetailResultCallback callback) {
		ApiCaller.getInstance().loadStoreClientDetail(pageNum, clientId, new APICustomerDetailResultCallback() {

			@Override
			public void result(String errMsg, CustomerDetail customerDetail, int pageCount) {
				callback.result(errMsg, customerDetail, pageCount);
			}
		});
	}

	/**
	 * 用户注册
	 * 
	 * @param type
	 *            注册类型（邮箱:0;手机:1）
	 * @param emailAddress
	 *            邮箱
	 * @param nickName
	 *            昵称
	 * @param password
	 *            密码
	 * @param mobile
	 *            电话
	 * @param verficationCode
	 *            验证码
	 * @param callback
	 */
	public void userRegister(int type, String emailAddress, String nickName, String password, String mobile, String verficationCode, final StringResultCallback callback) {
		password = EncryptUtil.generatePassword(password);
		ApiCaller.getInstance().userRegister(type, emailAddress, nickName, password, mobile, verficationCode, new APIUserRegisterResultCallback() {

			@Override
			public void result(String errMsg, String apiKey, String apiKeySecret, int uid) {
				/**
				 * 保存到本地
				 */
				AppSetting.getInstance().saveStringPreferencesByKey(Define.ApiKey, apiKey);
				AppSetting.getInstance().saveStringPreferencesByKey(Define.ApiKeySecret, apiKeySecret);
				AppSetting.getInstance().saveIntPreferencesByKey(Define.UID, uid);
				// 保存到数据库
				UserInfo userInfo = new UserInfo();
				userInfo.setUid(uid);
				saveUserInfo(userInfo); 
				// 回调
				callback.result(errMsg);
			}
		});
	}

	/**
	 * 获取注册的手机验证码
	 * 
	 * @param mobile
	 * @param callback
	 */
	public void getRegisterVerificationCode(String mobile, final GetVerificationCodeResultCallback callback) {
		String module = "register";
		getMobileVerificationCode(mobile, module, callback); 
	}

	/**
	 * 获取找回密码的手机验证码
	 * 
	 * @param mobile
	 * @param callback
	 */
	public void getFindPasswdVerificationCode(String mobile, final GetVerificationCodeResultCallback callback) {
		String module = "find_password";
		getMobileVerificationCode(mobile, module, callback); 
	}

	/**
	 * 获取修改手机的手机验证码
	 * 
	 * @param mobile
	 * @param callback
	 */
	public void getModifyMobileVerificationCode(String mobile, final GetVerificationCodeResultCallback callback) {
		String module = "modify_mobile";
		getMobileVerificationCode(mobile, module, callback); 
	}
	
	/**
	 * 获取手机验证码
	 * @param mobile
	 * @param module
	 * @param callback
	 */
	private void getMobileVerificationCode(String mobile, String module, final GetVerificationCodeResultCallback callback) {
		ApiCaller.getInstance().getPhoneVerificationCode(mobile, module, new APIGetVerificationCodeResultCallback() {

			@Override
			public void result(String errMsg, int resultCode) {
				callback.result(errMsg, resultCode);
			}
		});
	}
	
	/**
	 * 邮箱重设密码
	 * 
	 * @param email
	 * @param callback
	 */
	public void forgetPasswordByEmail(String email, final StringResultCallback callback) {
		ApiCaller.getInstance().forgetPasswordByEmail(email, new APIStringResultCallback() {
			
			@Override
			public void result(String errMsg) {
				callback.result(errMsg); 
			}
		});
	}
	
	/**
	 * 手机重设密码
	 * @param phoneNum
	 * @param validateNum
	 * @param password
	 * @param callback
	 */
	public void forgetPasswordByPhone(String phoneNum, String validateNum, String password, final StringResultCallback callback) {
		ApiCaller.getInstance().forgetPasswordByPhone(phoneNum, validateNum, password, new APIStringResultCallback() {
			
			@Override
			public void result(String errMsg) {
				callback.result(errMsg); 
			}
		});
	}
	
	/**
	 * 绑定邮箱
	 * 
	 * @param email
	 * @param callback
	 */
	public void bindingEmail(String email, final StringResultCallback callback) {
		ApiCaller.getInstance().bindingEmail(email, new APIStringResultCallback() {
			
			@Override
			public void result(String errMsg) {
				callback.result(errMsg); 
			}
		});
	}
	
	/**
	 * 绑定手机
	 * 
	 * @param phoneNum
	 * @param validateNum
	 * @param callback
	 */
	public void bindingPhone(String phoneNum, String validateNum, final StringResultCallback callback) {
		ApiCaller.getInstance().bindingPhone(phoneNum, validateNum, new APIStringResultCallback() {
			
			@Override
			public void result(String errMsg) {
				callback.result(errMsg); 
			}
		});
	}
	
	/**
	 * 角色申请
	 * @param realName 真实姓名
	 * @param certificate 资格证书
	 * @param tel 联系电话
	 * @param roleId 申请角色id数组(groupid_str)
	 * @param callback
	 */
	public void roleApply(String realName, String certificate, String tel, int roleId, final StringResultCallback callback) {
		ApiCaller.getInstance().roleApply(realName, certificate, tel, roleId, new APIStringResultCallback() {
			
			@Override
			public void result(String errMsg) {
				callback.result(errMsg); 
			}
		});
	}
	
	/**
	 * 获取角色列表
	 * @param callback
	 */
	public void getRoleList(final RoleListResultCallback callback) {
		ApiCaller.getInstance().getRoleList(new APIRoleListResultCallback() {
			
			@Override
			public void result(String errMsg, List<Role> roles) {
				callback.result(errMsg, roles); 
			}
		});
	}
	
	/**
	 * 获取消息一级列表
	 * @param type
	 * @param callback
	 */
	public void loadMessageList(String type, final MessageResultCallback callback) {
		ApiCaller.getInstance().loadMessageList(type, new APIMessageResultCallback() {
			
			@Override
			public void result(String errMsg, List<Message> messages) {
				callback.result(errMsg, messages); 
			}
		});
	}
	
	/**
	 * 获取消息二级列表
	 * @param ptype 一级分类
	 * @param mtype 二级分类
	 * @param pageNum 当前页码
	 * @param callback
	 */
	public void loadSubMessageList(String ptype, String mtype, int pageNum, final SubMessageResultCallback callback) {
		ApiCaller.getInstance().loadSubMessageList(ptype, mtype, pageNum, new APISubMessageResultCallback() {
			
			@Override
			public void result(String errMsg, List<SubMessage> subMessages) {
				callback.result(errMsg, subMessages); 
			}
		});
	}
	
	/**
	 * 是否接受指派导游
	 * @param notifyId 消息id
	 * @param isAccept (1:接收，2:拒绝)
	 * @param teamId 团单id
	 * @param callback
	 */
	public void acceptGuideAppoint(int notifyId, int isAccept, int teamId, final StringResultCallback callback) {
		ApiCaller.getInstance().acceptGuideAppoint(notifyId, isAccept, teamId, new APIStringResultCallback() {
			
			@Override
			public void result(String errMsg) {
				callback.result(errMsg); 
			}
		});
	}
	
	/**
	 * 获取协议
	 * @param type
	 * @param callback
	 */
	public void loadAgreement(int type, final AgreementResultCallback callback) {
		ApiCaller.getInstance().loadAgreement(type, new APIAgreementResultCallback() {
			
			@Override
			public void result(String errMsg, String content) {
				callback.result(errMsg, content); 
			}
		});
	}
	
	/**
	 * 获取点名列表
	 * 
	 * @param pageNum
	 * @param teamId
	 * @param callback
	 */
	public void loadRollCallList(int pageNum, int teamId, final RollCallListResultCallback callback) {
		ApiCaller.getInstance().loadRollCallList(pageNum, teamId, new APIRollCallListResultCallback() {
			
			@Override
			public void result(String errMsg, List<RollCall> rollCalls, int pageCount) {
				callback.result(errMsg, rollCalls, pageCount); 
			}
		});
	}
	
	/**
	 * 删除点名名单
	 * 
	 * @param rollCallID
	 * @param callback
	 */
	public void deleteRollCall(int rollCallID, final StringResultCallback callback) {
		ApiCaller.getInstance().deleteRollCall(rollCallID, new APIStringResultCallback() {
			
			@Override
			public void result(String errMsg) {
				callback.result(errMsg); 
			}
		});
	}
	
	/**
	 * 根据点名表id获取点名详细
	 * 
	 * @param teamId
	 * @param rollCallID
	 * @param callback
	 */
	public void loadRollCallDetail(int teamId, int rollCallID, final RollCallDetailResultCallback callback) {
		final List<TeamCustomer> teamCustomers = TravelManager.getInstance().getTeamCustomerListByTravelId(teamId);
		
		ApiCaller.getInstance().loadRollCallDetail(rollCallID, new APIRollCallDetailResultCallback() {
			
			@Override
			public void result(String errMsg, RollCallDetail rollCallDetail) {
				if (rollCallDetail != null) {
					List<Tourist> tourists = rollCallDetail.getTouristList();
					if (tourists != null && teamCustomers != null) {
						for (TeamCustomer teamCustomer : teamCustomers) {
							for (Tourist tourist : tourists) {
								if (teamCustomer.getName().equals(tourist.getName())) {
									tourist.setTel(teamCustomer.getTel()); 
								}
							}
						}
					}
				}
				
				callback.result(errMsg, rollCallDetail); 
			}
		});
	}
	
	/**
	 * 修改或添加点名名单
	 * @param touristListJson
	 * @param rollCallTime
	 * @param teamId
	 * @param rollCallID 不为零则表示修改，为零则表示新增
	 * @param callback
	 */
	public void editOrAddRollCall(String touristListJson,int teamId, int rollCallID, final StringResultCallback callback) {
		long rollCallTime = System.currentTimeMillis() / 1000;
		ApiCaller.getInstance().editOrAddRollCall(touristListJson, rollCallTime, teamId, rollCallID, new APIStringResultCallback() {
			
			@Override
			public void result(String errMsg) {
				callback.result(errMsg);
			}
		});
	}

	/**************************************************************************************************************************
	 * 
	 * 数据库操作
	 * 
	 * *************************************************************************
	 * ************************************************
	 */

	/**
	 * 保存或更新用户信息
	 * 
	 * @param userInfo
	 */
	public void saveOrUpdateUserInfo(UserInfo userInfo) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		UserInfo localUserInfo = getUserInfoByUid(userInfo.getUid());
		if (localUserInfo != null) {
			db.update(userInfo);
		} else {
			db.save(userInfo);
		}
	}

	/**
	 * 保存用户信息
	 * 
	 * @param userInfo
	 */
	public void saveUserInfo(UserInfo userInfo) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		db.save(userInfo);
	}

	/**
	 * 更新用户信息
	 * 
	 * @param userInfo
	 */
	public void updateUserInfo(UserInfo userInfo) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		db.update(userInfo);
	}

	/**
	 * 根据用户id获取用户信息
	 * 
	 * @param uid
	 * @return
	 */
	public UserInfo getUserInfoByUid(int uid) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		UserInfo userInfo = db.queryById(uid, UserInfo.class);
		return userInfo;
	}

	/**
	 * 获取当前用户信息
	 * 
	 * @return
	 */
	public UserInfo getCurrentUserInfo() {
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		if (uid == 0) {
			return null;
		}
		UserInfo userInfo = getUserInfoByUid(uid);
		return userInfo;
	}

	/**
	 * 更新本地用户收藏数目
	 * 
	 * @param count
	 */
	public void updateCollectCount(int count) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		UserInfo localUserInfo = getCurrentUserInfo();
		localUserInfo.setFavoriteNum(count);
		db.update(localUserInfo);
	}

	/**
	 * 更新本地用户行程数目
	 * 
	 * @param count
	 */
	public void updateTravelCount(int count) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		UserInfo localUserInfo = getCurrentUserInfo();
		localUserInfo.setPlanNum(count);
		db.update(localUserInfo);
	}

	/**
	 * 更新本地用户收藏数目
	 * 
	 * @param isKeep
	 */
	public void undateCollectCount(int isKeep) {
		UserInfo localUserInfo = UserManager.getInstance().getCurrentUserInfo();
		if (localUserInfo != null) {
			int collectCount = localUserInfo.getFavoriteNum();
			if (isKeep == 1) {
				collectCount = collectCount + 1;
			} else {
				collectCount = collectCount - 1;
			}
			UserManager.getInstance().updateCollectCount(collectCount);
		}
	}

	/**
	 * 更新本地用户行程数目
	 * 
	 * @param isKeep
	 */
	public void undateTravelCount(int isKeep) {
		UserInfo localUserInfo = UserManager.getInstance().getCurrentUserInfo();
		if (localUserInfo != null) {
			int travelCount = localUserInfo.getPlanNum();
			if (isKeep == 1) {
				travelCount = travelCount + 1;
			} else {
				travelCount = travelCount - 1;
			}
			UserManager.getInstance().updateTravelCount(travelCount);
		}
	}

}
