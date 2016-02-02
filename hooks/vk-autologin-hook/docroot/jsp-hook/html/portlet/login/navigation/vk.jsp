<%@ include file="/html/portlet/login/init.jsp" %>
<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page import="com.liferay.util.portlet.PortletProps"%>

<%
boolean showVKConnectIcon = false;

if (PrefsPropsUtil.getBoolean(themeDisplay.getCompanyId(), "VK_AUTH_ENABLED", false)) {
	showVKConnectIcon = true;
}
%>


<%
String vkAuthRedirectURL = PrefsPropsUtil.getString(themeDisplay.getCompanyId(),"VK_AUTH_REDIRECT_URL");

String vkAuthURL = PrefsPropsUtil.getString(themeDisplay.getCompanyId(),"VK_AUTH_URL", "https://oauth.vk.com/authorize");
vkAuthURL = HttpUtil.addParameter(vkAuthURL, "client_id", PrefsPropsUtil.getString(themeDisplay.getCompanyId(), "VK_AUTH_CLIENT_ID", StringPool.BLANK));
vkAuthURL = HttpUtil.addParameter(vkAuthURL, "redirect_uri", vkAuthRedirectURL);
vkAuthURL = HttpUtil.addParameter(vkAuthURL, "scope", "offline,email");
vkAuthURL = HttpUtil.addParameter(vkAuthURL, "response_type", "code");

String taglibOpenVKConnectLoginWindow = "javascript:var vkConnectLoginWindow = window.open('" + vkAuthURL.toString() + "','vk', 'align=center,directories=no,height=560,location=no,menubar=no,resizable=yes,scrollbars=yes,status=no,toolbar=no,width=1000'); void(''); vkConnectLoginWindow.focus();";
%>
<c:if test="<%= showVKConnectIcon %>">

	<li class="" role="presentation">
		<a class=" taglib-icon" role="menuitem" 
			href="<%= taglibOpenVKConnectLoginWindow %>">

			<img alt="" src="/vk-autologin-hook/images/vkontakte.png"></img>
			<span class="taglib-text ">VK</span>
		</a>
	</li>
</c:if>
