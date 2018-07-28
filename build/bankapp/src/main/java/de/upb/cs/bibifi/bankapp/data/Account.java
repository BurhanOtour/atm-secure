package de.upb.cs.bibifi.bankapp.data;

public class Account {
    private int balance;
    private String name;
    private String hashedPin;

    public Account(int balance, String name, String hashedPin) {
        this.balance = balance;
        this.name = name;
        this.hashedPin = hashedPin;
    }

    public int getBalance() {
        return balance;
    }

    public String getHashedPin() {
        return hashedPin;
    }

    public String getName() {
        return name;
    }

    public boolean addBalance(int newBalance) {
        this.balance += newBalance;
        return true;
    }

    public boolean withdrawBalance(int balance) {
        if (this.balance >= balance && balance > 0) {
            this.balance -= balance;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;

        Account that = (Account) obj;
        if (this.balance != that.balance || !this.hashedPin.equals(that.hashedPin) || !this.name.equals(that.name))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + name.hashCode();
        result = 31 * result + balance;
        result = 31 * result + hashedPin.hashCode();
        return result;
    }
}
