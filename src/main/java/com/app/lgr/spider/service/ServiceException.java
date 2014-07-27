package com.app.lgr.spider.service;

/**
 * User: hzwangxx
 * Date: 14-7-27
 * Time: 11:07
 */
public class ServiceException extends Exception{
    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}