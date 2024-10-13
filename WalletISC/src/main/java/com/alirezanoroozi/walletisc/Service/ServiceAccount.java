package com.alirezanoroozi.walletisc.Service;

import com.alirezanoroozi.walletisc.Entity.Account;
import com.alirezanoroozi.walletisc.Entity.Bill;
import com.alirezanoroozi.walletisc.Repository.RepositoryAccount;
import com.alirezanoroozi.walletisc.Repository.RepositoryBill;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServiceAccount {
    @Autowired
    private RepositoryAccount repositoryAccount;
    @Autowired
    private RepositoryBill  repositoryBill;
    @Autowired
    private HttpSession httpSession;

    public ResponseEntity<List<Account>> getAccoutList(){
            return ResponseEntity.ok(repositoryAccount.findAll());
    }

    public Account getAccount(Long id) {
        // ابتدا حساب کاربری را با ID مشخص شده پیدا می‌کنیم
        Account account = repositoryAccount.findById(id).orElse(null);

        Object userId = httpSession.getAttribute("userId");
        Object role = httpSession.getAttribute("role");
        Boolean isAuthenticated = (Boolean) httpSession.getAttribute("isAuthenticated");

        if (role != null && role.equals("admin") && Boolean.TRUE.equals(isAuthenticated) ) {
            return account;
        }
        if (userId != null && userId.equals(account.getPerson().getId()) && Boolean.TRUE.equals(isAuthenticated)) {
            return account;
        } else {
            return null;
        }
    }

    public Account saveAccount(Account account){
        return repositoryAccount.save(account);
    }
    public ResponseEntity<?> deleteAccount(Long id){
            repositoryAccount.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("حساب حذف شد");
    }

    public void deleteAllAccount(){
        repositoryAccount.deleteAll();
    }
    /*public ResponseEntity<?> updateAccount(Account accountUpdate,Long id){


        // ابتدا حساب کاربری را با ID مشخص شده پیدا می‌کنیم
        Account account = repositoryAccount.findById(id).orElse(null);

        Object userId = httpSession.getAttribute("userId");
        Object role = httpSession.getAttribute("role");
        Boolean isAuthenticated = (Boolean) httpSession.getAttribute("isAuthenticated");

        if (role != null && role.equals("admin") && Boolean.TRUE.equals(isAuthenticated) )
        {
            account.setBalance(accountUpdate.getBalance());
            account.setPerson(accountUpdate.getPerson());
            return ResponseEntity.ok(repositoryAccount.save(account));
        }
        if (userId != null && userId.equals(account.getPerson().getId()) && Boolean.TRUE.equals(isAuthenticated))   // بررسی اینکه userId و isAuthenticated معتبر هستند
        {
            account.setBalance(accountUpdate.getBalance());
            account.setPerson(accountUpdate.getPerson());
            return ResponseEntity.ok(repositoryAccount.save(account));                  // اگر کاربر مجاز باشد، اطلاعات شخص را برمی‌گرداند
        }
        else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "دسترسی ندارید");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);            // اگر کاربر مجاز نباشد، یک JSON با متغیر error برمی‌گرداند
        }
    }*/
    public ResponseEntity<?> updateAccount(Account accountUpdate,Long id){
        // ابتدا حساب کاربری را با ID مشخص شده پیدا می‌کنیم
        Account account = repositoryAccount.findById(id).orElse(null);
            account.setBalance(accountUpdate.getBalance());
            account.setPerson(accountUpdate.getPerson());
            return ResponseEntity.ok(repositoryAccount.save(account));
    }

    public ResponseEntity<?> pay(String accountNumber,Double amount,String billType){
        Bill bill = new Bill();
        boolean status = false;
        Object userId = httpSession.getAttribute("userId");
        Account account = repositoryAccount.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        String  description ="";
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        // محاسبه مجموع برداشت‌های امروز
        double DailyWithdrawalLimit = repositoryBill.sumWithdrawalsTodayByAccount(account.getId(), startOfDay, endOfDay);
        if(repositoryBill.sumWithdrawalsTodayByAccount(account.getId(), startOfDay, endOfDay)!=null){
            DailyWithdrawalLimit =repositoryBill.sumWithdrawalsTodayByAccount(account.getId(), startOfDay, endOfDay);}

        Boolean isAuthenticated = (Boolean) httpSession.getAttribute("isAuthenticated");
        // عملیات برداشت
        if (billType.equals("برداشت") ) {
            if (userId != null && userId.equals(account.getPerson().getId()) && Boolean.TRUE.equals(isAuthenticated)) {
                if (account.getBalance() > amount && account.getBalance() >= 10000) {
                    if (DailyWithdrawalLimit <= 10000000.00 && (DailyWithdrawalLimit + amount) <= 10000000) {
                        Double newBalance = account.getBalance() - amount;
                        if (newBalance >= 10000) { // بررسی موجودی جدید
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
              }
            else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "دسترسی ندارید");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
        }
        // عملیات واریز
        else if (billType.equals("واریز")) {
            Double newBalance = account.getBalance() + amount;
            account.setBalance(newBalance);
            status = true;
            description = "واریز موفقیت آمیز بود";
            System.out.println(description);
        } else {
            description = "خطا: نوع تراکنش نامعتبر است";
        }
        // تنظیمات صورت‌حساب
        bill.setBillAmount(amount);
        bill.setBillType(billType);
        bill.setBillStatus(status);
        bill.setDescription(description);
        bill.setAccount(account);
        
        // ذخیره‌سازی اطلاعات در پایگاه داده
        repositoryAccount.save(account);
        repositoryBill.save(bill);
        return ResponseEntity.ok(repositoryBill.save(bill));
    }


}
