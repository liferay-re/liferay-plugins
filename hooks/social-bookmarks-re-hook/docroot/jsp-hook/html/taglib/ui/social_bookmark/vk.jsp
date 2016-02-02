<%@ include file="/html/taglib/ui/social_bookmark/init.jsp" %>

<%
String vkContentId = GetterUtil.getString((String)request.getAttribute("liferay-ui:social-bookmark:contentId"));
String vkApiId = PrefsPropsUtil.getString(company.getCompanyId(), "VK_AUTH_CLIENT_ID", StringPool.BLANK);
%>
<%if (Validator.isNotNull(vkApiId)) { %>
	<liferay-util:html-top outputKey="taglib_ui_social_bookmark_vk">
		<!-- Put this script tag to the <head> of your page -->
		<script type="text/javascript" src="<%= HttpUtil.getProtocol(request) %>://vk.com/js/api/openapi.js?121"></script>
	</liferay-util:html-top>
	
	<script type="text/javascript">
	  VK.init({apiId: <%= vkApiId %>, onlyWidgets: true});
	</script>
	<!-- Put this div tag to the place, where the Like block will be -->
	<div id="vk_like_<%= vkContentId %>"></div>
	<script type="text/javascript">
		VK.Widgets.Like('vk_like_<%= vkContentId %>', {type: "button"});
	</script>
<%} %>