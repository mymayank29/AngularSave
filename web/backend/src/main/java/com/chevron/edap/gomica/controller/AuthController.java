package com.chevron.edap.gomica.controller;


import com.chevron.edap.gomica.security.IAuthService;
import com.chevron.edap.gomica.security.dto.User;
//import com.chevron.edap.gomica.service.LdapUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

//    private static Logger LOGGER = LoggerFactory.getLogger(EditingController.class);

    @Autowired
    private IAuthService authService;

    @Autowired
    @Qualifier("ldapUserDetailsService")
    private UserDetailsService ldapUserDetailsService;

    @PreAuthorize("hasPermission('ACCESS', '')")
    @RequestMapping(value = "/whoami", method = RequestMethod.GET)
    @ResponseBody
    public User whoAmI(){
        return authService.whoAmI();
    }

}
