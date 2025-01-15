package moe.lyu.sapiblog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("moe.lyu.sapiblog.mapper")
public class SApiBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(SApiBlogApplication.class, args);
    }

}
