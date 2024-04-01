package ednardo.api.soloapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldErrorDTO {

    private String fieldName;
    private String message;

}