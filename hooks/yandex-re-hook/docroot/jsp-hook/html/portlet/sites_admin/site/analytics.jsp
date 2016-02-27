<%@ include file="/html/portlet/sites_admin/init.jsp" %>

<%
Group liveGroup = (Group)request.getAttribute("site.liveGroup");

UnicodeProperties groupTypeSettings = null;

if (liveGroup != null) {
	groupTypeSettings = liveGroup.getTypeSettingsProperties();
}
else {
	groupTypeSettings = new UnicodeProperties();
}
%>

<liferay-ui:error-marker key="errorSection" value="analytics" />

<h3><liferay-ui:message key="analytics" /></h3>

<%
String[] analyticsTypes = PrefsPropsUtil.getStringArray(company.getCompanyId(), PropsKeys.ADMIN_ANALYTICS_TYPES, StringPool.NEW_LINE);

for (String analyticsType : analyticsTypes) {
%>

	<c:choose>
		<c:when test='<%= StringUtil.equalsIgnoreCase(analyticsType, "google") %>'>

			<%
			String googleAnalyticsId = PropertiesParamUtil.getString(groupTypeSettings, request, "googleAnalyticsId");
			%>

			<aui:input helpMessage="set-the-google-analytics-id-that-will-be-used-for-this-set-of-pages" label="google-analytics-id" name="googleAnalyticsId" size="30" type="text" value="<%= googleAnalyticsId %>" />
		</c:when>
		<c:otherwise>

			<%
			String analyticsName = TextFormatter.format(analyticsType, TextFormatter.J);

			String analyticsScript = PropertiesParamUtil.getString(groupTypeSettings, request, Sites.ANALYTICS_PREFIX + analyticsType);
			%>

			<aui:input cols="60" helpMessage='<%= LanguageUtil.format(pageContext, "set-the-script-for-x-that-will-be-used-for-this-set-of-pages", analyticsName) %>' label="<%= analyticsName %>" name="<%= Sites.ANALYTICS_PREFIX + analyticsType %>" rows="15" type="textarea" value="<%= analyticsScript %>" wrap="soft" />
		</c:otherwise>
	</c:choose>

<%
}
%>
<!-- Yandex Metrika -->
<%
String yaMetrikaId = PropertiesParamUtil.getString(groupTypeSettings, request, Sites.ANALYTICS_PREFIX + "yaMetrika");
%>
<aui:input helpMessage="set-counter-id-for-ya-metrika" label="ya-metrika" name='<%= Sites.ANALYTICS_PREFIX + "yaMetrika" %>' size="30" type="text" value="<%= yaMetrikaId %>" />

<!-- Yandex WM -->
<%
String yaWMId = PropertiesParamUtil.getString(groupTypeSettings, request, Sites.ANALYTICS_PREFIX + "yaWM");
%>
<aui:input helpMessage="set-token-for-ya-wm" label="ya-wm" name='<%= Sites.ANALYTICS_PREFIX + "yaWM" %>' size="30" type="text" value="<%= yaWMId %>" />