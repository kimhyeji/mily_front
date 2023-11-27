package com.mily.user;

import com.mily.payment.Payment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Setter
@Component
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@DynamicInsert
@DynamicUpdate
@SuperBuilder
public class MilyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String userLoginId;

    private String userPassword;

    private String userName;

    @Column(unique = true)
    private String userEmail;

    @Column(unique = true)
    private String userPhoneNumber;

    private String userDateOfBirth;

    private LocalDateTime userCreateDate;

    @Column(nullable = false)
    @ColumnDefault("0")
    public int milyPoint;

    @OneToMany(mappedBy = "customerName", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments;

    /* 모든 유저는 회원 가입 시에 기본 Role 이 member 가 됩니다. */
    @Column(nullable = false)
    @ColumnDefault("'member'")
    public String role;
    /* role : member, waiting, lawyer, admin */

    @OneToOne(mappedBy = "milyUser", cascade = CascadeType.REMOVE)
    public LawyerUser lawyerUser;

    public boolean isAdmin() {
        return "admin999".equals(userLoginId);
    }

    public List<? extends GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        // 모든 멤버는 member 권한을 가집니다.
        grantedAuthorities.add(new SimpleGrantedAuthority("member"));

        // userLoginId 가 admin인 회원은 admin 권한도 가집니다.
        if (isAdmin()) {
            grantedAuthorities.add(new SimpleGrantedAuthority("admin"));
        }
        return grantedAuthorities;
    }
}

// 아이디 찾기는 팝업이 아닌 페이지 내에서 알려주는 걸로 진행.
// 이메일로 인증했을시 맞는 아이디 찾아주기
// 비밀번호 찾기는 이메일 인증을 통해서 비밀번호를 변경할 수 있는 권한을 준다.