package com.example.hl7project.utility;

import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
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
                        if (field.getType().equals(String.class) && value instanceof LocalDateTime) {
                            field.set(dto, ((LocalDateTime) value).toString());
                        } else if (field.getType().equals(String.class) && value instanceof java.sql.Date) {
                            field.set(dto, value.toString());
                        } else if (field.getType().equals(LocalDate.class) && value instanceof java.sql.Date) {
                            field.set(dto, ((java.sql.Date) value).toLocalDate());
                        } else if (field.getType().equals(LocalDateTime.class) && value instanceof java.sql.Timestamp) {
                            field.set(dto, ((java.sql.Timestamp) value).toLocalDateTime());
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
    public static Map<String, Object> createFieldMap(Object[] row, Class<?> dtoClass) {
        Map<String, Object> fieldMap = new HashMap<>();
        Field[] fields = dtoClass.getDeclaredFields();
        if (row.length != fields.length) {
            System.out.println("Mismatch in row length and fields length.");
        }

        for (int i = 0; i < row.length; i++) {
            if (i < fields.length) {
                fieldMap.put(fields[i].getName(), row[i]);
            }
        }

        return fieldMap;
    }

}
