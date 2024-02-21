package org.springbook.learningtest.factorybean;

public class Message {
    String text;

    /**
     * 생성자가 private이므로 외부에서 생성자를 통해 오브젝트를 만들 수 없다.
     * @param text
     */
    private Message( String text ) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    /**
     * 생성자 대신 사용할 수 있는 스태틱 팩토리 메소드 제공
     * @param text
     * @return
     */
    public static Message newMessage( String text ) {
        return new Message( text );
    }
}
