package com.hack.journal.entity;

import com.hack.journal.enums.Gender;
import com.hack.journal.enums.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "_user", schema = "public")
public class User implements UserDetails {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private long Id;
    @Column(name = "full_name")
    private String fullName;
    @Column(unique = true, name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "date_of_birth")
    private Timestamp dateOfBirth;
    @Column(name = "display_image_url")
    private String displayImageUrl;
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private Role role;
    @Column(name = "is_enabled")
    private boolean enabled;
    @Column(name = "is_verified")
    private boolean verified;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<UnexpiredRevokedToken> unexpiredRevokedTokens;

//    @OneToMany(mappedBy = "user")
//    @JsonManagedReference
//    private List<Favourite> favourites;
//    @OneToMany(mappedBy = "user")
//    private List<Cart> cart;
//    @OneToMany(mappedBy = "user")
//    private List<Order> orders;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
