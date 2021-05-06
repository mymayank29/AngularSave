package com.chevron.edap.gomica.security.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

public class UserDetailsWrapper implements UserDetails, Serializable {

    private UserDetails origin;
    private String displayName;

    public UserDetailsWrapper(UserDetails origin) {
        this.origin = origin;
    }

    public UserDetails getOrigin() {
        return origin;
    }

    public void setOrigin(UserDetails origin) {
        this.origin = origin;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return origin.getAuthorities();
    }

    @Override
    public String getPassword() {
        return origin.getPassword();
    }

    @Override
    public String getUsername() {
        return origin.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return origin.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return origin.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return origin.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return origin.isEnabled();
    }
}
