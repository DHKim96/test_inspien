package com.inspien;

import com.inspien.common.ServiceManager;
import com.inspien.soap.dto.User;

public class Main {
    public static void main(String[] args) {
        User user = User.builder()
                .name("김동현")
                .phone("010-5374-8549")
                .email("history8549@gmail.com")
                .build();

        ServiceManager serviceManager = new ServiceManager();
        serviceManager.execute(user);
    }
}
