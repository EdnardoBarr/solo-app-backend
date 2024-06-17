package ednardo.api.soloapp.service;

import ednardo.api.soloapp.enums.RoleName;
import ednardo.api.soloapp.exception.RoleValidationException;
import ednardo.api.soloapp.model.Role;
import ednardo.api.soloapp.repository.RoleRepository;
import ednardo.api.soloapp.service.impl.RoleServiceImpl;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTests {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateRoleSuccess() {
        //Arrange
        Role role = Role.builder().roleName(RoleName.ROLE_DEFAULT).build();

        //Act
        roleService.createRole(role);

        //Assert
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    public void testCreateRoleThrowsRoleValidationException() {
        //Arrange
        Role role = Role.builder().roleName(RoleName.ROLE_DEFAULT).build();
        doThrow(new RuntimeException("Error")).when(roleRepository).save(any(Role.class));

        //Act & Assert
        Assertions.assertThrows(RoleValidationException.class, () -> roleService.createRole(role));
    }


}
