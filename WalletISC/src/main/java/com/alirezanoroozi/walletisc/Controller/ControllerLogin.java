package com.alirezanoroozi.walletisc.Controller;

import com.alirezanoroozi.walletisc.Entity.Person;

import com.alirezanoroozi.walletisc.Service.ServiceLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ControllerLogin {
    @Autowired
    private ServiceLogin serviceLogin;
   private Person person;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Person request)  {
            if (request != null) { // بررسی وجود کاربر
               return serviceLogin.login(request);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }
      }
}