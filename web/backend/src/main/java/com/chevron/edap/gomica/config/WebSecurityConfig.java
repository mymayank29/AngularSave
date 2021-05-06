package com.chevron.edap.gomica.config;

import com.chevron.edap.gomica.security.local.LocalAuthenticationProvider;
import com.chevron.edap.gomica.security.local.LocalUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.kerberos.authentication.KerberosAuthenticationProvider;
import org.springframework.security.kerberos.authentication.KerberosServiceAuthenticationProvider;
import org.springframework.security.kerberos.authentication.sun.GlobalSunJaasKerberosConfig;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosClient;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosTicketValidator;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.kerberos.web.authentication.SpnegoEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@Import(LdapConfig.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Autowired
    private ConfigurationProvider configurationProvider;

    @Autowired
    @Qualifier("ldapUserDetailsService")
    private UserDetailsService ldapUserDetailsService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.debug(configurationProvider.isSpnegoDebugEnabled());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(spnegoEntryPoint())
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(
                        spnegoAuthenticationProcessingFilter(authenticationManagerBean()),
                        BasicAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        GlobalSunJaasKerberosConfig globalConfig = new GlobalSunJaasKerberosConfig();
        globalConfig.setDebug(configurationProvider.isSpnegoDebugEnabled());
//        globalConfig.setDebug(true);
        String kerberosConfigLocation = System.getProperty("java.security.krb5.conf", configurationProvider.getKrb5ConfLocation());
        globalConfig.setKrbConfLocation(kerberosConfigLocation);
        globalConfig.afterPropertiesSet();
        if (configurationProvider.getLocalRoles() != null && configurationProvider.getLocalUser() != null) {
            auth.authenticationProvider(localAuthenticationProvider());
        }
        auth.authenticationProvider(kerberosAuthenticationProvider())
            .authenticationProvider(kerberosServiceAuthenticationProvider());
    }

    protected AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
                httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/403");
            }
        };
    }

    @Bean
    public LocalAuthenticationProvider localAuthenticationProvider() {
        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(configurationProvider.getLocalUser(), localUserDetailsService());
        return provider;
    }

    @Bean
    public LocalUserDetailsService localUserDetailsService() {
        return new LocalUserDetailsService(configurationProvider.getLocalRoles());
    }

    @Bean
    public UserDetailsService authorizationService() {
        return ldapUserDetailsService;
    }

    @Bean
    public KerberosAuthenticationProvider kerberosAuthenticationProvider() {
        KerberosAuthenticationProvider provider =
                new KerberosAuthenticationProvider();
        SunJaasKerberosClient client = new SunJaasKerberosClient();
        client.setDebug(configurationProvider.isSpnegoDebugEnabled());
        provider.setKerberosClient(client);
        provider.setUserDetailsService(authorizationService());
        return provider;
    }

    @Bean
    public SpnegoEntryPoint spnegoEntryPoint() {
        return new SpnegoEntryPoint("/spnego");
    }

    public SpnegoAuthenticationProcessingFilter spnegoAuthenticationProcessingFilter(AuthenticationManager authenticationManager) {
        SpnegoAuthenticationProcessingFilter filter =
                new SpnegoAuthenticationProcessingFilter();
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Bean
    public KerberosServiceAuthenticationProvider kerberosServiceAuthenticationProvider() {
        KerberosServiceAuthenticationProvider provider =
                new KerberosServiceAuthenticationProvider();
        provider.setTicketValidator(sunJaasKerberosTicketValidator());
        provider.setUserDetailsService(authorizationService());
        return provider;
    }

    @Bean
    public SunJaasKerberosTicketValidator sunJaasKerberosTicketValidator() {

        String principal = configurationProvider.getKerberosPrincipal();
        String keyTabLoaction = configurationProvider.getKerberosKeyTabLocation();
        SunJaasKerberosTicketValidator ticketValidator =
                new SunJaasKerberosTicketValidator();
        ticketValidator.setServicePrincipal(principal);
        ticketValidator.setKeyTabLocation(new FileSystemResource(keyTabLoaction));
        ticketValidator.setDebug(configurationProvider.isSpnegoDebugEnabled());
        return ticketValidator;
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(false);
        return loggingFilter;
    }
}
