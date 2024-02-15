package org.springbook.user.domain;

public enum Level {

    // BASIC부터 선언하면 SILVER가 없는 상태이므로 Illegal forward reference 에러 발생
    GOLD(3, null ), SILVER(2, GOLD ), BASIC(1, SILVER );

    private final int value;
    private final Level next; // 다음 단계 레벨 정보

    /**
     * DB에 저장할 값을 넣어줄 생성자
     * @param value
     */
    Level( int value, Level next ) {
        this.value = value;
        this.next = next;
    }

    public int intValue() {
        return value;
    }

    public Level nextLevel() {
        return this.next;
    }
    /**
     * 값으로부터 Level 타입 오브젝트를 가져오도록 만든 스태틱 메소드
     * @param value
     * @return
     */
    public static Level valueOf( int value ) {
        switch ( value ) {
            case 1:
                return BASIC;
            case 2:
                return SILVER;
            case 3:
                return GOLD;
            default:
                throw new AssertionError( "Unknown value: " + value );
        }
    }
}
