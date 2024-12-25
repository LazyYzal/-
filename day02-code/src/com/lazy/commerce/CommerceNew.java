package com.lazy.commerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CommerceNew {


    // 处理商品结果集的方法
    private static List<Goods> processGoodsResultSet(ResultSet resultSet) throws SQLException {
        List<Goods> goodsList = new ArrayList<>();
        // 处理结果集
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
        // 处理结果集
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


    // 打印商品结果列表
    public static void printGoodsList(List<Goods> goodsList) {
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        // 打印表头
        System.out.printf("%-5s %-20s %-10s %-15s %-10s %-20s %-10s %-10s%n",
                "ID", "商品名称", "单价", "库存", "单位", "上架时间", "状态", "分类ID");
        // 打印分隔线
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        // 打印商品信息
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
        System.out.println("------------------------------------------------------------------------------------------------------------------------");

    }

    // 打印分类结果列表
    public static void printCategoryList(List<Category> categoryList) {
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        // 打印表头
        System.out.printf("%-5s %-20s %-10s %-50s%n",
                "ID", "分类名称", "排序", "分类介绍");
        // 打印分隔线
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        // 打印分类信息
        for (Category category : categoryList) {
            System.out.printf("%-5d %-20s %-10d %-50s%n",
                    category.getId(),
                    category.getCategoryName(),
                    category.getSortOrder(),
                    category.getCategoryIntroduction());
        }
        System.out.println("------------------------------------------------------------------------------------------------------------------------");

    }

    // T1 向商品表（goods）中插入一条新的商品记录
    public static void addGoods(String goodsName, double unitPrice, int stockQuantity, String measurementUnit,
                                int salesVolume, LocalDateTime onShelfTime, String salesStatus, int categoryId) throws SQLException {
        // 获取数据库连接
        Connection conn = DBUtils.getConnection();

        // 创建 preparedStatement 对象
        String sql = "INSERT INTO goods (goods_name, unit_price, stock_quantity, measurement_unit, sales_volume, on_shelf_time, sales_status, category_id)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);

        // 设置参数
        preparedStatement.setString(1, goodsName);
        preparedStatement.setDouble(2, unitPrice);
        preparedStatement.setInt(3, stockQuantity);
        preparedStatement.setString(4, measurementUnit);
        preparedStatement.setInt(5, salesVolume);
        preparedStatement.setString(6, onShelfTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        preparedStatement.setString(7, salesStatus);
        preparedStatement.setInt(8, categoryId);

        // 执行更新操作
        int affectedRows = preparedStatement.executeUpdate();
        System.out.println("T1.1 插入成功，受影响的行数: " + affectedRows);

        //释放资源
        DBUtils.close(conn, preparedStatement);

    }

    // T2 根据分类 id 查询商品信息
    public static List<Goods> getGoodsByCategoryId(int categoryId) throws SQLException {
        System.out.println("查询分类id为 " + categoryId + " 的商品信息：");

        // 获取数据库连接
        Connection conn = DBUtils.getConnection();

        // 创建 preparedStatement 对象
        String sql = "SELECT * FROM goods WHERE category_id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);

        // 设置参数
        preparedStatement.setInt(1, categoryId);

        // 执行查询操作
        ResultSet resultSet = preparedStatement.executeQuery();

        // 处理结果
        List<Goods> goodsList = processGoodsResultSet(resultSet);

        // 关闭资源
        DBUtils.close(conn, preparedStatement, resultSet);

        return goodsList;
    }


    // T3 查询库存量低于 5 的商品信息
    public static List<Goods> getLowStockGoods() throws SQLException {
        System.out.println("T1.3 查询库存量低于5的商品信息：");

        // 获取数据库连接
        Connection conn = DBUtils.getConnection();

        // 创建 preparedStatement 对象
        String sql = "SELECT * FROM goods WHERE stock_quantity < 5";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);

        // 执行查询操作
        ResultSet resultSet = preparedStatement.executeQuery();

        // 处理结果
        List<Goods> goodsList = processGoodsResultSet(resultSet);

        //释放资源
        DBUtils.close(conn, preparedStatement, resultSet);

        return goodsList;
    }


    // T4 查询所有商品按照销量从高到低排序
    public static List<Goods> getGoodsBySalesVolumeDesc() throws SQLException {
        System.out.println("T1.4 查询所有商品按照销量从高到低排序：");

        // 获取数据库连接
        Connection conn = DBUtils.getConnection();
        // 创建 preparedStatement 对象
        String sql = "SELECT * FROM goods ORDER BY sales_volume DESC";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        // 执行查询操作
        ResultSet resultSet = preparedStatement.executeQuery();
        // 处理结果
        List<Goods> goodsList = processGoodsResultSet(resultSet);
        //释放资源
        DBUtils.close(conn, preparedStatement, resultSet);
        return goodsList;
    }

    // T5 查询所有商品按照单价从高到低排序
    public static List<Goods> getGoodsByUnitPriceDesc() throws SQLException {
        System.out.println("T1.5 查询所有商品按照单价从高到低排序：");

        // 获取数据库连接
        Connection conn = DBUtils.getConnection();
        // 创建 preparedStatement 对象
        String sql = "SELECT * FROM goods ORDER BY unit_price DESC";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        // 执行查询操作
        ResultSet resultSet = preparedStatement.executeQuery();
        // 处理结果
        List<Goods> goodsList = processGoodsResultSet(resultSet);
        // 释放资源
        DBUtils.close(conn, preparedStatement, resultSet);
        return goodsList;
    }

    // T6  分页查询所有商品，每页显示 5 条记录
    public static List<Goods> getGoodsByPage(int pageNum, int pageSize) throws SQLException {
        // 获取数据库连接
        Connection conn = DBUtils.getConnection();
        // 创建 preparedStatement 对象
        String sql = "SELECT * FROM goods LIMIT ? OFFSET ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        // 设置参数
        preparedStatement.setInt(1, pageSize);
        preparedStatement.setInt(2, (pageNum - 1) * pageSize);
        // 执行查询操作
        ResultSet resultSet = preparedStatement.executeQuery();
        // 处理结果
        List<Goods> goodsList = processGoodsResultSet(resultSet);
        DBUtils.close(conn, preparedStatement, resultSet);
        return goodsList;
    }

    // T7 修改商品分类
    public static void modifyGoodsCategory(int goodsId, int newCategoryId) throws SQLException {
        // 获取数据库连接
        Connection conn = DBUtils.getConnection();
        // 创建 preparedStatement 对象
        String sql = "UPDATE goods SET category_id = ? WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        // 设置参数
        preparedStatement.setInt(1, newCategoryId);
        preparedStatement.setInt(2, goodsId);
        // 执行更新操作
        int affectedRows = preparedStatement.executeUpdate();
        System.out.println(affectedRows == 1 ? "修改成功" : "修改失败");
        // 释放资源
        DBUtils.close(conn, preparedStatement);
    }


    // T2.1 往分类表（category）中插入一条新的分类记录
    public static void addCategory(String categoryName, int sortOrder, String categoryIntroduction) throws SQLException {
        // 获取数据库连接
        Connection conn = DBUtils.getConnection();
        String sql = "INSERT INTO category (category_name, sort_order, category_introduction)" +
                " VALUES (?, ?, ?)";
        // 创建 preparedStatement 对象
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        // 设置参数
        preparedStatement.setString(1, categoryName);
        preparedStatement.setInt(2, sortOrder);
        preparedStatement.setString(3, categoryIntroduction);
        // 执行插入操作
        int affectedRows = preparedStatement.executeUpdate();
        System.out.println(affectedRows == 1 ? "插入成功" : "插入失败");
        // 释放资源
        DBUtils.close(conn, preparedStatement);
    }


    // T2.2 查看所有分类
    public static List<Category> getAllCategories() throws SQLException {
        Connection conn = DBUtils.getConnection();
        String sql = "SELECT * FROM category";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);

        ResultSet resultSet = preparedStatement.executeQuery();

        List<Category> categoryList = processCategoryResultSet(resultSet);

        DBUtils.close(conn, preparedStatement, resultSet);
        return categoryList;
    }

    // T2.3 更新分类表（category）中指定分类的排序列（sort_order）
    //字段值，实现改变分类展示顺序等功能
    public static void modifyCategorySortOrder(int categoryId, int newSortOrder) throws SQLException {

        // 获取数据库连接
        Connection conn = DBUtils.getConnection();

        // 创建 preparedStatement 对象
        String sql = "UPDATE category SET sort_order = ? WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);

        // 设置参数
        preparedStatement.setInt(1, newSortOrder);
        preparedStatement.setInt(2, categoryId);

        // 执行更新操作
        int affectedRows = preparedStatement.executeUpdate();
        System.out.println("修改分类排序：" + (affectedRows == 1 ? "修改成功" : "修改失败"));

        // 释放资源
        DBUtils.close(conn, preparedStatement);
    }

    // T2.4 从分类表（category）中删除指定 id 的分类记录，同时若该分类下存在商品，
    // 则将这些商品在商品表（goods）中的分类id（category_id）字段置为空，以保证数据完整性
    public static void deleteCategory(int categoryId) throws SQLException {
        // 获取数据库连接
        Connection conn = DBUtils.getConnection();

        // 检查分类下是否有商品
        String checkSql = "SELECT * FROM goods WHERE category_id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(checkSql);
        // 设置参数
        preparedStatement.setInt(1, categoryId);
        ResultSet resultSet = preparedStatement.executeQuery();

        // 如果有商品，将商品的分类 ID 置为空
        if (resultSet.next()) {
            String updateSql = "UPDATE goods SET category_id = NULL WHERE category_id = ?";
            preparedStatement = conn.prepareStatement(updateSql);
            preparedStatement.setInt(1, categoryId);

            preparedStatement.executeUpdate();
        }
        // 删除分类
        String deleteSql = "DELETE FROM category WHERE id = ?";
        preparedStatement = conn.prepareStatement(deleteSql);
        preparedStatement.setInt(1, categoryId);
        int affectedRows = preparedStatement.executeUpdate();

        System.out.println("删除id为 " + categoryId + " 的分类：" + (affectedRows == 1 ? "删除成功" : "删除失败"));
        // 释放资源
        DBUtils.close(conn, preparedStatement, resultSet);
    }


    public static void main(String[] args) {
        try {
            // T1.1 向商品表（goods）中插入一条新的商品记录
            LocalDateTime onShelfTime = LocalDateTime.now();
            addGoods("商品名称", 19.99, 1, "件", 0, onShelfTime, "在售", 1);

            // T1.2 查询指定分类的商品信息
            List<Goods> goodsList = getGoodsByCategoryId(1);
            printGoodsList(goodsList); // 打印结果信息

            // T1.3 查询库存量低于 5 的商品信息
            List<Goods> lowStockGoodsList = getLowStockGoods();
            printGoodsList(lowStockGoodsList); // 打印结果信息

            // T1.4 查询所有商品按照销量从高到低排序
            List<Goods> goodsListDesc = getGoodsBySalesVolumeDesc();
            printGoodsList(goodsListDesc);

            // T1.5 查询所有商品按照单价从高到低排序
            List<Goods> goodsListByUnitPrice = getGoodsByUnitPriceDesc();
            printGoodsList(goodsListByUnitPrice);

            // T1.6 分页查询所有商品，每页显示 5 条记录
            getGoodsByPage(1, 5);

            // T1.7
            // T8 修改分类排序
            modifyCategorySortOrder(1, 2);

            // T9 删除指定分类
            deleteCategory(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
