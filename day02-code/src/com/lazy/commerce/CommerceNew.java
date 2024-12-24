package com.lazy.commerce;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CommerceNew {
    private static final String URL = "jdbc:mysql://localhost:3306/commerce?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    // 静态代码块，用于注册驱动
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 获取数据库连接
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 处理商品结果集的方法
    private static List<Goods> processGoodsResultSet(ResultSet resultSet) throws SQLException {
        List<Goods> goodsList = new ArrayList<>();
        while (resultSet.next()) {
            Goods goods = new Goods();
            goods.setId(resultSet.getInt("id"));
            goods.setGoodsName(resultSet.getString("goods_name"));
            goods.setUnitPrice(resultSet.getDouble("unit_price"));
            goods.setStockQuantity(resultSet.getInt("stock_quantity"));
            goods.setMeasurementUnit(resultSet.getString("measurement_unit"));
            goods.setSalesVolume(resultSet.getInt("sales_volume"));
            goods.setOnShelfTime(resultSet.getTimestamp("on_shelf_time").toLocalDateTime());
            goods.setSalesStatus(resultSet.getString("sales_status"));
            goods.setCategoryId(resultSet.getInt("category_id"));
            goodsList.add(goods);
        }
        return goodsList;
    }

    // 处理分类结果集的方法
    private static List<Category> processCategoryResultSet(ResultSet resultSet) throws SQLException {
        List<Category> categoryList = new ArrayList<>();
        while (resultSet.next()) {
            Category category = new Category();
            category.setId(resultSet.getInt("id"));
            category.setCategoryName(resultSet.getString("category_name"));
            category.setSortOrder(resultSet.getInt("sort_order"));
            category.setCategoryIntroduction(resultSet.getString("category_introduction"));
            categoryList.add(category);
        }
        return categoryList;
    }

    // 打印商品列表
    public static void printGoodsList(List<Goods> goodsList) {
        System.out.printf("%-5s %-20s %-10s %-15s %-10s %-20s %-10s %-10s%n",
                "ID", "商品名称", "单价", "库存", "单位", "上架时间", "状态", "分类ID");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        for (Goods goods : goodsList) {
            System.out.printf("%-5d %-20s %-10.2f %-15d %-10s %-20s %-10s %-10d%n",
                    goods.getId(),
                    goods.getGoodsName(),
                    goods.getUnitPrice(),
                    goods.getStockQuantity(),
                    goods.getMeasurementUnit(),
                    goods.getOnShelfTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    goods.getSalesStatus(),
                    goods.getCategoryId());
        }
    }

    // 打印分类列表
    public static void printCategoryList(List<Category> categoryList) {
        System.out.printf("%-5s %-20s %-10s %-50s%n",
                "ID", "分类名称", "排序", "分类介绍");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        for (Category category : categoryList) {
            System.out.printf("%-5d %-20s %-10d %-50s%n",
                    category.getId(),
                    category.getCategoryName(),
                    category.getSortOrder(),
                    category.getCategoryIntroduction());
        }
    }

    // 增加商品
    public static void addGoods(String goodsName, double unitPrice, int stockQuantity, String measurementUnit,
                                int salesVolume, LocalDateTime onShelfTime, String salesStatus, int categoryId) {
        String sql = "INSERT INTO goods (goods_name, unit_price, stock_quantity, measurement_unit, sales_volume, on_shelf_time, sales_status, category_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, goodsName);
            preparedStatement.setDouble(2, unitPrice);
            preparedStatement.setInt(3, stockQuantity);
            preparedStatement.setString(4, measurementUnit);
            preparedStatement.setInt(5, salesVolume);
            preparedStatement.setString(6, onShelfTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            preparedStatement.setString(7, salesStatus);
            preparedStatement.setInt(8, categoryId);

            int affectedRows = preparedStatement.executeUpdate();
            System.out.println("插入成功，受影响的行数: " + affectedRows);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 获取商品列表（合并查询条件）
    public static List<Goods> getGoods(String sql, Object... params) {
        List<Goods> goodsList = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            // 设置查询参数
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                goodsList = processGoodsResultSet(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goodsList;
    }

    // 获取商品按销量排序
    public static List<Goods> getGoodsBySalesVolumeDesc() {
        String sql = "SELECT * FROM goods ORDER BY sales_volume DESC";
        return getGoods(sql);
    }

    // 获取商品按单价排序
    public static List<Goods> getGoodsByUnitPriceDesc() {
        String sql = "SELECT * FROM goods ORDER BY unit_price DESC";
        return getGoods(sql);
    }

    // 获取指定分类商品
    public static List<Goods> getGoodsByCategoryId(int categoryId) {
        String sql = "SELECT * FROM goods WHERE category_id = ?";
        return getGoods(sql, categoryId);
    }

    // 获取库存量低于5的商品
    public static List<Goods> getLowStockGoods() {
        String sql = "SELECT * FROM goods WHERE stock_quantity < 5";
        return getGoods(sql);
    }

    public static void main(String[] args) throws SQLException {
        // 示例：插入商品
        LocalDateTime onShelfTime = LocalDateTime.now();
        addGoods("商品名称", 19.99, 1, "件", 0, onShelfTime, "在售", 1);

        // 查询商品
        List<Goods> goodsList = getGoodsByCategoryId(1);
        printGoodsList(goodsList);
    }
}
