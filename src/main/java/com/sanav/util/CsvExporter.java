package com.sanav.util;

import com.sanav.entity.ContactMessage;
import com.sanav.entity.Product;
import com.sanav.entity.User;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for generating CSV exports for admin pages.
 */
public class CsvExporter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static byte[] generateUsersCsv(List<User> users) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Name,Email,Role,Status,Last Login,Created At\n");
        for (User u : users) {
            sb.append(u.getId()).append(",");
            sb.append(escapeCsv(u.getName())).append(",");
            sb.append(escapeCsv(u.getEmail())).append(",");
            sb.append(u.getRole()).append(",");
            sb.append(u.getStatus()).append(",");
            sb.append(u.getLastLogin() != null ? u.getLastLogin().format(FORMATTER) : "Never").append(",");
            sb.append(u.getCreatedAt() != null ? u.getCreatedAt().format(FORMATTER) : "").append("\n");
        }
        return sb.toString().getBytes();
    }

    public static byte[] generateProductsCsv(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Name,Category,Price,Status,Created At\n");
        for (Product p : products) {
            sb.append(p.getId()).append(",");
            sb.append(escapeCsv(p.getName())).append(",");
            sb.append(escapeCsv(p.getCategory() != null ? p.getCategory() : "")).append(",");
            sb.append(p.getPrice()).append(",");
            sb.append(p.getStatus()).append(",");
            sb.append(p.getCreatedAt() != null ? p.getCreatedAt().format(FORMATTER) : "").append("\n");
        }
        return sb.toString().getBytes();
    }

    public static byte[] generateMessagesCsv(List<ContactMessage> messages) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Name,Email,Subject,Status,Created At\n");
        for (ContactMessage m : messages) {
            sb.append(m.getId()).append(",");
            sb.append(escapeCsv(m.getName())).append(",");
            sb.append(escapeCsv(m.getEmail())).append(",");
            sb.append(escapeCsv(m.getSubject())).append(",");
            sb.append(m.getStatus()).append(",");
            sb.append(m.getCreatedAt() != null ? m.getCreatedAt().format(FORMATTER) : "").append("\n");
        }
        return sb.toString().getBytes();
    }

    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
