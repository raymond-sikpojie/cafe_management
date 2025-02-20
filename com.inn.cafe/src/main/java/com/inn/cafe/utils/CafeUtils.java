package com.inn.cafe.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inn.cafe.entities.Product;
import com.inn.cafe.entities.User;
import com.inn.cafe.models.ProductDTO;
import com.inn.cafe.models.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CafeUtils {

    private CafeUtils() {

    }

    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus) {
        return new ResponseEntity<String>(responseMessage, httpStatus);
    }

    public static UserDTO mapUserToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .role("")
                .build();
    }

    public static ProductDTO mapProductToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .status(product.getStatus())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .build();
    }

    public static String getUUID() {
        Date date = new Date();
        long time = date.getTime();

        StringBuilder sb = new StringBuilder();
        sb.append("BILL-");
        sb.append(time);

        return sb.toString();
    }

    public static JSONArray getJsonArrayFromString(String data) throws JSONException {
        return new JSONArray(data);
    }

    public static Map<String, Object> getMapFromJson(String data) {
        if (StringUtils.isNotEmpty(data)) {
            return new Gson().fromJson(data, new TypeToken<Map<String, Object>>() {}.getType());
        }
        return new HashMap<>();
    }

    public static Boolean isFilePresent(String path) {
        try {
            File file = new File(path);
            return file.exists();
        } catch (Exception e) {
            log.info("An error occurred while checking if file already exists" + e.getMessage());
            return false;
        }
    }
}
