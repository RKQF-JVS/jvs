package cn.bctools.web.config.session;

import io.undertow.servlet.ServletExtension;
import io.undertow.servlet.api.DeploymentInfo;

import javax.servlet.ServletContext;

/**
 * @Author: ZhuXiaoKang
 */
public class NoSessionHandlerExtension implements ServletExtension {

    @Override
    public void handleDeployment(DeploymentInfo deploymentInfo, ServletContext servletContext) {
        deploymentInfo.setSessionManagerFactory(new NoSessionFactory());
    }
}
