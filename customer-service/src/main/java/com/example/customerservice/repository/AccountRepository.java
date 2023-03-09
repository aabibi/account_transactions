package com.example.customerservice.repository;

import com.example.customerservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository  extends JpaRepository<Account, Long> {

    @Query( "from Account where document_number= ?1 ")
    Account  findAccountByDocument_number(long documentNumber);

}

