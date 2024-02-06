package ru.itrum.springSecurity.task01.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {

    @NotEmpty(message = "Name could not be empty")
    @Size(min = 2, max = 100, message = "Name must be between 2 to 100 symbols")
    private String username;

    private String password;
}