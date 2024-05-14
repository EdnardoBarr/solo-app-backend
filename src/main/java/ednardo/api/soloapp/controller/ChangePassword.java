package ednardo.api.soloapp.controller;

import ednardo.api.soloapp.model.dto.ChangePasswordDTO;
import ednardo.api.soloapp.service.ChangePasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/password")
public class ChangePassword {
    @Autowired
    ChangePasswordService changePasswordService;

    @PostMapping("/change")
    public ResponseEntity changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        this.changePasswordService.changePassword(changePasswordDTO);
        return ResponseEntity.noContent().build();
    }

}
