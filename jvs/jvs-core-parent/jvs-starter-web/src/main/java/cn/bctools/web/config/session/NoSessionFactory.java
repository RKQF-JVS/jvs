package cn.bctools.web.config.session;

import io.undertow.server.session.SessionManager;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.SessionManagerFactory;

/**
 * @Author: ZhuXiaoKang
 */
public class NoSessionFactory implements SessionManagerFactory {

    @Override
    public SessionManager createSessionManager(Deployment deployment) {
        return new NoSessionManager();
    }
}
