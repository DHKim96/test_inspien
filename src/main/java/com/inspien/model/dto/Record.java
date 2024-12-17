package com.inspien.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Record {
    @JsonProperty("Names")
    private String names;

    @JsonProperty("Phone")
    private String phone;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("BirthDate")
    private String birthday;

    @JsonProperty("Company")
    private String company;

    @JsonProperty("PersonalNumber")
    private String personalNumber;

    @JsonProperty("OrganisationNumber")
    private String organizationNumber;

    @JsonProperty("Country")
    private String country;

    @JsonProperty("Region")
    private String region;

    @JsonProperty("City")
    private String city;

    @JsonProperty("Street")
    private String street;

    @JsonProperty("ZipCode")
    private String zipCode;

    @JsonProperty("CreditCard")
    private String creditCard;

    @JsonProperty("GUID")
    private String guid;
}
