package com.chevron.edap.gomica.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;

public class LdapUserDetailsServiceWrapper extends LdapUserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserDetailsService.class);

    public LdapUserDetailsServiceWrapper(LdapUserSearch userSearch) {
        super(userSearch);
    }

    public LdapUserDetailsServiceWrapper(LdapUserSearch userSearch, LdapAuthoritiesPopulator authoritiesPopulator) {
        super(userSearch, authoritiesPopulator);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        long startTime = System.currentTimeMillis();
        String searchName = username.contains("@") ? username.split("@")[0] : username;
        UserDetails result = super.loadUserByUsername(searchName);
        long processingTime = System.currentTimeMillis() - startTime;
        LOGGER.info("LDAP lookup time, ms = " + processingTime);
        return result;
    }
}
