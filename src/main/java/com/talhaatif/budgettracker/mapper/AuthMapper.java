package com.talhaatif.budgettracker.mapper;


import com.talhaatif.budgettracker.dto.auth.*;
import com.talhaatif.budgettracker.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRoles")
    User signupRequestToUser(SignupRequest signupRequest);

    UserResponse userToUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUserFromRequest(UpdateRequest updateRequest, @org.mapstruct.MappingTarget User user);

    @Named("mapRoles")
    default Set<String> mapRoles(Set<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of("ROLE_USER");
        }
        return roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .collect(Collectors.toSet());
    }
}