package ru.liferay.vk.security.auth;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.AutoLogin;
import com.liferay.portal.security.auth.AutoLoginException;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.expando.model.ExpandoTableConstants;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;

import ru.liferay.vk.WebKeys;

public class VKAutoLoginFilter implements AutoLogin {
	private static Log log = LogFactoryUtil.getLog(VKAutoLoginFilter.class);

	@Override
	public String[] login(HttpServletRequest request,
			HttpServletResponse response) throws AutoLoginException {
		log.debug("VK auto login filter fired...");
		String[] credentials = null;

		try {
			long companyId = PortalUtil.getCompanyId(request);

			HttpSession session = request.getSession();

			User user = null;
			long userId = 0;
			long vkontakteId = GetterUtil.getLong(
					(String)session.getAttribute(WebKeys.VK_ID));
			if (vkontakteId > 0) {
				log.debug(String.format("Searching user with vkontakteId = [%d]", vkontakteId));
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
					log.debug(String.format("User with vkontakteId = [%d] was not found", vkontakteId));
					return credentials;
				}
				
				if (user != null) {
					log.debug(String.format("User with vkontakteId = [%d] was found. User id = [%d]", vkontakteId, user.getUserId()));
					credentials = new String[3];

					credentials[0] = String.valueOf(user.getUserId());
					credentials[1] = user.getPassword();
					credentials[2] = Boolean.FALSE.toString();
				} else {
					log.debug(String.format("User with vkontakteId = [%d] was not found", vkontakteId));
				}
			} else {
				return credentials;
			}

		}
		catch (Exception e) {
			log.error(e, e);
		}

		return credentials;
	}

	@Override
	public String[] handleException(HttpServletRequest request,
			HttpServletResponse response, Exception e)
			throws AutoLoginException {
		// TODO Auto-generated method stub
		return null;
	}

}
