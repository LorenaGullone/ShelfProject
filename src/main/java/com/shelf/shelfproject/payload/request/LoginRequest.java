package com.shelf.shelfproject.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
public class LoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
