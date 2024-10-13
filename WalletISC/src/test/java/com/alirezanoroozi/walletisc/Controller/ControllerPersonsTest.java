package com.alirezanoroozi.walletisc.Controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.alirezanoroozi.walletisc.Repository.RepositoryPerson;
import com.google.gson.Gson;
import static org.hamcrest.Matchers.containsString;
import com.alirezanoroozi.walletisc.Controller.ControllerPersons;
import com.alirezanoroozi.walletisc.Entity.Person;
import com.alirezanoroozi.walletisc.Service.ServicePerson;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.*;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.http.HttpSession;


import java.text.SimpleDateFormat;
import java.util.*;

public class ControllerPersonsTest {

        @InjectMocks
        private ControllerPersons controllerPersons;
        @Mock
        private RepositoryPerson personRepository;

        @Mock
        private ServicePerson servicePersons;

        @Mock
        private HttpSession httpSession;

        @Mock
        private Validator validator;
        @Mock
        private Gson getJsonString; // در صورتی که برای تبدیل JSON استفاده می‌شود

        private MockMvc mockMvc;
        private MockHttpSession session;
        private ObjectMapper objectMapper;

        @BeforeEach
        public void setup() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders.standaloneSetup(controllerPersons).build();
                session = new MockHttpSession();
                session.setAttribute("role", "admin");
                session.setAttribute("isAuthenticated", true);
                objectMapper = new ObjectMapper(); // برای تبدیل اشیاء به JSON
                session = new MockHttpSession();

        }

        @Test
        public void testGetAllPersons_AdminRole() throws Exception {
                Person person1 = new Person();
                person1.setId(10L);
                person1.setFirstName("Alireza");
                person1.setLastname("Noroozi");
                person1.setNationalId("0021694621");
                person1.setDateOfBirth(new Date(1234567890000L));
                person1.setGender("man");
                person1.setMilitaryStatus("exempt");
                person1.setMobileNumber("0021694621");
                person1.setEmail("Asdas@asdas.saas");
                person1.setUserStatus("admin");
                person1.setAccount(new HashSet<>());

                Person person2 = new Person();
                person2.setId(13L);
                person2.setFirstName("Ahmad");
                person2.setLastname("Ahmadi");
                person2.setNationalId("9371486031");
                person2.setDateOfBirth(new Date(1234567890000L));
                person2.setGender("man");
                person2.setMilitaryStatus("exempt");
                person2.setMobileNumber("09029606001");
                person2.setEmail("Asdas@assdas.saas");
                person2.setUserStatus("admin");
                person2.setAccount(new HashSet<>());

                List<Person> persons = Arrays.asList(person1, person2);
                // Mock کردن رفتار
                when(httpSession.getAttribute("role")).thenReturn("admin");
                when(servicePersons.getAllPersons()).thenReturn(persons);

                // ارسال درخواست و دریافت پاسخ
                String responseContent = mockMvc.perform(get("/api/persons")
                                .sessionAttr("role", "admin")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(10))

                        .andReturn()
                        .getResponse()
                        .getContentAsString();

                // چاپ داده‌های دریافتی
                System.out.println("Response JSON:");
                System.out.println(responseContent);
        }


        @Test
        public void testGetAllPersons_NonAdminRole() throws Exception {
                Person person1 = new Person();
                person1.setId(10L);
                person1.setFirstName("Alireza");
                person1.setLastname("Noroozi");
                person1.setNationalId("0021694621");
                person1.setDateOfBirth(new Date(1234567890000L));
                person1.setGender("man");
                person1.setMilitaryStatus("exempt");
                person1.setMobileNumber("0021694621");
                person1.setEmail("Asdas@asdas.saas");
                person1.setUserStatus("admin");
                person1.setAccount(new HashSet<>());

                Person person2 = new Person();
                person2.setId(13L);
                person2.setFirstName("Ahmad");
                person2.setLastname("Ahmadi");
                person2.setNationalId("9371486031");
                person2.setDateOfBirth(new Date(1234567890000L));
                person2.setGender("man");
                person2.setMilitaryStatus("exempt");
                person2.setMobileNumber("09029606001");
                person2.setEmail("Asdas@assdas.saas");
                person2.setUserStatus("admin");
                person2.setAccount(new HashSet<>());

                List<Person> persons = Arrays.asList(person1, person2);
                // Mock کردن رفتار
                when(httpSession.getAttribute("role")).thenReturn("admin");
                when(servicePersons.getAllPersons()).thenReturn(persons);

                // ارسال درخواست و دریافت پاسخ
                String responseContent = mockMvc.perform(get("/api/persons")
                                .sessionAttr("role", "user")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

                // چاپ داده‌های دریافتی
                System.out.println("Response JSON:");
                System.out.println(responseContent);
}
        @Test
        public void testGetByIdPerson_AdminRole() throws Exception {
                Long userId = 10L;
                session.setAttribute("userId", userId);
                session.setAttribute("role", "admin");
                session.setAttribute("isAuthenticated", true);
                // داده‌های نمونه
                Person person = new Person();
                person.setId(userId);
                person.setFirstName("Alireza");
                person.setLastname("Noroozi");
                person.setNationalId("0021694621");
                person.setDateOfBirth(new Date(1234567890000L));
                person.setGender("man");
                person.setMilitaryStatus("exempt");
                person.setMobileNumber("09029606001");
                person.setEmail("Asdas@asdas.saas");
                person.setUserStatus("admin");

                // شبیه‌سازی رفتار سرویس
                when(servicePersons.getPersonById(userId)).thenReturn(person);

                // ارسال درخواست GET و بررسی پاسخ
                mockMvc.perform(get("/api/persons/{id}", userId)
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(userId))
                        .andExpect(jsonPath("$.firstName").value("Alireza"))
                        .andExpect(jsonPath("$.lastname").value("Noroozi"));
        }

        @Test
        public void testGetByIdPerson_UserRole() throws Exception {
                Long userId = 10L;
                session.setAttribute("userId", userId);
                session.setAttribute("role", "user"); // user برای شبیه‌سازی کاربر عادی
                session.setAttribute("isAuthenticated", true);

                // داده‌های نمونه
                Person person = new Person();
                person.setId(userId);
                person.setFirstName("Alireza");
                person.setLastname("Noroozi");
                person.setNationalId("0021694621");
                person.setDateOfBirth(new Date(1234567890000L));
                person.setGender("man");
                person.setMilitaryStatus("exempt");
                person.setMobileNumber("09029606001");
                person.setEmail("Asdas@asdas.saas");
                person.setUserStatus("admin");

                // شبیه‌سازی رفتار سرویس
                when(servicePersons.getPersonById(userId)).thenReturn(person);

                // ارسال درخواست GET و بررسی پاسخ
                mockMvc.perform(get("/api/persons/{id}", userId)
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(userId))
                        .andExpect(jsonPath("$.firstName").value("Alireza"))
                        .andExpect(jsonPath("$.lastname").value("Noroozi"));
        }
        @Test
        public void testGetByIdPerson_Unauthorized() throws Exception {
                Long userId = 10L;
                session.setAttribute("userId", userId);
                session.setAttribute("role", "null");
                session.setAttribute("isAuthenticated", false); // کاربر احراز هویت نشده

                // ارسال درخواست GET و بررسی پاسخ
                mockMvc.perform(get("/api/persons/{id}", userId)
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden())
                        .andExpect(jsonPath("$.error").value("دسترسی ندارید"));
        }


        @Test
        public void testGetByIdPerson_AccessDeniedForOtherUser() throws Exception {
                Long userId = 10L; // شناسه کاربر احراز هویت شده
                Long otherUserId = 20L; // شناسه شخص دیگری که کاربر به آن دسترسی می‌خواهد
                session.setAttribute("userId", userId); // کاربر احراز هویت شده
                session.setAttribute("role", "user");// نقش کاریر

                // داده‌های نمونه
                Person person = new Person();
                person.setId(otherUserId);
                person.setFirstName("Ahmad");
                person.setLastname("Ahmadi");
                person.setNationalId("9371486031");
                person.setDateOfBirth(new Date(1234567890000L));
                person.setGender("man");
                person.setMilitaryStatus("exempt");
                person.setMobileNumber("09029606001");
                person.setEmail("Ahmad@asdas.saas");
                person.setUserStatus("user");

                // شبیه‌سازی رفتار سرویس
                when(servicePersons.getPersonById(otherUserId)).thenReturn(person);

                // ارسال درخواست GET و بررسی پاسخ
                mockMvc.perform(get("/api/persons/{id}", otherUserId)
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden()) // انتظار داریم که دسترسی ممنوع باشد
                        .andExpect(jsonPath("$.error").value("دسترسی ندارید")); // انتظار داریم پیام خطا برگردد
        }

        @Test
        public void testDeletePerson_AdminRole() throws Exception {
                Long personId = 1L;

                // Mock کردن رفتار
                when(httpSession.getAttribute("role")).thenReturn("admin");
                when(httpSession.getAttribute("isAuthenticated")).thenReturn(true);

                // ارسال درخواست و دریافت پاسخ
                mockMvc.perform(delete("/api/persons/{id}", personId)
                                .sessionAttr("userId", 2L) // کاربر دیگری که نمی‌تواند حذف کند
                                .sessionAttr("role", "admin") .
                                sessionAttr("isAuthenticated", true)// کاربر مدیر
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()); // انتظار داریم که پاسخ 200 باشد

                // اطمینان از اینکه متد حذف در سرویس فراخوانی شده است
                verify(servicePersons, times(1)).deletePersonById(personId);
        }
        @Test
        public void testDeletePerson_UserRole() throws Exception {
                Long personId = 1L;

                // Mock کردن رفتار
                when(httpSession.getAttribute("userId")).thenReturn(personId);
                when(httpSession.getAttribute("isAuthenticated")).thenReturn(true);
                when(httpSession.getAttribute("role")).thenReturn("user"); // کاربر عادی

                // ارسال درخواست و دریافت پاسخ
                mockMvc.perform(delete("/api/persons/{id}", personId)
                                .sessionAttr("userId", personId) // کاربر خود
                                .sessionAttr("isAuthenticated", true)// کاربر احراز هویت شده
                                .sessionAttr("isAuthenticated", "user")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()); // انتظار داریم که پاسخ 200 باشد

                // اطمینان از اینکه متد حذف در سرویس فراخوانی شده است
                verify(servicePersons, times(1)).deletePersonById(personId);
        }
        @Test
        public void testDeleteByIdPerson_Unauthorized() throws Exception {
                Long userId = 10L; // شناسه کاربر احراز هویت شده
                Long idToDelete = 20L; // شناسه‌ای که کاربر می‌خواهد حذف کند

                // مقداردهی به سشن
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("userId", userId); // تنظیم شناسه کاربر احراز هویت شده
                session.setAttribute("isAuthenticated", true); // کاربر احراز هویت شده
                session.setAttribute("role", null); // حذف نقش برای شبیه‌سازی کاربر عادی

                // ارسال درخواست DELETE و بررسی پاسخ
                mockMvc.perform(delete("/api/persons/{id}", idToDelete)
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden()); // انتظار داریم که پاسخ 403 Forbidden باشد
        }

        @Test
        public void testCreatePerson_ValidData() throws Exception {
                Person person = new Person();
                person.setFirstName("Alireza");
                person.setLastname("Noroozi");
                person.setNationalId("0021694621");
                person.setDateOfBirth(new Date(90, 0, 1)); // تاریخ تولد
                person.setGender("man");
                person.setMilitaryStatus("exempt");
                person.setMobileNumber("09029606001");
                person.setEmail("Asdas@asdas.saas");
                person.setUserStatus("admin");

                mockMvc.perform(post("/api/persons")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(person))) // تبدیل شیء به JSON
                        .andExpect(status().isOk()) // انتظار داریم که پاسخ 200 باشد
                        .andDo(result -> {
                                String response = result.getResponse().getContentAsString();
                                System.out.println("Response: " + response); // چاپ پاسخ موفق
                        });

                // اطمینان از اینکه متد ذخیره در سرویس فراخوانی شده است
                verify(servicePersons, times(1)).savePerson(any(Person.class));
        }
        @Test
        public void createPerson_WhenRequiredFiledIsEmpty_ShouldReturnBadRequest() throws Exception {
                Person person = new Person();
                person.setFirstName(""); // فیلد اجباری خالی است
                person.setLastname("");
                person.setNationalId("");
                person.setDateOfBirth(new Date(90, 0, 1)); // تاریخ تولد
                person.setGender("");
                person.setMilitaryStatus("");
                person.setMobileNumber("");
                person.setEmail("");

                // شبیه‌سازی اعتبارسنجی
                Set<ConstraintViolation<Person>> violations = new HashSet<>();
                ConstraintViolation<Person> violation = mock(ConstraintViolation.class);
                Path propertyPath = mock(Path.class);
                when(propertyPath.toString()).thenReturn("firstName"); // شبیه‌سازی مسیر
                when(violation.getPropertyPath()).thenReturn(propertyPath);
                when(violation.getMessage()).thenReturn("First name is required");
                when(violation.getMessage()).thenReturn("Last name is required");
                when(violation.getMessage()).thenReturn("National code is required");
                when(violation.getMessage()).thenReturn("Gender is required");
                when(violation.getMessage()).thenReturn("Military status is required for men over 18");
                when(violation.getMessage()).thenReturn("Mobile number is required");
                when(violation.getMessage()).thenReturn("Email is required");


                violations.add(violation);

                when(validator.validate(person)).thenReturn(violations);

                mockMvc.perform(post("/api/persons")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(person)) // تبدیل به JSON
                                .session(session)) // استفاده از جلسه شبیه‌سازی شده
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string(containsString("First name is required")))
                        .andExpect(content().string(containsString("National code is required")))
                        .andExpect(content().string(containsString("Gender is required")))
                        .andExpect(content().string(containsString("Military status is required for men over 18")))
                        .andExpect(content().string(containsString("Mobile number is required")))
                        .andExpect(content().string(containsString("Email is required")))

                        .andExpect(content().string(containsString("Last name is required")));
        }
        @Test
        public void createPerson_When_Email_AndNationalId_Gender_FiledIsNotTrue_MilitaryStatus_MobileNumber_isNotTrue_ShouldReturnBadRequest() throws Exception {
                Person person = new Person();
                person.setFirstName("Alireza");
                person.setLastname("noroozi");
                person.setNationalId("0022684622"); //کد ملی خارج از الگو
                person.setDateOfBirth(new Date(90, 0, 1)); // تاریخ تولد
                person.setGender("trans");//جنسیت خارج از الگو
                person.setMilitaryStatus("");//وضعیت سربازی خارج از الگو
                person.setMobileNumber("090260011111");//شماره موبایل خارج از الگو
                person.setEmail("ddsad@@.ssdad");//ایمیل خارج از الگو

                // شبیه‌سازی اعتبارسنجی
                Set<ConstraintViolation<Person>> violations = new HashSet<>();
                ConstraintViolation<Person> violation = mock(ConstraintViolation.class);
                Path propertyPath = mock(Path.class);
                when(violation.getPropertyPath()).thenReturn(propertyPath);
                when(violation.getMessage()).thenReturn("Invalid national code");
                when(violation.getMessage()).thenReturn("Gender must be either 'man' or 'woman'");
                when(violation.getMessage()).thenReturn("Military status is required for men over 18");
                when(violation.getMessage()).thenReturn("Mobile number must be 11 digits - 09123334444");
                when(violation.getMessage()).thenReturn("Email should be valid");


                violations.add(violation);
                when(validator.validate(person)).thenReturn(violations);

                mockMvc.perform(post("/api/persons")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(person)) // تبدیل به JSON
                                .session(session)) // استفاده از جلسه شبیه‌سازی شده
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string(containsString("Invalid national code")))
                        .andExpect(content().string(containsString("Gender must be either 'man' or 'woman'")))
                        .andExpect(content().string(containsString("Military status is required for men over 18")))
                        .andExpect(content().string(containsString("Mobile number must be 11 digits - 09123334444")))
                        .andExpect(content().string(containsString("Email should be valid")));
        }

        @Test
        public void createPerson_WhenMilitaryStatusManAgeIs18Above() throws Exception {
                Person person = new Person();
                person.setFirstName("Alireza");
                person.setLastname("noroozi");
                person.setNationalId("6594559672"); //کد ملی متابق الگو
                person.setDateOfBirth(new Date(21, 0, 1)); // تاریخ تولد
                person.setGender("man");//جنسیت متابق الگو
                person.setMilitaryStatus("");//وضعیت سربازی خارج از الگو    باید چیزی نوشته شود
                person.setMobileNumber("09026001111");//شماره موبایل ملی متابق الگو
                person.setEmail("ddsad@ssdad.com");//ایمیل متابق

                // شبیه‌سازی اعتبارسنجی
                Set<ConstraintViolation<Person>> violations = new HashSet<>();
                ConstraintViolation<Person> violation = mock(ConstraintViolation.class);
                Path propertyPath = mock(Path.class);
                when(violation.getPropertyPath()).thenReturn(propertyPath);
                when(violation.getMessage()).thenReturn("Military status is required for men over 18");

                violations.add(violation);
                when(validator.validate(person)).thenReturn(violations);

                mockMvc.perform(post("/api/persons")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(person)) // تبدیل به JSON
                                .session(session)) // استفاده از جلسه شبیه‌سازی شده
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string(containsString("Military status is required for men over 18")));
        }
        @Test
        public void createPerson_WhenMilitaryStatusManAgeIs_Not_18Above() throws Exception {
                Person person = new Person();
                person.setFirstName("Alireza");
                person.setLastname("noroozi");
                person.setNationalId("6594559672"); //کد ملی متابق الگو
                person.setDateOfBirth(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse("2022-10-04T10:27:05.000+00:00"));// سن زیر 18 سال
                person.setGender("man");//جنسیت متابق الگو
                person.setMilitaryStatus("");//وضعیت سربازی چون زیر 18 میباشد نیازی به نوشتن ندارد
                person.setMobileNumber("09026001111");//شماره موبایل ملی متابق الگو
                person.setEmail("ddsad@ssdad.com");//ایمیل متابق

                // شبیه‌سازی اعتبارسنجی
                Set<ConstraintViolation<Person>> violations = new HashSet<>();
                ConstraintViolation<Person> violation = mock(ConstraintViolation.class);
                Path propertyPath = mock(Path.class);
                when(violation.getPropertyPath()).thenReturn(propertyPath);
                when(violation.getMessage()).thenReturn(""); //اروری مشاهده نمیشود

                violations.add(violation);
                when(validator.validate(person)).thenReturn(violations);

                mockMvc.perform(post("/api/persons")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(person)) // تبدیل به JSON
                                .session(session)) // استفاده از جلسه شبیه‌سازی شده
                        .andExpect(status().isOk())//پاسخ درست
                        .andExpect(content().string(containsString("")));//اروری مشاهده نمیشود
        }

        @Test
        public void createPerson_WhenMilitaryStatusGenderIsWoman() throws Exception {
                Person person = new Person();
                person.setFirstName("Alireza");
                person.setLastname("noroozi");
                person.setNationalId("6594559672"); //کد ملی متابق الگو
                person.setDateOfBirth(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse("2002-10-04T10:27:05.000+00:00"));// سن بالای 18 سال
                person.setGender("woman");//جنسیت زن الگو
                person.setMilitaryStatus("");//وضعیت سربازی چون زن میابشد نیازی به نوشتن ندارد
                person.setMobileNumber("09026001111");//شماره موبایل ملی متابق الگو
                person.setEmail("ddsad@ssdad.com");//ایمیل متابق

                // شبیه‌سازی اعتبارسنجی
                Set<ConstraintViolation<Person>> violations = new HashSet<>();
                ConstraintViolation<Person> violation = mock(ConstraintViolation.class);
                Path propertyPath = mock(Path.class);
                when(violation.getPropertyPath()).thenReturn(propertyPath);
                when(violation.getMessage()).thenReturn(""); //اروری مشاهده نمیشود

                violations.add(violation);
                when(validator.validate(person)).thenReturn(violations);

                mockMvc.perform(post("/api/persons")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(person)) // تبدیل به JSON
                                .session(session)) // استفاده از جلسه شبیه‌سازی شده
                        .andExpect(status().isOk())//پاسخ درست
                        .andExpect(content().string(containsString("")));//اروری مشاهده نمیشود
        }

}