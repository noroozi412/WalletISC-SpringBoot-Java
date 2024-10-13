package com.alirezanoroozi.walletisc.Controller;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.alirezanoroozi.walletisc.Entity.Account;
import com.alirezanoroozi.walletisc.Entity.Bill;
import com.alirezanoroozi.walletisc.Entity.Person;
import com.alirezanoroozi.walletisc.Repository.RepositoryAccount;
import com.alirezanoroozi.walletisc.Repository.RepositoryBill;
import com.alirezanoroozi.walletisc.Service.ServiceAccount;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import org.checkerframework.checker.units.qual.A;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.http.HttpSession;

import jakarta.validation.Validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
;

public class ControllerAccountTest {
    @InjectMocks
    private ControllerAccount controllerAccount;
    @Mock
    private RepositoryAccount account;

    @Mock
    private ServiceAccount serviceAccount;

    @Mock
    private HttpSession httpSession;

    @Mock
    private Validator validator;
    @Mock
    private RepositoryBill repositoryBill;
    @Mock
    private Gson getJsonString; // در صورتی که برای تبدیل JSON استفاده می‌شود
    @Mock
    private RepositoryAccount repositoryAccount; // فرض کنید این یک اینترفیس است

    private MockMvc mockMvc;
    private MockHttpSession session;
    private ObjectMapper objectMapper;
    @Value("${app.daily.withdrawal.limit}")
    private double dailyWithdrawalLimit;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controllerAccount).build();
        session = new MockHttpSession();
      //  session.setAttribute("role", "admin");
      //  session.setAttribute("isAuthenticated", true);
        objectMapper = new ObjectMapper(); // برای تبدیل اشیاء به JSON
        session = new MockHttpSession();


    }
    @Test
    public void testGetAccountList_AdminAccess() throws Exception {
        // داده‌های تست
        Account account1 = new Account();
        account1.setId(1L);
        account1.setBalance(15000.0);
        account1.setAccountNumber("1234567890123456");
        account1.setShebaNumber("IR0000001234567890");
        account1.setPerson(null);

        Account account2 = new Account();
        account2.setId(2L);
        account2.setBalance(15000.0);
        account2.setAccountNumber("6543210987654321");
        account2.setShebaNumber("IR0000006543210987");
        account2.setPerson(null);

        List<Account> accounts = Arrays.asList(account1, account2);

        // Mock کردن رفتار
        when(httpSession.getAttribute("role")).thenReturn("admin"); // نقش صحیح
        when(httpSession.getAttribute("isAuthenticated")).thenReturn(true); // کاربر احراز هویت شده است
        when(serviceAccount.getAccoutList()).thenReturn(ResponseEntity.ok(accounts)); // شبیه‌سازی درست

        // ارسال درخواست و دریافت پاسخ
        String responseContent = mockMvc.perform(get("/api/account")
                        .sessionAttr("role", "admin") // اضافه کردن session به درخواست
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // انتظار داریم که وضعیت 200 برگردد
                .andReturn()
                .getResponse()
                .getContentAsString();

        // چاپ داده‌های دریافتی
        System.out.println("Response JSON:");
        System.out.println(responseContent);

    }
    @Test
    public void testGetAccountList_NotAdmin() throws Exception {

            Account account1 = new Account();
            account1.setId(1L);
            account1.setBalance(15000.0);
            account1.setAccountNumber("1234567890123456");
            account1.setShebaNumber("IR0000001234567890");
            account1.setPerson(null);

            Account account2 = new Account();
            account2.setId(2L);
            account2.setBalance(15000.0);
            account2.setAccountNumber("6543210987654321");
            account2.setShebaNumber("IR0000006543210987");
            account2.setPerson(null);

            List<Account> accounts = Arrays.asList(account1, account2);

            // Mock کردن رفتار
            when(httpSession.getAttribute("role")).thenReturn("user"); // نقش صحیح
            when(httpSession.getAttribute("isAuthenticated")).thenReturn(true); // کاربر احراز هویت شده است
            when(serviceAccount.getAccoutList()).thenReturn(ResponseEntity.ok(accounts)); // شبیه‌سازی درست

            // ارسال درخواست و دریافت پاسخ
            String responseContent = mockMvc.perform(get("/api/account")
                            .sessionAttr("role", "user") // اضافه کردن session به درخواست
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden()) // انتظار داریم که وضعیت 200 برگردد
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            // چاپ داده‌های دریافتی
            System.out.println("Response JSON:");
            System.out.println(responseContent);
    }
    @Test
    public void testGetAccount_UserAccess() throws Exception {
        // Arrange
        Long accountId = 1L;
        Long userId = 2L;
        Account account = new Account();
        Person person = new Person();
        person.setId(userId);
        account.setPerson(person);
        when(serviceAccount.getAccount(accountId)).thenReturn(account);

        // Act & Assert
        mockMvc.perform(get("/api/account/{id}", accountId)
                        .sessionAttr("userId", userId)
                        .sessionAttr("isAuthenticated", true))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
    @Test
    public void testGetAccount_NoAccess() throws Exception {
        // Arrange
        Long accountId = 1L;
        when(serviceAccount.getAccount(accountId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/account/{id}", accountId)
                        .sessionAttr("userId", 3L) // کاربری که دسترسی ندارد
                        .sessionAttr("isAuthenticated", true))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("دسترسی ندارید"));
    }
    @Test
    public void testGetAccount_UnauthenticatedUser() throws Exception {
        // Arrange
        Long accountId = 1L;
        when(serviceAccount.getAccount(accountId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/account/{id}", accountId)
                        .sessionAttr("isAuthenticated", false)) // کاربر احراز هویت نشده
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("دسترسی ندارید"));
    }
    @Test
    public void testGetAccount_NonExistentAccount() throws Exception {
        // Arrange
        Long accountId = 1L;
        when(serviceAccount.getAccount(accountId)).thenReturn(null); // حساب وجود ندارد

        // Act & Assert
        mockMvc.perform(get("/api/account/{id}", accountId)
                        .sessionAttr("userId", 3L) // کاربری که دسترسی ندارد
                        .sessionAttr("isAuthenticated", true))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("دسترسی ندارید"));
    }
    @Test
    public void testSaveAccount() throws Exception {
        // داده حساب برای تست
        Account account = new Account();
        account.setBalance(15000.0);
        // به هیچ عنوان نیازی به تعیین accountNumber و shebaNumber نیست چون به‌طور خودکار تولید می‌شوند
        // فرض می‌کنیم person را در اینجا نیز مشخص کرده‌اید مالک
        Person person = new Person();
        person.setId(1L); // شناسه شخص
        account.setPerson(person);

        // داده‌ای که می‌خواهیم به عنوان پاسخ شبیه‌سازی کنیم
        Account savedAccount = new Account();
        savedAccount.setId(1L); // شناسه جدید
        savedAccount.setBalance(15000.0);
        savedAccount.setPerson(person);
        // accountNumber و shebaNumber به صورت خودکار هنگام ذخیره تولید می‌شوند

        // شبیه‌سازی رفتار متد سرویس
        when(serviceAccount.saveAccount(any(Account.class))).thenReturn(savedAccount);

        // انجام درخواست POST
        mockMvc.perform(post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(account)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].balance").value(15000.0));
        }
    @Test
    public void testSaveAccount_ValidationErrors() throws Exception {
        // Arrange
        Account account = new Account();
        account.setBalance(5000.0); // کف موجودی مقدار نامعتبر (کمتر از 10.000)

        Set<ConstraintViolation<Account>> violations = new HashSet<>();
        ConstraintViolation<Account> violation = mock(ConstraintViolation.class);
        Path propertyPath = mock(Path.class);

        when(propertyPath.toString()).thenReturn("balance"); // شبیه‌سازی مسیر
        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(violation.getMessage()).thenReturn("The initial transaction amount must be 10,000");
        violations.add(violation);

        when(validator.validate(account)).thenReturn(violations);

        // Act & Assert
        mockMvc.perform(post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(account))) // تبدیل به JSON
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteAccount_AdminUser() throws Exception {
        // Arrange
        Long accountId = 1L;
        Account account = new Account();
        account.setId(accountId);
        Person person = new Person();
        person.setId(2L);
        account.setPerson(person);

        when(repositoryAccount.findById(accountId)).thenReturn(Optional.of(account)); // شبیه‌سازی پیدا کردن حساب
        when(httpSession.getAttribute("role")).thenReturn("admin"); // شبیه‌سازی نقش کاربر
        when(httpSession.getAttribute("isAuthenticated")).thenReturn(true); // شبیه‌سازی وضعیت احراز هویت

        // Act & Assert
        mockMvc.perform(delete("/api/account/{id}", accountId)
                        .session(session)) // استفاده از httpSession شبیه‌سازی‌شده به‌طور مستقیم
                .andExpect(status().isOk())
                .andExpect(content().string("Account Removed"));

        verify(serviceAccount).deleteAccount(accountId); // بررسی اینکه متد حذف حساب فراخوانی شده است
    }
    @Test
    public void testDeleteAccount_OwnerUser() throws Exception {
        // Arrange
        Long accountId = 1L;
        Account account = new Account();
        account.setId(accountId);
        Person person = new Person();
        person.setId(2L);
        account.setPerson(person);

        when(repositoryAccount.findById(accountId)).thenReturn(Optional.of(account)); // شبیه‌سازی پیدا کردن حساب
        when(httpSession.getAttribute("userId")).thenReturn(2L); // شبیه‌سازی کاربر مالک حساب
        when(httpSession.getAttribute("isAuthenticated")).thenReturn(true); // شبیه‌سازی وضعیت احراز هویت

        // Act & Assert
        mockMvc.perform(delete("/api/account/{id}", accountId)
                        .session(session)) // استفاده از httpSession شبیه‌سازی‌شده به‌طور مستقیم
                .andExpect(status().isOk())
                .andExpect(content().string("Account Removed"));

        verify(serviceAccount).deleteAccount(accountId); // بررسی اینکه متد حذف حساب فراخوانی شده است
    }
    @Test
    public void testDeleteAccount_AccountNotFound() throws Exception {
        // Arrange
        Long accountId = 1L;

        when(repositoryAccount.findById(accountId)).thenReturn(Optional.empty()); // شبیه‌سازی عدم پیدا کردن حساب
        when(httpSession.getAttribute("isAuthenticated")).thenReturn(true); // شبیه‌سازی وضعیت احراز هویت

        // Act & Assert
        mockMvc.perform(delete("/api/account/{id}", accountId)
                        .session(session)) // استفاده از httpSession شبیه‌سازی‌شده به‌طور مستقیم
                .andExpect(status().isNotFound())
                .andExpect(content().string("No account found with this ID"));
    }
    @Test
    public void testDeleteAccount_NotAuthorized() throws Exception {
        // Arrange
        Long accountId = 1L;
        Account account = new Account();
        account.setId(accountId);
        Person person = new Person();
        person.setId(2L);
        account.setPerson(person);

        when(repositoryAccount.findById(accountId)).thenReturn(Optional.of(account)); // شبیه‌سازی پیدا کردن حساب
        when(httpSession.getAttribute("userId")).thenReturn(3L); // شبیه‌سازی کاربر غیرمالک
        when(httpSession.getAttribute("isAuthenticated")).thenReturn(true); // شبیه‌سازی وضعیت احراز هویت

        // Act & Assert
        mockMvc.perform(delete("/api/account/{id}", accountId)
                        .session(session)) // استفاده از httpSession شبیه‌سازی‌شده به‌طور مستقیم
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("You do not have access")); // بررسی پیام خطا
    }

    @Test
    public void testUpdateAccountAsAdmin() {
      Account  existingAccount = new Account();
        existingAccount.setId(1L);
        existingAccount.setBalance(500000.0);
        Person existingPerson = new Person();
        existingPerson.setId(9L);
        existingAccount.setPerson(existingPerson);

        // ساخت شی برای داده به روزرسانی
      Account  updateData = new Account();
        updateData.setBalance(1000500.0);
        Person updatePerson = new Person();
        updatePerson.setId(9L);
        updateData.setPerson(updatePerson);
        when(repositoryAccount.findById(1L)).thenReturn(Optional.of(existingAccount));
        when(httpSession.getAttribute("role")).thenReturn("admin");
        when(httpSession.getAttribute("isAuthenticated")).thenReturn(true);

        ResponseEntity<?> response = controllerAccount.updateAccount(1L, updateData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(serviceAccount).updateAccount(any(), eq(1L));
    }
    @Test
    public void testUpdateAccountAsOwner() {
        Account  existingAccount = new Account();
        existingAccount.setId(1L);
        existingAccount.setBalance(500000.0);
        Person existingPerson = new Person();
        existingPerson.setId(9L);
        existingAccount.setPerson(existingPerson);

        // ساخت شی برای داده به روزرسانی
        Account  updateData = new Account();
        updateData.setBalance(1000500.0);
        Person updatePerson = new Person();
        updatePerson.setId(10L);
        updateData.setPerson(updatePerson);
        when(repositoryAccount.findById(1L)).thenReturn(Optional.of(existingAccount));
        when(httpSession.getAttribute("userId")).thenReturn(9L);
        when(httpSession.getAttribute("isAuthenticated")).thenReturn(true);

        ResponseEntity<?> response = controllerAccount.updateAccount(1L, updateData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(serviceAccount).updateAccount(any(), eq(1L));
    }
    @Test
    public void testUpdateAccountNotFound() {
        Account  existingAccount = new Account();
        existingAccount.setId(1L);
        existingAccount.setBalance(500000.0);
        Person existingPerson = new Person();
        existingPerson.setId(9L);
        existingAccount.setPerson(existingPerson);

        // ساخت شی برای داده به روزرسانی
        Account  updateData = new Account();
        updateData.setBalance(1000500.0);
        Person updatePerson = new Person();
        updatePerson.setId(10L);
        when(repositoryAccount.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controllerAccount.updateAccount(1L, updateData);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("حسابی با این ID پیدا نشد", response.getBody());
    }
    @Test
    public void testUpdateAccountForbidden() {
        Account  existingAccount = new Account();
        existingAccount.setId(1L);
        existingAccount.setBalance(500000.0);
        Person existingPerson = new Person();
        existingPerson.setId(9L);
        existingAccount.setPerson(existingPerson);

        // ساخت شی برای داده به روزرسانی
        Account  updateData = new Account();
        updateData.setBalance(1000500.0);
        Person updatePerson = new Person();
        updatePerson.setId(10L);
        when(repositoryAccount.findById(1L)).thenReturn(Optional.of(existingAccount));
        when(httpSession.getAttribute("userId")).thenReturn(8L); // user ID does not match
        when(httpSession.getAttribute("isAuthenticated")).thenReturn(true);

        ResponseEntity<?> response = controllerAccount.updateAccount(1L, updateData);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("error", "دسترسی ندارید");
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    public void testSuccessfulDeposit() throws Exception {
        HashMap<String, String> request = new HashMap<>();
        request.put("accountNumber", "8377870174149690");
        request.put("billType", "واریز");
        request.put("billAmount", "10000000");

        Account account = new Account();
        account.setBalance(5000000.0);

        Bill bill = new Bill();
        bill.setBillStatus(true);
        bill.setDescription("واریز موفقیت آمیز بود");

        when(repositoryAccount.findByAccountNumber("8377870174149690")).thenReturn(Optional.of(account));
        when(repositoryBill.save(any(Bill.class))).thenReturn(bill);

        mockMvc.perform(post("/api/account/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.billStatus").value(true))
                .andExpect(jsonPath("$.description").value("واریز موفقیت آمیز بود"));
    }
    @Test
    public void testSuccessfulWithdrawalWithAuthentication() throws Exception {
        HashMap<String, String> request = new HashMap<>();
        request.put("accountNumber", "8377870174149690");
        request.put("billType", "برداشت");
        request.put("billAmount", "5000000");

        Account account = new Account();
        account.setBalance(15000000.0);
        account.setId(1L);  // Assuming the account has an ID
        account.setPerson(new Person());
        account.getPerson().setId(1L);  // Assuming the person has an ID

        Bill bill = new Bill();
        bill.setBillStatus(true);
        bill.setDescription("برداشت موفقیت آمیز بود");

        when(repositoryAccount.findByAccountNumber("8377870174149690")).thenReturn(Optional.of(account));
        when(repositoryBill.save(any(Bill.class))).thenReturn(bill);
        when(httpSession.getAttribute("userId")).thenReturn(1L);  // Mock authenticated user ID
        when(httpSession.getAttribute("isAuthenticated")).thenReturn(true);  // Mock authentication status

        mockMvc.perform(post("/api/account/pay")
                        .sessionAttr("userId", 1L)  // Set session attributes
                        .sessionAttr("isAuthenticated", true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.billStatus").value(true))
                .andExpect(jsonPath("$.description").value("برداشت موفقیت آمیز بود"));

}
    @Test
    public void testWithdrawalWithoutAuthentication() throws Exception {
        HashMap<String, String> request = new HashMap<>();
        request.put("accountNumber", "8377870174149690");
        request.put("billType", "برداشت");
        request.put("billAmount", "5000000");

        Account account = new Account();
        account.setBalance(15000000.0);
        account.setId(1L);

        when(repositoryAccount.findByAccountNumber("8377870174149690")).thenReturn(Optional.of(account));
        when(httpSession.getAttribute("userId")).thenReturn(null);  // No authenticated user
        when(httpSession.getAttribute("isAuthenticated")).thenReturn(false);  // Not authenticated

        mockMvc.perform(post("/api/account/pay")
                        .sessionAttr("userId", "null")  // Set session attributes to simulate unauthenticated state
                        .sessionAttr("isAuthenticated", false)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("دسترسی ندارید"));
    }
    @Test
    public void testWithdrawalWithInsufficientBalance() throws Exception {
        HashMap<String, String> request = new HashMap<>();
        request.put("accountNumber", "8377870174149690");
        request.put("billType", "برداشت");
        request.put("billAmount", "15000000");

        Account account = new Account();
        account.setBalance(10000000.0);
        account.setId(1L);
        account.setPerson(new Person());
        account.getPerson().setId(1L);

        when(repositoryAccount.findByAccountNumber("8377870174149690")).thenReturn(Optional.of(account));
        when(httpSession.getAttribute("userId")).thenReturn(1L);
        when(httpSession.getAttribute("isAuthenticated")).thenReturn(true);

        mockMvc.perform(post("/api/account/pay")
                        .sessionAttr("userId", 1L)
                        .sessionAttr("isAuthenticated", true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.billStatus").value(false))
                .andExpect(jsonPath("$.description").value("خطا: موجودی کافی نیست"));
    }

    @Test
    public void testCreateBillWithdrawInsufficientBalance() throws Exception {
        HashMap<String, String> personData = new HashMap<>();
        personData.put("accountNumber", "123456789");
        personData.put("billType", "برداشت");
        personData.put("billAmount", "500000");

        Account account = new Account();
        account.setId(1L);
        account.setBalance(100000.00); // موجودی کمتر از مقدار برداشت
        Person person = new Person();
        person.setId(1L);
        account.setPerson(person);

        when(httpSession.getAttribute("userId")).thenReturn(1L);
        when(httpSession.getAttribute("isAuthenticated")).thenReturn(true);
        when(repositoryAccount.findByAccountNumber("123456789")).thenReturn(Optional.of(account));
        when(repositoryBill.sumWithdrawalsTodayByAccount(any(), any(), any())).thenReturn(0.00);

        mockMvc.perform(post("/api/account/pay")
                        .contentType("application/json")
                        .content("{\"accountNumber\":\"123456789\",\"billType\":\"برداشت\",\"billAmount\":\"500000\"}"))
                .andExpect(status().isOk());
        // بررسی ذخیره تراکنش با توضیحات صحیح
        ArgumentCaptor<Bill> billCaptor = ArgumentCaptor.forClass(Bill.class);
        verify(repositoryBill).save(billCaptor.capture());
        Bill savedBill = billCaptor.getValue();
        assertEquals("خطا: موجودی کافی نیست", savedBill.getDescription()); // بررسی توضیحات ذخیره شده
}

    @Test
    public void testCreateBillWithdrawDailyLimitExceeded() throws Exception {
        HashMap<String, String> personData = new HashMap<>();
        personData.put("accountNumber", "123456789");
        personData.put("billType", "برداشت");
        personData.put("billAmount", "5000000");

        Account account = new Account();
        account.setId(1L);
        account.setBalance(200000000.00);
        Person person = new Person();
        person.setId(1L);
        account.setPerson(person);

        when(httpSession.getAttribute("userId")).thenReturn(1L);
        when(httpSession.getAttribute("isAuthenticated")).thenReturn(true);
        when(repositoryAccount.findByAccountNumber("123456789")).thenReturn(Optional.of(account));
        when(repositoryBill.sumWithdrawalsTodayByAccount(any(), any(), any())).thenReturn(6000000.00); // فرض بر این است که برداشت‌های قبلی 6000000 تومان است

        mockMvc.perform(post("/api/account/pay")
                        .contentType("application/json")
                        .content("{\"accountNumber\":\"123456789\",\"billType\":\"برداشت\",\"billAmount\":\"5000000\"}"))
                .andExpect(status().isOk());
        // بررسی ذخیره تراکنش با توضیحات صحیح
        ArgumentCaptor<Bill> billCaptor = ArgumentCaptor.forClass(Bill.class);
        verify(repositoryBill).save(billCaptor.capture());
        Bill savedBill = billCaptor.getValue();
        assertEquals("خطا: محدودیت برداشت روزانه exceeded", savedBill.getDescription()); // بررسی توضیحات ذخیره شده
    }
}







