package com.sanav.entity.converter;

import com.sanav.entity.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        if (role == null) {
            return null;
        }
        return role.name().toLowerCase();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return Role.USER;
        }
        
        String normalized = dbData.trim().toUpperCase();
        for (Role role : Role.values()) {
            if (role.name().equals(normalized)) {
                return role;
            }
        }
        
        return Role.USER; // Safe default for dev environment
    }
}
