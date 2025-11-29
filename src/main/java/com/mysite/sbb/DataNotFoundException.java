package com.mysite.sbb;

/**
 * 데이터를 찾지 못했을 때 발생하는 사용자 정의 예외 클래스.
 * RuntimeException을 상속하여 언체크 예외(Unchecked Exception)로 정의.
 * 메서드 시그니처에 명시적인 예외 처리가 강제되지 않아 비즈니스 로직을 간결하게 유지.
 */
public class DataNotFoundException extends RuntimeException {

    // Serializable 인터페이스 구현 시 필요한 고유 버전 ID.
    private static final long serialVersionUID = 1L;

    /**
     * 예외 객체 생성자.
     * @param message 예외 발생 시 전달할 상세 오류 메시지.
     */
    public DataNotFoundException(String message) {
        // 부모 클래스(RuntimeException)의 생성자를 호출하여 오류 메시지를 설정.
        super(message);
    }
}