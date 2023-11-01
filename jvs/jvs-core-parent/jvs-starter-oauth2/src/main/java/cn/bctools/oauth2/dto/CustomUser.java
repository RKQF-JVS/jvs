package cn.bctools.oauth2.dto;

import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.entity.dto.UserInfoDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author 
 */
public class CustomUser extends UserInfoDto implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        UserDto userDto = super.getUserDto();
        return userDto.getPassword();
    }

    @Override
    public String getUsername() {
        return super.getUserDto().getAccountName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !getUserDto().getCancelFlag();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonExpired();
    }

    /**
     * 判断是否过期
     *
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return isAccountNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return isAccountNonExpired();
    }
}
