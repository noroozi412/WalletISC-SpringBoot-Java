package com.alirezanoroozi.walletisc.Controller;

import com.alirezanoroozi.walletisc.Entity.Account;
import com.alirezanoroozi.walletisc.Entity.Bill;
import com.alirezanoroozi.walletisc.Repository.RepositoryAccount;
import com.alirezanoroozi.walletisc.Repository.RepositoryBill;
import com.alirezanoroozi.walletisc.Service.ServiceAccount;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
@Validated

public class ControllerAccount {

    @Autowired
    private ServiceAccount serviceAccount;
    @Autowired
    private Validator validator;
    @Autowired
    private RepositoryAccount repositoryAccount;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private RepositoryBill billRepository;

    @GetMapping
    public ResponseEntity<?> getAccountList() {
        Object role = httpSession.getAttribute("role");
        Boolean isAuthenticated = (Boolean) httpSession.getAttribute("isAuthenticated");
        // بررسی دسترسی کاربر
        if (role != null && role.equals("admin") && Boolean.TRUE.equals(isAuthenticated)) {
             // دریافت لیست اکانت‌ها
            return ResponseEntity.ok(serviceAccount.getAccoutList());
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "دسترسی ندارید");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse); // برگرداندن پاسخ با وضعیت 403
        }
    }
    @PostMapping
    public ResponseEntity<List<Account>> saveAccount(@Valid @RequestBody Account account) {
        Account saveAccount = serviceAccount.saveAccount(account);
        return ResponseEntity.ok(Collections.singletonList(saveAccount)); // اطمینان از اینکه لیست شامل حساب ذخیره شده است
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccount(@PathVariable("id") Long id) {
        Account account = serviceAccount.getAccount(id);
        if (account != null) {
            return ResponseEntity.ok(account);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "دسترسی ندارید");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
    }

   @DeleteMapping("/{id}")
   public ResponseEntity<?> deleteAccount(@PathVariable("id") Long id) {
       // ابتدا حساب کاربری را با ID مشخص شده پیدا می‌کنیم
       Account account = repositoryAccount.findById(id).orElse(null);

       if (account == null) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No account found with this ID");
       }

       Object userId = httpSession.getAttribute("userId");
       Object role = httpSession.getAttribute("role");
       Boolean isAuthenticated = (Boolean) httpSession.getAttribute("isAuthenticated");

       // بررسی نقش کاربر
       if (role != null && role.equals("admin") && Boolean.TRUE.equals(isAuthenticated)) {
           serviceAccount.deleteAccount(id);
           return ResponseEntity.status(HttpStatus.OK).body("Account Removed");
       }

       // بررسی مالکیت حساب
       if (userId != null && userId.equals(account.getPerson().getId()) && Boolean.TRUE.equals(isAuthenticated)) {
           serviceAccount.deleteAccount(id);
           return ResponseEntity.status(HttpStatus.OK).body("Account Removed");
       } else {
           Map<String, String> errorResponse = new HashMap<>();
           errorResponse.put("error", "You do not have access");
           return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
       }
   }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateAccount(@PathVariable("id") Long id, @Valid @RequestBody Account account) {
      // ابتدا حساب کاربری را با ID مشخص شده پیدا می‌کنیم
      Account existingAccount = repositoryAccount.findById(id).orElse(null);

      if (existingAccount == null) {
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body("حسابی با این ID پیدا نشد");
      }

      Object userId = httpSession.getAttribute("userId");
      Object role = httpSession.getAttribute("role");
      Boolean isAuthenticated = (Boolean) httpSession.getAttribute("isAuthenticated");

      // بررسی نقش کاربر
      if (role != null && role.equals("admin") && Boolean.TRUE.equals(isAuthenticated)) {
          existingAccount.setBalance(account.getBalance());
          // فقط بالانس و مالکیت قابل تغییر است
          existingAccount.setPerson(account.getPerson());
          return ResponseEntity.ok(serviceAccount.updateAccount(existingAccount, id));
      }

      // بررسی مالکیت حساب
      if (userId != null && userId.equals(existingAccount.getPerson().getId()) && Boolean.TRUE.equals(isAuthenticated)) {
          existingAccount.setBalance(account.getBalance());
          existingAccount.setPerson(account.getPerson());
          return ResponseEntity.ok(serviceAccount.updateAccount(existingAccount, id));
      } else {
          Map<String, String> errorResponse = new HashMap<>();
          errorResponse.put("error", "دسترسی ندارید");
          return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
      }
  }
    @PostMapping("/pay")
    public ResponseEntity<?> createBill(@RequestBody HashMap<String, String> personData) {
        String accountNumber = personData.get("accountNumber");
        String billType = personData.get("billType");
        String billAmountString = personData.get("billAmount");
        Double billAmount = 0.00;

        try {
            billAmount = Double.parseDouble(billAmountString);
        } catch (NumberFormatException e) {
            return ResponseEntity.ok("فرمت وارد شده برای ریال نامعتبر است:");
        }

        Bill bill = new Bill();
        boolean status = false;
        Object userId = httpSession.getAttribute("userId");
        Account account = repositoryAccount.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        String description = "";
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        double DailyWithdrawalLimit = billRepository.sumWithdrawalsTodayByAccount(account.getId(), startOfDay, endOfDay);
        if (billRepository.sumWithdrawalsTodayByAccount(account.getId(), startOfDay, endOfDay) != null) {
            DailyWithdrawalLimit = billRepository.sumWithdrawalsTodayByAccount(account.getId(), startOfDay, endOfDay);
        }

        Boolean isAuthenticated = (Boolean) httpSession.getAttribute("isAuthenticated");

        if ("برداشت".equals(billType)) {
            if (billAmount>99999) {
                if (userId != null && userId.equals(account.getPerson().getId()) && Boolean.TRUE.equals(isAuthenticated)) {
                    if (account.getBalance() > billAmount && account.getBalance() >= 10000) {
                        if (DailyWithdrawalLimit <= 10000000.00 && (DailyWithdrawalLimit + billAmount) <= 10000000) {
                            Double newBalance = account.getBalance() - billAmount;
                            if (newBalance >= 10000) {
                                account.setBalance(newBalance);
                                status = true;
                                description = "برداشت موفقیت آمیز بود";
                            } else {
                                description = "خطا: موجودی کافی نیست";
                            }
                        } else {
                            description = "خطا: محدودیت برداشت روزانه exceeded";
                        }
                    } else {
                        description = "خطا: موجودی کافی نیست";
                    }
                } else {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "دسترسی ندارید");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
                }
            }else{
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "خطا: حداقل برداشت مبلغ برداشت باید 100.000 ریاب باشد");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
        } else if ("واریز".equals(billType)) {
            Double newBalance = account.getBalance() + billAmount;
            account.setBalance(newBalance);
            status = true;
            description = "واریز موفقیت آمیز بود";
        } else {
            description = "خطا: نوع تراکنش نامعتبر است";
        }

        bill.setBillAmount(billAmount);
        bill.setBillType(billType);
        bill.setBillStatus(status);
        bill.setDescription(description);
        bill.setAccount(account);
        repositoryAccount.save(account);
        return ResponseEntity.ok(billRepository.save(bill));
    }

}
