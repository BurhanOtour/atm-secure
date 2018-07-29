package de.upb.cs.bibifi.bankapp.bank.impl;

import com.google.gson.Gson;
import de.upb.cs.bibifi.bankapp.bank.IServerProcessor;
import de.upb.cs.bibifi.commons.dto.CreationResponse;
import de.upb.cs.bibifi.commons.dto.Response;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;
import de.upb.cs.bibifi.commons.enums.RequestType;
import org.json.JSONObject;

import static de.upb.cs.bibifi.commons.constants.AppConstants.*;


public class ServerProcessor implements IServerProcessor {

    private static ServerProcessor processor;

    private Gson gson = new Gson();

    private ServerProcessor() {

    }

    public static ServerProcessor getServerProcessor() {
        if (processor == null)
            processor = new ServerProcessor();
        return processor;
    }


    // Validate TransmissionPacket before injecting it here!
    public String executeOperation(TransmissionPacket transmissionPacket) {
        String output = null;
        switch (transmissionPacket.getRequestType()) {
            case CREATE:
                output = createAccount(transmissionPacket);
                break;
            case DEPOSIT:
                output = deposit(transmissionPacket);
                break;
            case WITHDRAW:
                output = withdraw(transmissionPacket);
                break;
            case CHECKBALANCE:
                output = checkBalance(transmissionPacket);
                break;

        }
        return output;

    }

    // @TODO Store hard coded String into constants
    private String createAccount(TransmissionPacket transmissionPacket) {
        String accountName = transmissionPacket.getProperty(KEY_ACCOUNT_NAME);
        Integer balance = Integer.parseInt(transmissionPacket.getProperty(KEY_BALANCE));
        String pin = Bank.getBank().createBalance(accountName, balance);
        Response response;
        if (pin == null) {
            response = new Response("", 255);
        } else {
            response = buildResponse(RequestType.CREATE, transmissionPacket, pin);
        }
        return gson.toJson(response);
    }

    private String deposit(TransmissionPacket transmissionPacket) {
        String accountName = transmissionPacket.getProperty(KEY_ACCOUNT_NAME);
        Integer balance = Integer.parseInt(transmissionPacket.getProperty(KEY_DEPOSITE));
        String pin = transmissionPacket.getProperty(KEY_PIN);
        boolean success = Bank.getBank().deposit(accountName, pin, balance);
        Response response;
        if (success) {
            response = buildResponse(RequestType.DEPOSIT, transmissionPacket, null);
        } else {
            response = new Response("", 255);
        }
        return gson.toJson(response);
    }

    private String checkBalance(TransmissionPacket transmissionPacket) {
        String accountName = transmissionPacket.getProperty(KEY_ACCOUNT_NAME);
        String pin = transmissionPacket.getProperty(KEY_PIN);
        int balance = Bank.getBank().checkBalance(accountName, pin);
        Response response;
        if (balance > -1) {
            response = buildResponse(RequestType.CHECKBALANCE, transmissionPacket, String.valueOf(balance));
        } else {
            response = new Response("", 255);
        }
        return gson.toJson(response);
    }

    private String withdraw(TransmissionPacket transmissionPacket) {
        String accountName = transmissionPacket.getProperty(KEY_ACCOUNT_NAME);
        Integer balance = Integer.parseInt(transmissionPacket.getProperty(KEY_WIHTDRAW));
        String pin = transmissionPacket.getProperty(KEY_PIN);
        boolean success = Bank.getBank().withdraw(accountName, pin, balance);
        Response response;
        if (success) {
            JSONObject obj = new JSONObject();
            obj.put(KEY_ACCOUNT_NAME, accountName);
            obj.put(KEY_WIHTDRAW, balance);
            response = buildResponse(RequestType.WITHDRAW, transmissionPacket, null);
        } else {
            response = new Response("", 255);
        }
        return gson.toJson(response);
    }

    private Response buildResponse(RequestType type, TransmissionPacket transmissionPacket, String data) {
        Response response = null;
        JSONObject obj = new JSONObject();
        String message;
        switch (type) {
            case CREATE:
                obj.put(KEY_ACCOUNT_NAME, transmissionPacket.getProperty(KEY_ACCOUNT_NAME));
                obj.put(KEY_INITIAL_BALANCE, Integer.parseInt(transmissionPacket.getProperty(KEY_BALANCE)));
                message = obj.toString();
                response = new CreationResponse(message, 0, data);
                System.out.println(response.getMessage());
                break;
            case DEPOSIT:
                obj.put(KEY_ACCOUNT_NAME, transmissionPacket.getProperty(KEY_ACCOUNT_NAME));
                obj.put(KEY_DEPOSITE, Integer.parseInt(transmissionPacket.getProperty(KEY_DEPOSITE)));
                message = obj.toString();
                response = new Response(message, 0);
                break;
            case WITHDRAW:
                obj.put(KEY_ACCOUNT_NAME, transmissionPacket.getProperty(KEY_ACCOUNT_NAME));
                obj.put(KEY_WIHTDRAW, Integer.parseInt(transmissionPacket.getProperty(KEY_WIHTDRAW)));
                message = obj.toString();
                response = new Response(message, 0);
                break;
            case CHECKBALANCE:
                obj.put(KEY_ACCOUNT_NAME, transmissionPacket.getProperty(KEY_ACCOUNT_NAME));
                obj.put(KEY_BALANCE, new Integer(data));
                message = obj.toString();
                response = new Response(message, 0);
                break;
        }
        return response;
    }
}
