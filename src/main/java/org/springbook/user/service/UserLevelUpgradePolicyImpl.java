package org.springbook.user.service;

import org.springbook.user.domain.Level;
import org.springbook.user.domain.User;

import static org.springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static org.springbook.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

public class UserLevelUpgradePolicyImpl implements UserLevelUpgradePolicy{
    @Override
    public boolean canUpgradeLevel( User user ) {
        Level currentLevel = user.getLevel();

        switch ( currentLevel ) {
            case BASIC:
                return ( user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER );
            case SILVER:
                return ( user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD );
            case GOLD:
                return false;
            default: // 현재 로직에서 다룰 수 없는 레벨인 경우 에러 발생
                throw new IllegalArgumentException( "Unknown Level: " + currentLevel );
        }
    }

    @Override
    public void upgradeLevel( User user ) {
        // 사용자 레벨을 다음 레벨로 변경
        user.upgradeLevel();

    }
}
