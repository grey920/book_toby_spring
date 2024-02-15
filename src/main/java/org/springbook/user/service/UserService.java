package org.springbook.user.service;

import com.sun.java.accessibility.util.EventID;
import org.springbook.user.domain.User;

public interface UserService {
    void add( User  user );
    void upgradeLevels();
}
