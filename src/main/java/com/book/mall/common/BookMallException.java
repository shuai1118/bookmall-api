
package com.book.mall.common;

public class BookMallException extends RuntimeException {

    public BookMallException() {
    }

    public BookMallException(String message) {
        super(message);
    }

    /**
     * 丢出一个异常
     *
     * @param message
     */
    public static void fail(String message) {
        throw new BookMallException(message);
    }

}
