package ru.liferay.hook.action;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import com.liferay.portal.kernel.struts.BaseStrutsPortletAction;
import com.liferay.portal.kernel.struts.StrutsPortletAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.GroupServiceUtil;
import com.liferay.portlet.sites.util.Sites;

public class CustomEditGroupAction extends BaseStrutsPortletAction {

	@Override
	public void processAction(StrutsPortletAction originalStrutsPortletAction,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		long liveGroupId = ParamUtil.getLong(actionRequest, "liveGroupId");
		if (liveGroupId <= 0) {
			//just skip it
			return;
		}
		Group liveGroup = GroupLocalServiceUtil.getGroup(liveGroupId);
		UnicodeProperties typeSettingsProperties = liveGroup.getTypeSettingsProperties();
		//ya metrika
		String yaMetrika = ParamUtil.getString(
				actionRequest, Sites.ANALYTICS_PREFIX + "yaMetrika",
				typeSettingsProperties.getProperty(Sites.ANALYTICS_PREFIX + "yaMetrika"));

		typeSettingsProperties.setProperty(
					Sites.ANALYTICS_PREFIX + "yaMetrika", yaMetrika);
		//ya wm
		String yaWM = ParamUtil.getString(
				actionRequest, Sites.ANALYTICS_PREFIX + "yaWM",
				typeSettingsProperties.getProperty(Sites.ANALYTICS_PREFIX + "yaWM"));

		typeSettingsProperties.setProperty(
					Sites.ANALYTICS_PREFIX + "yaWM", yaWM);
		
		if (Validator.isNotNull(yaMetrika) || Validator.isNotNull(yaWM)) {
			liveGroup = GroupServiceUtil.updateGroup(
					liveGroup.getGroupId(), typeSettingsProperties.toString());
		}
		
		
		originalStrutsPortletAction.processAction(originalStrutsPortletAction, portletConfig, actionRequest,
				actionResponse);

	}

	@Override
	public String render(StrutsPortletAction originalStrutsPortletAction,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		return originalStrutsPortletAction.render(portletConfig, renderRequest, renderResponse);
	}

}