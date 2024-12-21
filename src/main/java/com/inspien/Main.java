package com.inspien;

import com.inspien.common.ServiceManager;
import com.inspien.soap.dto.User;

/**
 * 애플리케이션의 Main 클래스.
 * <p>
 * 사용자 정보를 생성한 뒤 {@link ServiceManager}를 통해
 * 전체 프로세스를 실행합니다.
 * </p>
 *
 * <p>
 * 주요 역할:
 * <ul>
 *     <li>사용자 정보 초기화</li>
 *     <li>{@link ServiceManager} 호출 및 전체 프로세스 실행</li>
 * </ul>
 * </p>
 */
public class Main {

    /**
     * 애플리케이션의 Main 메서드.
     * <p>
     * 사용자 정보를 생성하고 {@link ServiceManager}를 통해
     * SOAP, XML, JSON 데이터 처리 프로세스를 실행합니다.
     * </p>
     */
    public static void main(String[] args) {
        // 사용자 정보 생성
        User user = User.builder()
                .name("김동현")
                .phone("010-5374-8549")
                .email("history8549@gmail.com")
                .build();

        // ServiceManager를 이용해 전체 프로세스 실행
        ServiceManager serviceManager = new ServiceManager();
        serviceManager.execute(user);
    }
}
