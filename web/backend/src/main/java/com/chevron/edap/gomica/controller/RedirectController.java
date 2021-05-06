package com.chevron.edap.gomica.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RedirectController {

//    @PreAuthorize("hasPermission('ACCESS', '')")
    @RequestMapping("/spnego")
    public String spnego() {
        return "index.html";
    }

    @PreAuthorize("hasPermission('ACCESS', '')")
    @RequestMapping("/")
    public String index() {
        return "index.html";
    }

    @PreAuthorize("hasPermission('ACCESS', '')")
    @RequestMapping("/invoices")
    public String redirect() {
        return "index.html";
    }
}
