package org.main.wiredspaceapi.security.token;

public interface TokenDecoder {
    AccessToken decode(String tokenEncoded);
}
