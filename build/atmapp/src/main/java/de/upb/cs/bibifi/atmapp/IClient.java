package de.upb.cs.bibifi.atmapp;

import java.io.IOException;

interface IClient {
    String clientRequest (String msg) throws IOException;
}