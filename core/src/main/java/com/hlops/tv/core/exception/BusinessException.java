package com.hlops.tv.core.exception;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 6/17/15
 * Time: 2:09 PM
 */
public class BusinessException extends Exception {

    private static final long serialVersionUID = 5947219424499268300L;

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

}
