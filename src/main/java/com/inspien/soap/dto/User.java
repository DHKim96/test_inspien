package com.inspien.soap.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String name;
    private String phone;
    private String email;
}
