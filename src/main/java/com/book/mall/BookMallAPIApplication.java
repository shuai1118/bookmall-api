
package com.book.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan("com.book.mall.dao")
@SpringBootApplication
public class BookMallAPIApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookMallAPIApplication.class, args);
    }

}
