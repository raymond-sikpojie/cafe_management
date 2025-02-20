package com.inn.cafe.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDTO {
    private int id;

    private String name;
    private String description;
    private int price;
    private String status;
    private int categoryId;
    private String categoryName;

}
