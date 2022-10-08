package com.io.sourceably.security.component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long Id;
    private String name;
    private String username;
    private Boolean active;
    private String role;
    private UserDetails userDetails;
    private String roleCode;
    private Boolean archive;
    private String context;
    private Long clientId;
    private Boolean passwordReset;

    private UserDto(String name, Long id){
        this.name=name;
        this.Id =id;
    }
}
