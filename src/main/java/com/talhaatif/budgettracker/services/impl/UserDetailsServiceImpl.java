package com.talhaatif.budgettracker.services.impl;

import com.talhaatif.budgettracker.entities.User;
import com.talhaatif.budgettracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;



import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUserName(username);
        if (u != null) {

            Set<SimpleGrantedAuthority> authorities = u.getRoles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());

            return org.springframework.security.core.userdetails.User.builder()
                    .username(u.getUsername())
                    .password(u.getPassword())
                    .authorities(authorities)
                    .build();
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }


}
