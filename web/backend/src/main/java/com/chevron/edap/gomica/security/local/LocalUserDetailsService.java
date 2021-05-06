package com.chevron.edap.gomica.security.local;

import com.chevron.edap.gomica.security.Role;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LocalUserDetailsService implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsService.class);


    private List<GrantedAuthority> localRoles;

    public LocalUserDetailsService(String localRoles) {
        if (StringUtils.isEmpty(localRoles)) {
            this.localRoles = Collections.emptyList();
        } else {
            this.localRoles = Arrays.stream(localRoles.split(",")).map(Role::getRoleFromBusinessName).filter(Objects::nonNull).map(role -> new SimpleGrantedAuthority(role.getLdapRole())).collect(Collectors.toSet()).stream().collect(Collectors.toList());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String name = Normalizer.normalize(username, Normalizer.Form.NFD);
        LOGGER.info("user name = {}", name);

        return new User(username, "notUsed", true, true, true, true, localRoles);
    }

}