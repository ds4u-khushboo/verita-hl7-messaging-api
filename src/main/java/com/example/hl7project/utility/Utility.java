package com.example.hl7project.utility;

import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Utility {

    public LocalDateTime hl7DateToDateTime(String date) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);

        return dateTime;
    }

    public String formatToHL7DateTime(LocalDateTime localDateTime) {
        DateTimeFormatter hl7Formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return localDateTime.format(hl7Formatter);
    }

    public static <T> List<T> mapToDto(List<Object[]> results, Class<T> dtoClass) {
        List<T> dtoList = new ArrayList<>();

        try {
            Field[] fields = dtoClass.getDeclaredFields();

            for (Object[] row : results) {
                T dto = dtoClass.getDeclaredConstructor().newInstance();

                for (int i = 0; i < row.length && i < fields.length; i++) {
                    Field field = fields[i];
                    field.setAccessible(true);
                    Object value = row[i];

                    if (value != null) {
                        Class<?> fieldType = field.getType();

                        if (fieldType.equals(String.class) && !(value instanceof String)) {
                            field.set(dto, value.toString());
                        } else if (fieldType.equals(Long.class)) {
                            if (value instanceof String) {
                                String stringValue = (String) value;
                                if (stringValue.matches("\\d+")) {
                                    field.set(dto, Long.valueOf(stringValue));
                                } else {
                                    System.err.println("Cannot convert non-numeric string to Long: " + stringValue);
                                }
                            } else if (value instanceof Number) {
                                field.set(dto, ((Number) value).longValue());
                            }
                        } else if (fieldType.equals(Integer.class) && value instanceof Number) {
                            field.set(dto, ((Number) value).intValue());
                        } else {
                            field.set(dto, value);
                        }
                    }
                }

                dtoList.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dtoList;
    }
}
