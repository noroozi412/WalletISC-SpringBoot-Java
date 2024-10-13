package com.alirezanoroozi.walletisc.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", billAmount=" + billAmount +
                ", billType='" + billType + '\'' +
                ", billStatus=" + billStatus +
                ", account=" + account +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    @NotNull
    @DecimalMin(value = "99999", message = "مقدار فاکتور باید حداقل 100000 باشد.")
    // @DecimalMax(value = "10000000", message = "مقدار فاکتور باید حداکثر 10000000 باشد.")
    private Double billAmount; // تغییر نوع به Double برای اعتبارسنجی بهتر

    @NotNull
    @Pattern(regexp = "^(واریز|برداشت)$", message = "نوع فاکتور باید 'واریز' یا 'برداشت' باشد.")
    private String billType;

    //   @NotNull
    private boolean billStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Account account;

    @NotNull
    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt; // فیلد زمان ایجاد


}
