package ednardo.api.soloapp.repository;

import ednardo.api.soloapp.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    private User user;

    //Arrange
    @BeforeEach
    public void setUp() {
        user = User.builder().email("ednardobl@gmail.com")
                .password("123")
                .givenName("Ednardo")
                .surname("Barreto")
                .country("Brazil")
                .city("Rio de Janeiro")
                .dateOfBirth("06/09/1989")
                .bio("I am an easygoing person and I like doing sports.")
                .active(true)
                .pictureLocation("http://")
                .interests(List.of())
                .roles(List.of())
                .build();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }


    @Test
    public void UserRepository_FindByEmail_ReturnOptionalUser() {
        userRepository.save(user);

        //Act
        Optional<User> returnedUser = userRepository.findByEmail(user.getEmail());

        //Assert
        Assertions.assertThat(returnedUser).isPresent();
    }

    @Test
    public void UserRepository_Save_ReturnedSavedUser() {

        //Arrange
        User user = User.builder().email("ednardobl@gmail.ocom")
                .password("123")
                .givenName("Ednardo")
                .surname("Barreto")
                .country("Brazil")
                .city("Rio de Janeiro")
                .dateOfBirth("06/09/1989")
                .bio("I am an easygoing person and I like doing sports.")
                .active(true)
                .pictureLocation("http://")
                .interests(List.of())
                .roles(List.of()).build();

        //Act
        User savedUser = userRepository.save(user);

        //Assert
        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isGreaterThan(0);


    }
}
