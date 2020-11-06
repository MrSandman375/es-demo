package com.renjie.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @Author Fan
 * @Date 2020/11/5
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class User {
    private String name;
    private int age;
}
