package com.alirezanoroozi.walletisc.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@Setter
@Getter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @PrePersist
    public void generateAccountAndShebaNumber() {
        String accountNumber = UUID.randomUUID().toString().replaceAll("[^0-9]", "");
        while (accountNumber.length() < 16) {
            accountNumber += UUID.randomUUID().toString().replaceAll("[^0-9]", "");
        }
        this.accountNumber = accountNumber.substring(0, 16);

        String bankCode = "IR"; // کد بانک
        String branchCode = "000000"; // کد شعبه
        this.shebaNumber = bankCode + branchCode + this.accountNumber;
    }

    @Column(unique = true)
    private String accountNumber;

    //@NotBlank(message = "IBAN is required")
    @Column(unique = true)
   // @Pattern(regexp = "^(?:IR)(?=.{24}$)[0-9]*$", message = "shebaNumber number must be True")
    private String shebaNumber;
    @NotNull(message = "Balance is required")
    @Min(value = 10000, message = "The initial transaction amount must be 10,000")
    private Double balance;

    @CreationTimestamp
    private LocalDateTime creationDate;

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", shebaNumber='" + shebaNumber + '\'' +
                ", balance=" + balance +
                ", creationDate=" + creationDate +
                ", person=" + person +
                ", bill=" + bill +
                '}';
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "person_id is required")
    private Person person;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonProperty
    private Set<Bill> bill;
}