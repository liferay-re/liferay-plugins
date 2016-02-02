package ru.liferay.vk.events;


import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SimpleAction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.model.User;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoColumnConstants;
import com.liferay.portlet.expando.model.ExpandoTable;
import com.liferay.portlet.expando.model.ExpandoTableConstants;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil;

import ru.liferay.vk.WebKeys;


public class StartUpAction extends SimpleAction {
    private static final Log log = LogFactoryUtil.getLog(StartUpAction.class);
    
    @Override
    public void run(String[] ids) throws ActionException {
    	log.info("VK Auto login hook StartUp Action");
		try {
			for (String companyId : ids) {
				doRun(GetterUtil.getLong(companyId));
			}
		} catch (Exception e) {
			log.error("Initialization failed", e);
			throw new ActionException(e);
		}
    }
    
    protected void doRun(long companyId) throws Exception {
		createCustomAttributes(companyId);
	}
    
    private void createCustomAttributes(long companyId) throws PortalException, SystemException {
    	// create expando table for user
        ExpandoTable userExandoTable = null;
        try {
        	userExandoTable = ExpandoTableLocalServiceUtil.getTable(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME);
        } catch (Exception ex) {}
        
        if (userExandoTable == null) {
        	userExandoTable = ExpandoTableLocalServiceUtil.addTable(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME);
        }
        //create vk_id field
        ExpandoColumn column = null;
        try {
        	column = ExpandoColumnLocalServiceUtil.getColumn(userExandoTable.getTableId(), WebKeys.VK_ID);
        } catch (Exception ex) {}
        if (column == null) {
                ExpandoColumnLocalServiceUtil.addColumn(userExandoTable.getTableId(), WebKeys.VK_ID, ExpandoColumnConstants.STRING);
        }
    }
}
