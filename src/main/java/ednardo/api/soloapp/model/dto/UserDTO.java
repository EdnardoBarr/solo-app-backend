package ednardo.api.soloapp.model.dto;

import ednardo.api.soloapp.validation.PasswordMatches;
import ednardo.api.soloapp.validation.ValidEmail;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.lang.NonNull;

@PasswordMatches
public class UserDTO {
    @ValidEmail
    @NonNull
    @NotEmpty
    private String email;
    @NonNull
    @NotEmpty
    private String firstName;
    @NonNull
    @NotEmpty
    private String lastName;
    @NonNull
    @NotEmpty
    private String password;
    @NonNull
    @NotEmpty
    private String matchingPassword;



    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    @NonNull
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NonNull String firstName) {
        this.firstName = firstName;
    }

    @NonNull
    public String getLastName() {
        return lastName;
    }

    public void setLastName(@NonNull String lastName) {
        this.lastName = lastName;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }


}
