package com.workintech.s19d2.service;

import com.workintech.s19d2.entity.Member;
import com.workintech.s19d2.repository.MemberRepository;
import com.workintech.s19d2.repository.RoleRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("authenticationService")
@Primary // Spring'in UserDetailsService çakışmasında senin servisini seçmesini sağlar
public class AuthenticationService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository; // Test sınıfı 3 parametre beklediği için ekledik
    private final PasswordEncoder passwordEncoder;

    // Test sınıfındaki (MainTest:97) çağrıya uygun constructor
    public AuthenticationService(MemberRepository memberRepository,
                                 RoleRepository roleRepository,
                                 PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member register(String email, String password) {
        Member member = new Member();
        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(password));
        // Gerekirse burada roleRepository kullanarak varsayılan rol ataması yapabilirsin
        return memberRepository.save(member);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}