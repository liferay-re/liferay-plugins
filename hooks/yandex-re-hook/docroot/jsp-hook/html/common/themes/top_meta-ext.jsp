<!-- Yandex WebMaster -->
<%
Group grp = null;

if (layout != null) {
	grp = layout.getGroup();
}
UnicodeProperties gTypeSettings = grp.getTypeSettingsProperties();
String yaWMId = gTypeSettings.getProperty(Sites.ANALYTICS_PREFIX + "yaWM");
%>
<c:if test="<%= Validator.isNotNull(yaWMId) %>">
	<meta content="<%= yaWMId %>" name="yandex-verification"/>
</c:if>