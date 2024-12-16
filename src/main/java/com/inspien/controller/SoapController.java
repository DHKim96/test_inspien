package com.inspien.controller;

import com.inspien.model.dto.User;
import com.inspien.service.SoapService;
import com.inspien.service.SoapServiceImpl;
import com.inspien.view.SoapUI;

public class SoapController {
    private final SoapService soapService;
    private final SoapUI soapUI;


    public SoapController() {
        this.soapService = new SoapServiceImpl();
        this.soapUI = new SoapUI();
    }

    private void executeService(String name, String phone, String email){

        String res = soapService.requestSoap(User.builder()
                                                .name(name)
                                                .phone(phone)
                                                .email(email)
                                                .build()
                                            );
    }

}
