package com.lazy.commerce;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goods {
    private int id;
    private String goodsName;
    private double unitPrice;
    private int stockQuantity;
    private String measurementUnit;
    private int salesVolume;
    private LocalDateTime onShelfTime;
    private String salesStatus;
    private int categoryId;


}
