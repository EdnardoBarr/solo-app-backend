package ednardo.api.soloapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloRestController {
    @GetMapping(path = "/user")
    public ResponseEntity helloUser() {
        return ResponseEntity.ok( "Hello User");
    }

    @GetMapping("/admin")
    public String helloAdmin() {
        return "Hello Admin";
    }
}
