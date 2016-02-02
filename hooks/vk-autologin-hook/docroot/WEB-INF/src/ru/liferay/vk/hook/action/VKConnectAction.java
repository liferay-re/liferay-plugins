package ru.liferay.vk.hook.action;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import ru.liferay.vk.WebKeys;
import ru.liferay.vk.util.PortletKeys;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.struts.BaseStrutsAction;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.expando.model.ExpandoTableConstants;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;

/**
 * Extension for open id login action to use vk.com
 * 
 * @author fav
 *
 */
public class VKConnectAction extends BaseStrutsAction {
	private static Log log = LogFactoryUtil.getLog(VKConnectAction.class);
	
	private static final String SUCCESS_REDIRECT_URL = "/portlet/login/vk_redirect.jsp";
	
	private static String VK_AUTH_TOKEN_URL_DEFAULT = "https://oauth.vk.com/access_token";
	private static String VK_USERS_URL_DEFAULT = "https://api.vk.com/method/users.get";
	
	@Override
	public String execute(
	        HttpServletRequest request, HttpServletResponse response)
	        throws Exception {
		
		Thread currentThread = Thread.currentThread();

        ClassLoader contextClassLoader =
            currentThread.getContextClassLoader();

        currentThread.setContextClassLoader(
            PortalClassLoaderUtil.getClassLoader());
        try {
        	ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        	
			log.info("Logging in through vk.com...");
			String redirect = ParamUtil.getString(request, "redirect");

			String code = ParamUtil.getString(request, "code");
			
			JSONObject accessToken = getAccessToken(themeDisplay.getCompanyId(), redirect, code);
			log.debug("VK access token = " + accessToken.getString("access_token"));
			
			JSONObject userJson = getUserJson(themeDisplay.getCompanyId(), accessToken);
			log.debug("VK user  = " + userJson.toString());
			
			HttpSession session = request.getSession();
			
			setVKCredentials(session, themeDisplay.getCompanyId(), userJson);

		} finally {
             currentThread.setContextClassLoader(contextClassLoader);
        }
			
		return SUCCESS_REDIRECT_URL;
		
	}
	
	protected void setVKCredentials(
			HttpSession session, long companyId, JSONObject json)
		throws Exception {
		
		JSONArray userTokenArray = json.getJSONArray("response");
		JSONObject userToken = userTokenArray.getJSONObject(0);
		log.debug(userToken.toString());
		log.debug("Screen name = " + userToken.getString("screen_name"));
		log.debug("UID = " + userToken.getLong("uid"));
		if (userToken == null || StringUtils.isEmpty(userToken.getString("screen_name")) || userToken.getLong("uid") <= 0 ) {
			log.debug("Failed to retrieve required info from VK user token");
			return;
		}
		
		User user = null;
		
		long vkontakteId = userToken.getLong("uid");
		if (vkontakteId > 0) {
			session.setAttribute(
					WebKeys.VK_ID, String.valueOf(vkontakteId));
			long userId = 0;
			try {
				//stored in expando so far
				List<ExpandoValue> exValues = ExpandoValueLocalServiceUtil.getColumnValues(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, 
						WebKeys.VK_ID, String.valueOf(vkontakteId), QueryUtil.ALL_POS, QueryUtil.ALL_POS);
				//expect one or less values
				if (exValues != null && exValues.size() > 0) {
					ExpandoValue exValue = exValues.get(0);
					userId = exValue.getClassPK();
					user = UserLocalServiceUtil.getUser(userId);
				}
			}
			catch (Exception nsue) {
			}

		}
		
		if (user == null) {
			//add user
			addUser(session, companyId, userToken);
		}
	}
	
	protected void addUser(
			HttpSession session, long companyId, JSONObject jsonObject)
		throws Exception {

		long creatorUserId = 0;
		boolean autoPassword = true;
		String password1 = StringPool.BLANK;
		String password2 = StringPool.BLANK;
		boolean autoScreenName = true;
		String screenName = StringPool.BLANK;
		String emailAddress = jsonObject.getString("screen_name") + StringPool.AT
				+ PrefsPropsUtil.getString(companyId, PortletKeys.TMP_EMAIL_POSTFIX);
		long facebookId = jsonObject.getLong("id");
		String openId = StringPool.BLANK;
		Locale locale = LocaleUtil.getDefault();
		String firstName = jsonObject.getString("first_name");
		String middleName = StringPool.BLANK;
		String lastName = jsonObject.getString("last_name");
		int prefixId = 0;
		int suffixId = 0;
		boolean male = Validator.equals(jsonObject.getString("sex"), "2");
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = StringPool.BLANK;
		long[] groupIds = null;
		long[] organizationIds = null;
		long[] roleIds = null;
		long[] userGroupIds = null;
		boolean sendEmail = true;

		ServiceContext serviceContext = new ServiceContext();
		log.info("About to add VK user with screen name = " + screenName + "; and email = " + emailAddress);
		User user = UserLocalServiceUtil.addUser(
			creatorUserId, companyId, autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress, facebookId, openId,
			locale, firstName, middleName, lastName, prefixId, suffixId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
			organizationIds, roleIds, userGroupIds, sendEmail, serviceContext);
		
		log.info("Added user with id = " + user.getUserId());

		UserLocalServiceUtil.updateLastLogin(
			user.getUserId(), user.getLoginIP());

		UserLocalServiceUtil.updatePasswordReset(user.getUserId(), false);

		UserLocalServiceUtil.updateEmailAddressVerified(user.getUserId(), true);
		
		//set vkontakteId
		user.getExpandoBridge().setAttribute(WebKeys.VK_ID, String.valueOf(jsonObject.getLong("uid")), false);

	}
	
	private JSONObject getUserJson(long companyId, JSONObject token)
			throws SystemException {
		if ((token == null) ||
				(token.getJSONObject("error") != null)) {
			log.debug("Error in access token");
			return null;
		}
		if (token == null || StringUtils.isEmpty(token.getString("user_id")) || StringUtils.isEmpty(token.getString("access_token"))) {
			log.debug("Failed to retrieve required info from VK access token");
			return null;
		}
		String url = HttpUtil.addParameter(
				PrefsPropsUtil.getString(companyId, PortletKeys.VK_USERS_URL, VK_USERS_URL_DEFAULT), "uid", token.getString("user_id"));
		url = HttpUtil.addParameter(
				url, "access_token", token.getString("access_token"));
		url = HttpUtil.addParameter(
				url, "fields", "uid,email,first_name,last_name,nickname,screen_name,sex,bdate,city,country");
		
		Http.Options options = new Http.Options();
		options.setLocation(url);
		options.setPost(true);
		
		try {
			String content = HttpUtil.URLtoString(options);
			log.debug("VK user content = " + content);
			if (Validator.isNotNull(content)) {
				JSONObject json = JSONFactoryUtil.createJSONObject(content);
				
				return json;
				
			}
		}
		catch (Exception e) {
			throw new SystemException(
				"Unable to retrieve VK user", e);
		}

		return null;
	}
	
	
	private JSONObject getAccessToken(long companyId, String redirect, String code)
			throws SystemException {

			String url = HttpUtil.addParameter(
					 PrefsPropsUtil.getString(companyId, PortletKeys.VK_AUTH_TOKEN_URL, VK_AUTH_TOKEN_URL_DEFAULT), "client_id", PrefsPropsUtil.getString(companyId, PortletKeys.VK_AUTH_CLIENT_ID));
			
			if (StringUtils.isEmpty(redirect)) {
				redirect = PrefsPropsUtil.getString(companyId, PortletKeys.VK_AUTH_REDIRECT_URL);
			}
			url = HttpUtil.addParameter(
				url, "redirect_uri", redirect);

			url = HttpUtil.addParameter(
				url, "client_secret", PrefsPropsUtil.getString(companyId, PortletKeys.VK_AUTH_CLIENT_SECRET));
			url = HttpUtil.addParameter(url, "code", code);

			Http.Options options = new Http.Options();

			options.setLocation(url);
			options.setPost(true);

			try {
				String content = HttpUtil.URLtoString(options);
				log.debug("VK content = " + content);
				if (Validator.isNotNull(content)) {
					JSONObject json = JSONFactoryUtil.createJSONObject(content);
					
					return json;
					
				}
			}
			catch (Exception e) {
				throw new SystemException(
					"Unable to retrieve VK access token", e);
			}

			return null;
		}
}
