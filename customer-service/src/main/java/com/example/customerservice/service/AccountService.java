package com.example.customerservice.service;


import com.example.customerservice.entity.Account;
import com.example.customerservice.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    public AccountRepository accountRepository;


    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account getUserById(Long id) {

        Optional<Account> account =   accountRepository.findById(id);
        if (account.isPresent() ) {
            return account.orElse(null);
        }
        return null;
    }

    public List<Account> getAllAccounts() {

        return  accountRepository.findAll();
    }

  public Account getUserByDocumentNumber(Long documentNumber)  {

      return accountRepository.findAccountByDocument_number(documentNumber);

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Account addAccount (Account u)  {
       // u.setAccountMoney(BigDecimal.valueOf(200.00));
        return  accountRepository.save(u);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Account creditMoney (Account u, BigDecimal amount)  {
        u.setAccountMoney(u.getAccountMoney().add(amount));
        return  accountRepository.save(u);
    }



    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Account debitMoney (Account u, BigDecimal amount)  {
        u.setAccountMoney(u.getAccountMoney().add(amount));
        return  accountRepository.save(u);
    }



}
