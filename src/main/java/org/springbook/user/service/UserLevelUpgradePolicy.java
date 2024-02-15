package org.springbook.user.service;

import org.springbook.user.domain.User;

public interface UserLevelUpgradePolicy {
    boolean canUpgradeLevel( User user );
    void upgradeLevel( User user );
}
