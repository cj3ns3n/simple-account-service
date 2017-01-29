package org.jensens.service.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class CustomErrorController implements ErrorController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/error")
    public String error(HttpServletRequest request, HttpServletResponse response) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        String responseErrorMessage = String.valueOf(request.getAttribute(RequestDispatcher.ERROR_MESSAGE));

        return String.format("Error: %d; Session: %s; %s", response.getStatus(), requestAttributes.getSessionId(), responseErrorMessage);
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

}
