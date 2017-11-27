package com.onedata.demo.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 标的提现记录
 * 2017/4/17.
 */
@Entity
@Table(name = "tb_loan_withdraw")
public class LoanWithdraw {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  /**
   * 标的ID
   */
  @Column(name = "loan_id")
  private String loanId;
  /**
   * 标的金额
   */
  @Column(name = "loan_amount")
  private long loanAmount;
  /**
   * 服务费
   */
  @Column(name = "loan_fee_service")
  private long loanFeeService;
  /**
   * 提现金额
   */
  @Column(name = "withdraw_amount")
  private long withdrawAmount;
  /**
   * 提现状态(0初始 1提现中 2已提现)
   */
  @Column(name = "withdraw_status")
  private int withdrawStatus;
  /**
   * 提现订单号
   */
  @Column(name = "withdraw_order")
  private String withdrawOrder;
  /**
   * 员工ID
   */
  @Column(name = "employee_id")
  private String employeeId;
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "c_time")
  private Date cTime;
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "m_time")
  private Date mTime;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getLoanId() {
    return loanId;
  }

  public void setLoanId(String loanId) {
    this.loanId = loanId;
  }

  public long getLoanAmount() {
    return loanAmount;
  }

  public void setLoanAmount(long loanAmount) {
    this.loanAmount = loanAmount;
  }

  public long getLoanFeeService() {
    return loanFeeService;
  }

  public void setLoanFeeService(long loanFeeService) {
    this.loanFeeService = loanFeeService;
  }

  public long getWithdrawAmount() {
    return withdrawAmount;
  }

  public void setWithdrawAmount(long withdrawAmount) {
    this.withdrawAmount = withdrawAmount;
  }

  public int getWithdrawStatus() {
    return withdrawStatus;
  }

  public void setWithdrawStatus(int withdrawStatus) {
    this.withdrawStatus = withdrawStatus;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(String employeeId) {
    this.employeeId = employeeId;
  }

  public String getWithdrawOrder() {
    return withdrawOrder;
  }

  public void setWithdrawOrder(String withdrawOrder) {
    this.withdrawOrder = withdrawOrder;
  }

  public Date getcTime() {
    return cTime;
  }

  public void setcTime(Date cTime) {
    this.cTime = cTime;
  }

  public Date getmTime() {
    return mTime;
  }

  public void setmTime(Date mTime) {
    this.mTime = mTime;
  }
}
