package de.upb.cs.bibifi.atmapp;

import com.google.gson.Gson;
import de.upb.cs.bibifi.atmapp.atm.RequestProcessor;
import de.upb.cs.bibifi.commons.IEncryption;
import de.upb.cs.bibifi.commons.data.AuthFile;
import de.upb.cs.bibifi.commons.dto.CreationResponse;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;
import de.upb.cs.bibifi.commons.enums.RequestType;
import de.upb.cs.bibifi.commons.impl.EncryptionImpl;
import de.upb.cs.bibifi.commons.impl.Utilities;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client implements IClient {


    public void clientRequest(TransmissionPacket request, String authFileName) throws IOException {
        String jsonRequest = Utilities.Serializer(request);
        IEncryption encryption = EncryptionImpl.initialize(AuthFile.getAuthFile(authFileName).getKey());
        
        Socket sock = new Socket("127.0.0.1", 3000);
        OutputStream outputStream = sock.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream, true);
        InputStream inputStream = sock.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        //Encrypt the json request and send it to socket
        String encyptString = encryption.encryptMessage(jsonRequest);
        printWriter.println(encyptString);

        //Receive the response
        String receivedMessge;
        if((receivedMessge = br.readLine())!=null){
            //Decrypt the message
            String response = encryption.decryptMessage(receivedMessge);
            Gson gson = new Gson();
            CreationResponse responseObject = gson.fromJson(response, CreationResponse.class);
            System.out.println(responseObject.getMessage());
            printWriter.flush();
            sock.close();
            return;
        }
    }

    public static void main(String[] args) throws IOException{
        RequestProcessor processor = new RequestProcessor();
        TransmissionPacket packet = processor.generateRequest(RequestType.CREATE, "dummy12",12,null);
        Client client = new Client();
        client.clientRequest(packet,"bank.auth");
    }
}