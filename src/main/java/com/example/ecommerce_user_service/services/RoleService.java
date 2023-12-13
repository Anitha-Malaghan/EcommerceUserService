package com.example.ecommerce_user_service.services;

import com.example.ecommerce_user_service.models.Role;
import com.example.ecommerce_user_service.repositories.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    public Role createRole(String name){
        Role role = new Role();
        role.setRole(name);

        return roleRepository.save(role);
    }
}
