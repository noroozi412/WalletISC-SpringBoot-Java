package com.alirezanoroozi.walletisc.Entity;

import com.alirezanoroozi.walletisc.Validator.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;


import java.util.Date;
import java.util.Set;
@Setter
@Getter
@ValidMilitaryStatus
@Entity
public class Person {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message ="Last name is required")
    private String Lastname;

    @Column(unique = true)
    @NotBlank(message = "National code is required")
    @ValidNationalCode
    @UniqueNationalId(message = "National code already exists")

    private String nationalId;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private Date dateOfBirth;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(man|woman)$", message = "Gender must be either 'man' or 'woman'")
    private String gender;

   // @Pattern(regexp = "^(completion|absence|exempt|included)$", message = "Military Status must be completion|absence|exempt|included")
    private String militaryStatus;

    @NotBlank(message = "Mobile number is required")
    @Column(unique = true)
    @Pattern(regexp = "^[0-9]{11}$", message = "Mobile number must be 11 digits - 09123334444")
    @UniqueMobileNumber(message = "Mobile number already exists")
    private String mobileNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @UniqueEmail
    @Column(unique = true)
    private String email;

    @Pattern(regexp = "^(user|admin)$", message = "User Status must be user|admin")
    private String userStatus;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    @JsonProperty
    private Set<Account> account;


    public Person(String firstName, String lastname, String nationalId, String gender, String militaryStatus, String mobileNumber, String email, String userStatus, Set<Account> account) {
        this.firstName = firstName;
        Lastname = lastname;
        this.nationalId = nationalId;
        this.gender = gender;
        this.militaryStatus = militaryStatus;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.userStatus = userStatus;
    }


    public Person() {

    }
}
