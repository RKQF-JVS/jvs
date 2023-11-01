package cn.bctools.auth.component.other;

import cn.bctools.oauth2.dto.CustomUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

/**
 * @author guojing
 */
public class OtherAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    @Getter
    @Setter
    private Object principal;
    @Getter
    @Setter
    private String otherParameter;
    @Getter
    @Setter
    private String clientId;

    public OtherAuthenticationToken() {
        super(null);
    }

    public OtherAuthenticationToken(String otherParameter, String clientId) {
        super(null);
        this.otherParameter = otherParameter;
        this.clientId = clientId;
    }

    public OtherAuthenticationToken(String otherParameter) {
        super(null);
        this.otherParameter = otherParameter;
    }

    public OtherAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public String getName() {
        CustomUser principal = (CustomUser) this.principal;
        return principal.getUserDto().getAccountName();
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
