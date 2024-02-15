package org.springbook.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springbook.user.domain.Level;
import org.springbook.user.domain.User;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig(locations = "/test-applicationContext.xml")
public class UserTest {
    User user;

    @BeforeEach
    public void setUp() {
        user = new User();
    }

    @Test
    public void upgradeLevel() {
        Level[] levels = Level.values();
        for( Level level : levels ) {
            if( level.nextLevel() == null ) continue;
            user.setLevel( level );
            user.upgradeLevel();
            assertEquals( level.nextLevel(), user.getLevel() );
        }
    }

    @Test
    public void cannotUpgradeLevel() {
        Level[] levels = Level.values();
        for( Level level : levels ) {
            if( level.nextLevel() != null ) continue;
            user.setLevel( level );
            assertThrows( IllegalStateException.class, () -> user.upgradeLevel() );
        }
    }
}
