package cn.bctools.web.config.session;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.*;

import java.util.Set;

/**
 * @Author: ZhuXiaoKang
 */
public class NoSessionManager implements SessionManager, SessionManagerStatistics {

    @Override
    public String getDeploymentName() {
        return null;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Session createSession(HttpServerExchange serverExchange, SessionConfig sessionCookieConfig) {
        return new SessionImpl();
    }

    @Override
    public Session getSession(HttpServerExchange serverExchange, SessionConfig sessionCookieConfig) {
        return null;
    }

    @Override
    public Session getSession(String sessionId) {
        return null;
    }

    @Override
    public void registerSessionListener(SessionListener listener) {

    }

    @Override
    public void removeSessionListener(SessionListener listener) {

    }

    @Override
    public void setDefaultSessionTimeout(int timeout) {

    }

    @Override
    public Set<String> getTransientSessions() {
        return null;
    }

    @Override
    public Set<String> getActiveSessions() {
        return null;
    }

    @Override
    public Set<String> getAllSessions() {
        return null;
    }

    @Override
    public SessionManagerStatistics getStatistics() {
        return null;
    }

    @Override
    public long getCreatedSessionCount() {
        return 0;
    }

    @Override
    public long getMaxActiveSessions() {
        return 0;
    }

    @Override
    public long getActiveSessionCount() {
        return 0;
    }

    @Override
    public long getExpiredSessionCount() {
        return 0;
    }

    @Override
    public long getRejectedSessions() {
        return 0;
    }

    @Override
    public long getMaxSessionAliveTime() {
        return 0;
    }

    @Override
    public long getAverageSessionAliveTime() {
        return 0;
    }

    @Override
    public long getStartTime() {
        return 0;
    }

    private static class SessionImpl implements Session {
        @Override
        public String getId() {
            return null;
        }

        @Override
        public void requestDone(HttpServerExchange serverExchange) {

        }

        @Override
        public long getCreationTime() {
            return 0;
        }

        @Override
        public long getLastAccessedTime() {
            return 0;
        }

        @Override
        public void setMaxInactiveInterval(int interval) {

        }

        @Override
        public int getMaxInactiveInterval() {
            return 0;
        }

        @Override
        public Object getAttribute(String name) {
            return null;
        }

        @Override
        public Set<String> getAttributeNames() {
            return null;
        }

        @Override
        public Object setAttribute(String name, Object value) {
            return null;
        }

        @Override
        public Object removeAttribute(String name) {
            return null;
        }

        @Override
        public void invalidate(HttpServerExchange exchange) {

        }

        @Override
        public SessionManager getSessionManager() {
            return null;
        }

        @Override
        public String changeSessionId(HttpServerExchange exchange, SessionConfig config) {
            return null;
        }
    }
}
