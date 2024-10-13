package com.alirezanoroozi.walletisc.Controller;
import com.alirezanoroozi.walletisc.Entity.Person;
import com.alirezanoroozi.walletisc.Service.ServiceLogin;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

    @SpringBootTest
    @AutoConfigureMockMvc
    public class TestControllerLogin  {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ServiceLogin serviceLogin;

        @Autowired
        private HttpSession httpSession;

        @BeforeEach
        public void setUp() {
            // تنظیمات اولیه قبل از هر تست
        }


        @Test
        public void testLoginSuccess() throws Exception {
            // داده‌های ورودی به صورت JSON ذر این قسمت دیتای ورودی باید در دیتابیس باشد پس اول ثبت نام کنید
            String jsonRequest = "{\"nationalId\": \"3958852221\", \"mobileNumber\": \"09029656201\"}";


            // ارسال درخواست POST و بررسی پاسخ
            mockMvc.perform(post("/api/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Login successful"));
        }

        @Test
        public void testLoginUnauthorized() throws Exception {
        // ارسال کد ملی و شماره ای که در در پایگاه داده نیست
            String jsonRequest = "{\"nationalId\": \"3958852222\", \"mobileNumber\": \"09029656202\"}";

            // ارسال درخواست POST و بررسی پاسخ
            mockMvc.perform(post("/api/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string("Login successful"));
        }

    }