package com.example.eaimessage.service.bodyfield;

/**
 * 바디 빌더가 사용하는 템플릿 코드·제목·본문 표시 값.
 * <p>
 * 값은 요청 필드만으로 만들 수도 있고, {@link MessageBodyFieldResolver} 구현에서
 * DB·외부 API 등을 호출해 채울 수도 있다.
 */
public record BodyDisplayFields(String templateCode, String subject, String content) {

    public static BodyDisplayFields of(String templateCode, String subject, String content) {
        return new BodyDisplayFields(
            templateCode == null ? "" : templateCode,
            subject == null ? "" : subject,
            content == null ? "" : content
        );
    }
}
