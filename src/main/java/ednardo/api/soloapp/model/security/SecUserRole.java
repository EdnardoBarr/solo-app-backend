//package ednardo.api.soloapp.model.security;
//
//import org.springframework.security.core.GrantedAuthority;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//
//@Entity
//public class SecUserRole implements GrantedAuthority {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String role;
//
//    @Override
//    public String getAuthority() {
//        return "ROLE_" + this.getRole();
//    }
//
//    public String getRole() {
//        return role;
//    }
//
//    public void setRole(String role) {
//        this.role = role;
//    }
//}
