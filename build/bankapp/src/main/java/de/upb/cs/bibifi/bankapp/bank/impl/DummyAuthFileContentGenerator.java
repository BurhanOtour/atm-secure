package de.upb.cs.bibifi.bankapp.bank.impl;

import de.upb.cs.bibifi.bankapp.bank.IAuthFileContentGenerator;
import de.upb.cs.bibifi.bankapp.constants.AppConstants;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class DummyAuthFileContentGenerator implements IAuthFileContentGenerator {
    @Override
    public InputStream generateAuthFileContent() throws UnsupportedEncodingException {
        String dummyAuthFileContent = "AuthFileContent";
        return new ByteArrayInputStream(dummyAuthFileContent.getBytes(AppConstants.COMMON_CHARSET));
    }

    public static IAuthFileContentGenerator getGenerator(){
        return new DummyAuthFileContentGenerator();
    }
}
