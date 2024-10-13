package com.alirezanoroozi.walletisc.Repository;

import com.alirezanoroozi.walletisc.Entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface RepositoryBill extends JpaRepository<Bill,Long> {
    //جمع مبالق روزانه یک  حساب
    @Query("SELECT COALESCE(SUM(b.billAmount), 0) " +
            "FROM Bill b " +
            "WHERE b.account.id = :accountId " +
            "AND b.billType = 'برداشت' " +
            "AND b.billStatus = true " +
            "AND b.createdAt >= :startOfDay " +
            "AND b.createdAt < :endOfDay") Double sumWithdrawalsTodayByAccount(@Param("accountId") Long accountId,
                                        @Param("startOfDay") LocalDateTime startOfDay,
                                        @Param("endOfDay") LocalDateTime endOfDay);
}

