package com.alirezanoroozi.walletisc.Service;

import com.alirezanoroozi.walletisc.Entity.Person;
import com.alirezanoroozi.walletisc.Repository.RepositoryPerson;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ServiceLogin {
    @Autowired
    private RepositoryPerson repositoryPerson;
    @Autowired
    private HttpSession httpSession;
    public boolean authenticate(String username, String password) {
        Person user = repositoryPerson.findByNationalId(username);
        return user != null && user.getMobileNumber().equals(password);
    }
    public ResponseEntity<String> login(Person request){

      Person  person = repositoryPerson.findByNationalId(request.getNationalId());
        if (person != null && person.getMobileNumber().equals(request.getMobileNumber())) { // بررسی وجود کاربر
            httpSession.setAttribute("userId", person.getId());
            httpSession.setAttribute("isAuthenticated", true);
            httpSession.setAttribute("role",person.getUserStatus());
            httpSession.setMaxInactiveInterval(30 * 60); // 30 دقیقه = 1800 ثانیه
            System.out.println("Login successful");
            return ResponseEntity.ok("Login successful");
        } else {
            httpSession.setAttribute("userId", "null");
            httpSession.setAttribute("isAuthenticated", false);
            httpSession.setAttribute("role","null");
            httpSession.setMaxInactiveInterval(30 * 60); // 30 دقیقه = 1800 ثانیه
            System.out.println("User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
    }
}

