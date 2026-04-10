package com.sanav.entity.converter;

import com.sanav.entity.MessageStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MessageStatusConverter implements AttributeConverter<MessageStatus, String> {

    @Override
    public String convertToDatabaseColumn(MessageStatus status) {
        if (status == null) {
            return null;
        }
        return status.name().toLowerCase();
    }

    @Override
    public MessageStatus convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return MessageStatus.UNREAD; // Safe default
        }
        
        String normalized = dbData.trim().toUpperCase();
        for (MessageStatus status : MessageStatus.values()) {
            if (status.name().equals(normalized)) {
                return status;
            }
        }
        
        // Handle potential numeric storage (e.g., "0" or "1" from legacy trials)
        if ("1".equals(normalized)) return MessageStatus.READ;
        if ("0".equals(normalized)) return MessageStatus.UNREAD;

        return MessageStatus.UNREAD; // Final fallback prevent 500 error
    }
}
