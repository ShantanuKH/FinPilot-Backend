package com.shantanu.FinPilot.auth.testFile;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class temp {

    @GetMapping("not-found")
    public void test() throws Exception {
        throw new Exception();
    }
}
