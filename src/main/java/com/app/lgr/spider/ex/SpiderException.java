package com.app.lgr.spider.ex;

/**
 * User: hzwangxx
 * Date: 14-7-27
 * Time: 11:07
 */
public class SpiderException extends Exception{
    public SpiderException() {
    }

    public SpiderException(String message) {
        super(message);
    }

    public SpiderException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpiderException(Throwable cause) {
        super(cause);
    }
}
