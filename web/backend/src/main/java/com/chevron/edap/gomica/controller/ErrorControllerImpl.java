package com.chevron.edap.gomica.controller;

import javafx.application.Application;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Controller
public class ErrorControllerImpl implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public String error() {
        return "Error handling";
    }

    @RequestMapping(value = "/403", produces = "text/html;charset=UTF-8")
    public void forbidden(HttpServletResponse response) {
        response.setContentType("text/html");
        Resource resource = new ClassPathResource("error.html");
        try (PrintWriter writer = response.getWriter(); InputStream is = resource.getInputStream()) {
            String text = IOUtils.toString(is, StandardCharsets.UTF_8.name());
            writer.write(text);
            writer.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
