package com.lazy.database;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class JdbcDemo1 {
    private static final String URL = "jdbc:mysql://localhost:3306/db1?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";


    // 测试向t_student表中添加一条学生信息
    @Test
    public void test1() {
        try {
            // 1、加载MySQL8.0版本的数据库驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 2、 获取数据库连接
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            // 3、获取执行sql语句的对象
            Statement stmt = conn.createStatement();
            // 4、定义SQL语句：向t_student表中添加一条学生信息
            String sql = "insert into t_student values(null,'张三','男',20,'99.6')";
            // 5、执行sql语句
            int count = stmt.executeUpdate(sql);  // executeUpdate - 增删改 | excuteQuery - 查询
            // 6、处理结果
            System.out.println(count == 1 ? "添加成功" : "添加失败");// 影响行数为1
            // 7、释放资源
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 测试删除t_student表中的id为7的数据
    @Test
    public void test2() {
        // 加载mysql数据库驱动
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement stmt = conn.createStatement();
            String sql = "delete from t_student where id = 1";
            int count = stmt.executeUpdate(sql);
            System.out.println(count == 1 ? "删除成功" : "删除失败");
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 测试t_student表中的id=6的一条学生的姓名为：“万叶”
    @Test
    public void test3() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement stmt = conn.createStatement();
            String sql = "update t_student set name = '万叶' where id = 6";
            int count = stmt.executeUpdate(sql);
            System.out.println(count == 1 ? "修改成功" : "修改失败");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 测试查询所有的s_student 表中的学生信息，并处理结果
    @Test
    public void test4() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement stmt = conn.createStatement();
            String sql = "select * from t_student";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String sex = rs.getString("sex");
                int age = rs.getInt("age");
                String score = rs.getString("score");
                System.out.println("id: " + id + "\t姓名：" + name + "\t性别：" + sex + "\t年龄：" + age + "\t分数：" + score);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
