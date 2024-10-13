package com.alirezanoroozi.walletisc.Repository;

import com.alirezanoroozi.walletisc.Entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryPerson extends JpaRepository<Person, Long>  {

    Optional<Boolean> existsByEmail(@Param("email") String email);
    Optional<Boolean> existsByNationalId(@Param("nationalId") String nationalId);
    Optional<Boolean> existsByMobileNumber(@Param("mobileNumber") String mobileNumber);
    Person findByNationalId(String username);




}

