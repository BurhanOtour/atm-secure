package de.upb.cs.bibifi.atmapp.atm.processors;

public interface ITransactionProcessor {

    void process();

    void prepareTransaction();

    void commitTransaction();
}
