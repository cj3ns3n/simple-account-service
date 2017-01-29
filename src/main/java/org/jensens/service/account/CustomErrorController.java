package org.jensens.service.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class CustomErrorController implements ErrorController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/error")
    public String error(HttpServletRequest request, HttpServletResponse response) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        String responseErrorMessage = "Unknown Error";

        try {
            responseErrorMessage = IOUtils.toString(request.getReader());
        } catch (IOException iox) {
            log.error("Request: " + request.getRequestURL() + " raised " + iox);
        }

        return String.format("Error: %d; Session: %s; %s", response.getStatus(), requestAttributes.getSessionId(), responseErrorMessage);
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

}
