package org.main.wiredspaceapi.business;

import org.main.wiredspaceapi.domain.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public void GreetUser(User user) {
        System.out.println("Greeting " + user.getName());
    }
}
