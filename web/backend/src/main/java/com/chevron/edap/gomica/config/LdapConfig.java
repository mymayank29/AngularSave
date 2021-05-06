package com.chevron.edap.gomica.config;


import com.chevron.edap.gomica.security.LdapUserDetailsServiceWrapper;
import com.chevron.edap.gomica.security.dto.UserDetailsWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.util.Arrays;
import java.util.Collection;

@Configuration
public class LdapConfig {
    @Autowired
    private ConfigurationProvider configurationProvider;


    @Bean(name = "ldapContextSource")
    public DefaultSpringSecurityContextSource getDefaultSpringSecurityContextSource() {

        DefaultSpringSecurityContextSource defaultSpringSecurityContextSource = new DefaultSpringSecurityContextSource(
                Arrays.asList(new String[]{configurationProvider.getLdapProviderUrl(), configurationProvider.getLdapProviderUrl1(),
                        configurationProvider.getLdapProviderUrl2(), configurationProvider.getLdapProviderUrl3()}),
                configurationProvider.getLdapBaseDn());
        defaultSpringSecurityContextSource.setUserDn(configurationProvider.getLdapUserDn());
        defaultSpringSecurityContextSource.setPassword(configurationProvider.getLdapPassword());
        defaultSpringSecurityContextSource.setBase(configurationProvider.getLdapContextBase());
        defaultSpringSecurityContextSource.setReferral("follow");
        defaultSpringSecurityContextSource.setPooled(true);

        return defaultSpringSecurityContextSource;
    }

    @Bean(name = "userSearch")
    public FilterBasedLdapUserSearch getFilterBasedLdapUserSearch() {
        String searchBase = "";
        BaseLdapPathContextSource contextSource = getDefaultSpringSecurityContextSource();
        FilterBasedLdapUserSearch filterBasedLdapUserSearch = new FilterBasedLdapUserSearch(searchBase,
                configurationProvider.getLdapUserSearchFilter(), contextSource);
        filterBasedLdapUserSearch.setSearchSubtree(true);
        return filterBasedLdapUserSearch;
    }


    @Bean(name = "defaultLdapAuthoritiesPopulator")
    public DefaultLdapAuthoritiesPopulator getDefaultLdapAuthoritiesPopulator() {
        BaseLdapPathContextSource contextSource = getDefaultSpringSecurityContextSource();
        DefaultLdapAuthoritiesPopulator defaultLdapAuthoritiesPopulator = new DefaultLdapAuthoritiesPopulator(contextSource,
                configurationProvider.getLdapGroupSearchBase());
        defaultLdapAuthoritiesPopulator.setSearchSubtree(true);
        defaultLdapAuthoritiesPopulator.setIgnorePartialResultException(true);
        defaultLdapAuthoritiesPopulator.setGroupSearchFilter(configurationProvider.getLdapGroupSearchFilter());
        defaultLdapAuthoritiesPopulator.setConvertToUpperCase(false);
        defaultLdapAuthoritiesPopulator.setRolePrefix("");
        return defaultLdapAuthoritiesPopulator;
    }

    @Bean
    public UserDetailsContextMapper getUserDetailsContextMapper() {
        return new LdapUserDetailsMapper() {
            @Override
            public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
                UserDetails details = super.mapUserFromContext(ctx, username, authorities);
                UserDetailsWrapper userDetailsWrapper = new UserDetailsWrapper(details);
                userDetailsWrapper.setDisplayName(ctx.getStringAttribute("displayName"));
                return userDetailsWrapper;
            }
        };
    }
    
    @Bean(name = "ldapUserDetailsService")
    public LdapUserDetailsService getLdapUserDetailsService() {
        FilterBasedLdapUserSearch filterBasedLdapUserSearch = getFilterBasedLdapUserSearch();
        DefaultLdapAuthoritiesPopulator defaultLdapAuthoritiesPopulator = getDefaultLdapAuthoritiesPopulator();
        UserDetailsContextMapper userDetailsContextMapper = getUserDetailsContextMapper();

        LdapUserDetailsService ldapUserDetailsService = new LdapUserDetailsServiceWrapper(filterBasedLdapUserSearch, defaultLdapAuthoritiesPopulator);
        ldapUserDetailsService.setUserDetailsMapper(userDetailsContextMapper);

        return ldapUserDetailsService;
    }

    @Bean(name = "ldapTemplate")
    public LdapTemplate ldapTemplate() {
        LdapTemplate ldapTemplate = new LdapTemplate(getDefaultSpringSecurityContextSource());
        ldapTemplate.setIgnorePartialResultException(true);
        return ldapTemplate;
    }
}
