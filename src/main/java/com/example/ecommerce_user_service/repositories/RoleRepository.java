package com.example.ecommerce_user_service.repositories;

import com.example.ecommerce_user_service.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findAllByIdIn(List<Long> roleIds);
}
