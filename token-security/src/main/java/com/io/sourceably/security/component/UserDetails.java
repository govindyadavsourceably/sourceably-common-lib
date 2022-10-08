package com.io.sourceably.security.component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails {

    private  Long id;


    private String title;

    private String firstName;

    private String middleName;

    private String lastName;

    private String suffix;

    private String preName;

    private String email;

    private String mobile;

    private String orgUnit;

    private String manager;

    private String gender;

    private String birthDate;

    private String occupation;

    private String bloodGroup;

    private String maritalStatus;

    private String address;

    private String city;

    private String country;

    private String state;

    private String phone1;

    private String phone2;

    private String notes;

    private String userPic     ;
    private String countryCode;

}