package com.onedata.demo.repository;

import com.onedata.demo.entity.LoanWithdraw;
import com.sinosoft.one.data.jade.annotation.SQL;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface LoanWithdrawRepository extends PagingAndSortingRepository<LoanWithdraw,Integer> {

  @SQL("select * from tb_loan_withdraw where loan_id=:loanId")
  LoanWithdraw findByLoanId(@Param("loanId") String loanId);
}
