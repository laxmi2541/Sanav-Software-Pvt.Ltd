package com.sanav.entity.converter;

import com.sanav.entity.Status;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status status) {
        if (status == null) {
            return null;
        }
        return status.name().toLowerCase();
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return Status.ACTIVE;
        }
        
        String normalized = dbData.trim().toUpperCase();
        for (Status status : Status.values()) {
            if (status.name().equals(normalized)) {
                return status;
            }
        }
        
        return Status.ACTIVE; // Safe default
    }
}
