package com.onedata.demo.service;

import com.onedata.demo.entity.LoanWithdraw;
import com.onedata.demo.repository.LoanWithdrawRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Chunliang.Han on 2017/11/22.
 */
@Service
public class FooService {
  @Autowired
  LoanWithdrawRepository loanWithdrawRepository;

  public void testFind() {
    LoanWithdraw loanWithdraw = loanWithdrawRepository.findByLoanId("005ADEE9-D674-425E-8E6B-DDD6D63B55FD");
    System.out.println(loanWithdraw.getcTime());
  }
}
