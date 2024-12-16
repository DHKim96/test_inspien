package com.inspien;

import com.inspien.controller.SoapController;

public class Main {
    public static void main(String[] args) {
        SoapController soapController = new SoapController();
        soapController.executeService("김동현", "010-5374-8549", "history8549@gmail.com");
    }
}
