package ednardo.api.soloapp.controller;

import ednardo.api.soloapp.model.Role;
import ednardo.api.soloapp.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    RoleService roleService;

    @GetMapping("/{id}")
    public ResponseEntity getRole(@PathVariable Long id) {
        Role role = roleService.getRoleById(id).orElseThrow(EntityNotFoundException::new);
        try {
            return ResponseEntity.ok(role);
        } catch (EntityNotFoundException exception) {
            return new ResponseEntity<>("Role has not been found.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/create")
    public ResponseEntity createRole(@RequestBody Role role) {
        this.roleService.createRole(role);
        return new ResponseEntity<>("Role has been created.", HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity updateRole(@RequestBody Role role) {
        this.roleService.updateRole(role);
        return new ResponseEntity<>("Role has been updated.", HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteRole(@PathVariable Long id) {
        this.roleService.deleteRoleById(id);
        return new ResponseEntity<>("Role has been deleted.", HttpStatus.NO_CONTENT);
    }
}
