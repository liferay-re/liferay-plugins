<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@ include file="/html/portlet/portal_settings/init.jsp" %>

<div class="portlet-msg-info">
	<liferay-ui:message key="vk-help-message"/>
</div>

<%
boolean vkConnectAuthEnabled = PrefsPropsUtil.getBoolean(company.getCompanyId(), "VK_AUTH_ENABLED", false);
String vkConnectAppId = PrefsPropsUtil.getString(company.getCompanyId(), "VK_AUTH_CLIENT_ID", StringPool.BLANK);
String vkConnectAppSecret = PrefsPropsUtil.getString(company.getCompanyId(), "VK_AUTH_CLIENT_SECRET", StringPool.BLANK);
String vkConnectGraphURL = PrefsPropsUtil.getString(company.getCompanyId(), "VK_AUTH_URL", "https://oauth.vk.com/authorize");
String vkConnectRedirectURL = PrefsPropsUtil.getString(company.getCompanyId(), "VK_AUTH_REDIRECT_URL", StringPool.BLANK);
String tmpEmailPostfix = PrefsPropsUtil.getString(company.getCompanyId(), "TMP_EMAIL_POSTFIX", StringPool.BLANK);
String vkOauthTokenURL = PrefsPropsUtil.getString(company.getCompanyId(),"VK_AUTH_TOKEN_URL", "https://oauth.vk.com/access_token");
String vkUsersGetURL = PrefsPropsUtil.getString(company.getCompanyId(),"VK_USERS_URL", "https://api.vk.com/method/users.get");
%>

<aui:fieldset>
	<aui:input label="enabled" name='<%= "settings--" + "VK_AUTH_ENABLED" + "--" %>' type="checkbox" value="<%= vkConnectAuthEnabled %>" />

	<aui:input cssClass="lfr-input-text-container" label="application-id" name='<%= "settings--" + "VK_AUTH_CLIENT_ID" + "--" %>' type="text" value="<%= vkConnectAppId %>" />

	<aui:input cssClass="lfr-input-text-container" label="application-secret" name='<%= "settings--" + "VK_AUTH_CLIENT_SECRET" + "--" %>' type="text" value="<%= vkConnectAppSecret %>" />

	<aui:input cssClass="lfr-input-text-container" label="oauth-authentication-url" name='<%= "settings--" + "VK_AUTH_URL" + "--" %>' type="text" value="<%= vkConnectGraphURL %>" />

	<aui:input cssClass="lfr-input-text-container" label="redirect-url" helpMessage="redirect-url-help-message" name='<%= "settings--" + "VK_AUTH_REDIRECT_URL" + "--" %>' type="text" value="<%= vkConnectRedirectURL %>" />
	
	<aui:input cssClass="lfr-input-text-container" label="oauth-token-url" name='<%= "settings--" + "VK_AUTH_TOKEN_URL" + "--" %>' type="text" value="<%= vkOauthTokenURL %>" />
	
	<aui:input cssClass="lfr-input-text-container" label="vk-users-url" name='<%= "settings--" + "VK_USERS_URL" + "--" %>' type="text" value="<%= vkUsersGetURL %>" />
	
	<aui:input cssClass="lfr-input-text-container" label="email-postfix" helpMessage="email-postfix-help-msg" name='<%= "settings--" + "TMP_EMAIL_POSTFIX" + "--" %>' type="text" value="<%= tmpEmailPostfix %>" />
</aui:fieldset>