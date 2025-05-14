package org.main.wiredspaceapi.security.token;

public interface TokenEncoder{
    String encode(AccessToken token);
}
