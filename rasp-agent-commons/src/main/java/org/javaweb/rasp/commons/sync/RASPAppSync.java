package org.javaweb.rasp.commons.sync;

import org.javaweb.rasp.commons.config.RASPAppProperties;
import org.javaweb.rasp.commons.config.RASPPropertiesConfiguration;

import java.util.List;

import static org.javaweb.rasp.commons.RASPRequestEnv.getAppConfigList;
import static org.javaweb.rasp.commons.config.RASPConfiguration.MODULES_LOGGER;
import static org.javaweb.rasp.commons.constants.RASPAppConstants.DEFAULT_APP_ID;
import static org.javaweb.rasp.loader.AgentConstants.AGENT_NAME;

public abstract class RASPAppSync extends RASPCloudSync {

	public abstract void appSync(RASPPropertiesConfiguration<RASPAppProperties> appConfig);

	@Override
	public void sync() {
		List<RASPPropertiesConfiguration<RASPAppProperties>> appConfigList = getAppConfigList();

		for (RASPPropertiesConfiguration<RASPAppProperties> appConfig : appConfigList) {
			String appId = appConfig.getRaspProperties().getAppID();

			if (DEFAULT_APP_ID.equals(appId)) {
				MODULES_LOGGER.debug("{}应用ID未注册，暂时无法同步!", AGENT_NAME);
				continue;
			}

			appSync(appConfig);
		}
	}

}
