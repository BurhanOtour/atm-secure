package de.upb.cs.bibifi.bankapp;

import de.upb.cs.bibifi.bankapp.bank.impl.Bank;
import de.upb.cs.bibifi.commons.data.AuthFile;
import org.json.JSONObject;

import java.math.BigDecimal;


public class Main {
    public static void main(String[] args) throws Exception {
        BigDecimal x = new BigDecimal("10.00");
        BigDecimal y = new BigDecimal("6.14");
        BigDecimal z = new BigDecimal("3.86");
        x = x.subtract(y);
        x = x.subtract(z);
        System.out.println(x);
        System.out.println(y);
        JSONObject obj = new JSONObject();
        obj.put("balance",y);
        System.out.println(obj.toString());
    }
}
