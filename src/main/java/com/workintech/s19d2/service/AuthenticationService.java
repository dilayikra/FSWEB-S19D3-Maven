package com.workintech.s19d2.service;

import com.workintech.s19d2.entity.Member;
import com.workintech.s19d2.entity.Role;
import com.workintech.s19d2.repository.MemberRepository;
import com.workintech.s19d2.repository.RoleRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("authenticationService")
@Primary
public class AuthenticationService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(MemberRepository memberRepository,
                                 RoleRepository roleRepository,
                                 PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member register(String email, String password) {
        // Email check for test: registerMemberThrowsExceptionWhenEmailExists
        Optional<Member> foundMember = memberRepository.findByEmail(email);
        if (foundMember.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        // Role "ADMIN" for test: registerNewMemberSuccessfully
        Role adminRole = roleRepository.findByAuthority("ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setAuthority("ADMIN");
            return roleRepository.save(role);
        });

        List<Role> roles = new ArrayList<>();
        roles.add(adminRole);

        Member member = new Member();
        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(password));
        member.setRoles(roles);

        return memberRepository.save(member);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}