package com.awbd.online_learning.controllers;

import com.awbd.online_learning.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)

    public ModelAndView handleResourceNotFoundException(HttpServletRequest request, ResourceNotFoundException ex) {
        logger.warn("Resource not found: URL [{}], Message [{}]", request.getRequestURL(), ex.getMessage());
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        modelAndView.addObject("errorMessage", ex.getMessage());
        modelAndView.addObject("url", request.getRequestURL().toString());
        return modelAndView;
    }

    // no controller handler is found for URL path
    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    public ModelAndView handleNoHandlerFoundException(HttpServletRequest request, org.springframework.web.servlet.NoHandlerFoundException ex) {
        logger.warn("No handler found for URL [{}], HTTP Method [{}]", ex.getRequestURL(), ex.getHttpMethod());
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        modelAndView.addObject("errorMessage", "The page you are looking for could not be found (No Handler).");
        modelAndView.addObject("url", ex.getRequestURL());
        modelAndView.addObject("exceptionTypeFromNoHandler", ex.getClass().getSimpleName());
        return modelAndView;
    }

    // static resource not found (CSS, JS, img)

    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNoResourceFoundException(HttpServletRequest request, NoResourceFoundException ex) {
        logger.warn("Static resource not found: URL [{}], Resource Path [{}]", request.getRequestURL(), ex.getResourcePath());
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        modelAndView.addObject("errorMessage", "The requested resource could not be found.");
        modelAndView.addObject("url", request.getRequestURL().toString());
        modelAndView.addObject("resourcePath", ex.getResourcePath());
        return modelAndView;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(HttpServletRequest request, AccessDeniedException ex) {
        logger.warn("Access Denied: URL [{}], User [{}], Message [{}]",
                request.getRequestURL(),
                request.getRemoteUser(),
                ex.getMessage());

        ModelAndView modelAndView = new ModelAndView("accessDenied");

        modelAndView.setStatus(HttpStatus.FORBIDDEN);
        modelAndView.addObject("errorMessage", "You do not have permission to access this resource.");
        modelAndView.addObject("url", request.getRequestURL().toString());
        return modelAndView;
    }


    //  for other RuntimeExceptions
    @ExceptionHandler(RuntimeException.class)

    public ModelAndView handleGenericRuntimeException(HttpServletRequest request, RuntimeException ex) {
        logger.error("Runtime Exception: URL [{}], Message [{}], Exception Type [{}]",
                request.getRequestURL(), ex.getMessage(), ex.getClass().getSimpleName(), ex); // Log the exception too
        ModelAndView modelAndView = new ModelAndView("error/general-error");
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        modelAndView.addObject("errorMessage", "An unexpected runtime error occurred: " + ex.getMessage());
        modelAndView.addObject("url", request.getRequestURL().toString());
        modelAndView.addObject("exceptionType", ex.getClass().getSimpleName());
        return modelAndView;
    }


    @ExceptionHandler(Exception.class)

    public ModelAndView handleGenericException(HttpServletRequest request, Exception ex) {

        if (ex instanceof ResourceNotFoundException ||
                ex instanceof org.springframework.web.servlet.NoHandlerFoundException ||
                ex instanceof NoResourceFoundException ||
                ex instanceof RuntimeException) {}

        logger.error("Generic Exception: URL [{}], Message [{}], Exception Type [{}]",
                request.getRequestURL(), ex.getMessage(), ex.getClass().getSimpleName(), ex);
        ModelAndView modelAndView = new ModelAndView("error/general-error");
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        modelAndView.addObject("errorMessage", "An unexpected error occurred. Please try again later.");
        modelAndView.addObject("url", request.getRequestURL().toString());
        modelAndView.addObject("exceptionType", ex.getClass().getSimpleName());
        return modelAndView;
    }
}