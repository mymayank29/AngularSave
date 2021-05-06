package com.chevron.edap.gomica.security.local;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.kerberos.authentication.KerberosServiceRequestToken;

public class LocalAuthenticationProvider implements AuthenticationProvider {

    private String localUser;

    private UserDetailsService userDetailsService;

    public LocalAuthenticationProvider(String localUser, UserDetailsService userDetailsService) {
        this.localUser = localUser;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(localUser);
        KerberosServiceRequestToken responseAuth = new KerberosServiceRequestToken(
                userDetails, null,
                userDetails.getAuthorities(), null);
        return responseAuth;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return localUser != null;
    }
}
