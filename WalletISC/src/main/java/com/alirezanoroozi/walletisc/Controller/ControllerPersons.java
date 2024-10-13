package com.alirezanoroozi.walletisc.Controller;

import com.alirezanoroozi.walletisc.Entity.Person;
import com.alirezanoroozi.walletisc.Service.ServicePerson;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/persons")

public class ControllerPersons {
    @Autowired
    ServicePerson servicePersons;
    @Autowired
    private Validator validator;
    @Autowired
    private HttpSession httpSession;

    @GetMapping
    public ResponseEntity<?> getAllPerson(HttpSession httpSession) {
        Object roleAttribute = httpSession.getAttribute("role");

        // بررسی اینکه آیا roleAttribute برابر null است یا خیر
        if ("admin".equals(roleAttribute)) {
            System.out.println("شما مدیر هستید");

        return ResponseEntity.ok(servicePersons.getAllPersons());
        } else {
            System.out.println("شما مدیر نیستید");
            // در صورتی که دسترسی نداشته باشد، پیام خطا را برمی‌گرداند
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("دسترسی ندارید");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getByIdPerson(@PathVariable Long id, HttpSession httpSession) {

        Object userId = httpSession.getAttribute("userId");
        Object role = httpSession.getAttribute("role");

        Boolean isAuthenticated = (Boolean) httpSession.getAttribute("isAuthenticated");

        if (role != null && role.equals("admin") && Boolean.TRUE.equals(isAuthenticated) )
         {
            return ResponseEntity.ok(servicePersons.getPersonById(id));
          }
        if (userId != null && role.equals("user") &&userId.equals(id) && Boolean.TRUE.equals(isAuthenticated))   // بررسی اینکه userId و isAuthenticated معتبر هستند
        {
            return ResponseEntity.ok(servicePersons.getPersonById(id));                   // اگر کاربر مجاز باشد، اطلاعات شخص را برمی‌گرداند
        }
        else {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "دسترسی ندارید");                                       // اگر کاربر مجاز نباشد، یک JSON با متغیر error برمی‌گرداند


        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteByIdPerson(@PathVariable Long id){
        Object userId = httpSession.getAttribute("userId");
        Object role = httpSession.getAttribute("role");
        Boolean isAuthenticated = (Boolean) httpSession.getAttribute("isAuthenticated");

        if (role != null && role.equals("admin") && Boolean.TRUE.equals(isAuthenticated)) {
            servicePersons.deletePersonById(id);
            return ResponseEntity.ok().build(); // پاسخ 200 OK
        }
        if (userId != null && userId.equals(id) && Boolean.TRUE.equals(isAuthenticated)) {
            servicePersons.deletePersonById(id);
            return ResponseEntity.ok().build(); // پاسخ 200 OK
        }

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "دسترسی ندارید");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

 @PostMapping
 public ResponseEntity<?> createPerson(@Valid @RequestBody Person person, BindingResult bindingResult) {
     if (bindingResult.hasErrors()) {
         // اگر خطا وجود داشته باشد، پیام‌های خطا را برمی‌گردانیم
         List<String> errors = bindingResult.getAllErrors()
                 .stream()
                 .map(DefaultMessageSourceResolvable::getDefaultMessage)
                 .collect(Collectors.toList());
         return ResponseEntity.badRequest().body(errors);
     }

     // پردازش شیء person
     servicePersons.savePerson(person);
     return ResponseEntity.ok(person);
 }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePerson(@PathVariable Long id, @Valid @RequestBody Person person) {

        Object userId = httpSession.getAttribute("userId");
        Object role = httpSession.getAttribute("role");
        Boolean isAuthenticated = (Boolean) httpSession.getAttribute("isAuthenticated");

        if (role != null && role.equals("admin") && Boolean.TRUE.equals(isAuthenticated) )
        {
            return ResponseEntity.ok(servicePersons.updatePerson(person,id));

        }
        if (userId != null && userId.equals(id) && Boolean.TRUE.equals(isAuthenticated))   // بررسی اینکه userId و isAuthenticated معتبر هستند
        {
            return ResponseEntity.ok(servicePersons.updatePerson(person,id));
            // اگر کاربر مجاز باشد، اطلاعات شخص را برمی‌گرداند
        }
        else {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "دسترسی ندارید");                                       // اگر کاربر مجاز نباشد، یک JSON با متغیر error برمی‌گرداند
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);}
    }

}




