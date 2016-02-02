<%--
/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>
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