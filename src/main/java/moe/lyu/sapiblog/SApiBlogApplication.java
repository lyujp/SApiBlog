package moe.lyu.sapiblog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("moe.lyu.sapiblog.mapper")
@EnableAspectJAutoProxy
public class SApiBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(SApiBlogApplication.class, args);
    }

}
