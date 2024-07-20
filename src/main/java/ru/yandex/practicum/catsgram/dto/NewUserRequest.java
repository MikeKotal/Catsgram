package ru.yandex.practicum.catsgram.dto;

import lombok.Data;

@Data
public class NewUserRequest {
    private String username;
    private String password;
    private String email;
}
