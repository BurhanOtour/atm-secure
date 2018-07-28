package de.upb.cs.bibifi.atmapp.atm.impl;

import de.upb.cs.bibifi.atmapp.Client;
import de.upb.cs.bibifi.atmapp.atm.IATM;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;
import de.upb.cs.bibifi.commons.enums.RequestType;
import org.apache.commons.cli.Option;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class Atm implements IATM {

    private final Client client;

    public Atm(Client client) {
        this.client = client;
    }

    @Override
    public void createAccount(Option[] params) {

        HashMap<String, String> requestParams = new HashMap<>();

        TransmissionPacket request = new TransmissionPacket();

        request.setRequestType(RequestType.CREATE);

        Arrays.stream(params).forEach(parameter -> {

            if ("n".equals(parameter.getOpt())) {
                requestParams.put("initialBalance", parameter.getValue());
            }
            if ("a".equals(parameter.getOpt())) {
                requestParams.put("accountName", parameter.getValue());
            }

        });

        request.setProperties(requestParams);

        try {

            TransmissionPacket transmissionPacket = client.clientRequest(request);



            transmissionPacket.getProperties().entrySet()// TODO: 28/07/2018 null check
                    .stream()
                    .forEach(System.out::println);// TODO: 27/07/2018 print what is needed only

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deposit(Option[] params) {
    }

    @Override
    public void withdraw(Option[] params) {
    }

    @Override
    public void checkBalance(Option[] params) {
    }
}
