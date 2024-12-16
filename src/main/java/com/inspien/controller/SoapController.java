package com.inspien.controller;

import com.inspien.model.dto.User;
import com.inspien.service.SoapService;
import com.inspien.service.SoapServiceImpl;
import com.inspien.view.SoapUI;

public class SoapController {
    private final SoapService soapService;

    public SoapController() {
        this.soapService = new SoapServiceImpl();
    }

    public void executeService(String name, String phone, String email){

        String res = soapService.requestSoap(User.builder()
                                                .name(name)
                                                .phone(phone)
                                                .email(email)
                                                .build()
                                            );
        System.out.println("\nres = " + res);
        soapService.parseSoapXML(res);
    }

}
