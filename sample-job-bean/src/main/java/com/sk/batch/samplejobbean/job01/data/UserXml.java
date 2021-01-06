package com.sk.batch.samplejobbean.job01.data;

import java.util.Date;
import lombok.Data;

@Data
public class UserXml {
    private String userName;
    private int userId;
    private Date transactionDate;
    private double transactionAmount;
    private Date updatedDate;
 
    @Override
    public String toString() {
        return "UserXml [username=" + userName + ", userId=" + userId
          + ", transactionDate=" + transactionDate + ", transactionAmount=" + transactionAmount
          + ", updatedDate=" + updatedDate+ "]";
    }

}
