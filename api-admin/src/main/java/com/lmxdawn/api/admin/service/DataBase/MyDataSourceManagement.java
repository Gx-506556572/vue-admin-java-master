package com.lmxdawn.api.admin.service.DataBase;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.basic.BasicDataSourceCreator;
import com.lmxdawn.api.admin.dao.DataBase.CompareTaskDao;
import com.lmxdawn.api.admin.entity.DataBase.DataBaseEntity;
import com.lmxdawn.api.admin.entity.DataBase.TaskDeatil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@Slf4j
public class MyDataSourceManagement {


    @Autowired
    private BasicDataSourceCreator basicDataSourceCreator;
    @Resource
   private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Resource
    private CompareTaskDao taskDao;
    /**
     * 额外数据源
     */
    private Map<String, DataSource> myDataSources = new HashMap<>();


    public DataSource createAndGetDS(DataSourceProperty dataSourceProperty, String dsID){
/*
        DataSource  dataSource = basicDataSourceCreator.createDataSource(dataSourceProperty);
            dynamicRoutingDataSource.addDataSource(dsID, dataSource);*/
           HikariConfig config = new HikariConfig();
           config.setJdbcUrl(dataSourceProperty.getUrl());
           config.setUsername(dataSourceProperty.getUsername());
           config.setPassword(dataSourceProperty.getPassword());
           config.setDriverClassName(dataSourceProperty.getDriverClassName());

           // HikariCP specific configuration
           config.setMaximumPoolSize(5); // 设置最大连接数
           config.setIdleTimeout(600000); // 设置空闲连接的最大存活时间（10分钟）
           config.setMaxLifetime(1800000); // 设置连接的最大存活时间（30分钟）
           config.setConnectionTimeout(60000); // 设置获取连接的最大等待时间（300秒）
           config.setPoolName("HikariPool-1");

           HikariDataSource dataSource = new HikariDataSource(config);
           return dataSource;

    }
    public String closeDs( String dsID){
        dynamicRoutingDataSource.removeDataSource(dsID);
        return "删除成功";
    }
    /**
     * 验证数据源是否可用。
     *
     * @param dataSource 要验证的数据源
     */
    public HashMap<String, Integer> validateDataSource(DataSource dataSource, DataBaseEntity entity) {
        switch (entity.getDatabaseType()){
            case "MYSQL":
                return mysqlTablesValidate(dataSource, entity);
            case "KINGBASE":
                return KINGBASETablesValidate(dataSource,entity);
            default:
                throw new RuntimeException("不支持的数据库类型");
        }
    }
    public HashMap<String, Integer> mysqlTablesValidate(DataSource dataSource, DataBaseEntity entity){
        HashMap<String, Integer> map = new HashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            String tableCount = "show tables ";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(tableCount)) {
                while (rs.next()) {
                    String tableName = rs.getString(1);
                    String recodsCount = "SELECT count(*) FROM " + tableName;
                    Statement st1 = conn.createStatement();
                    ResultSet rs1 =   st1.executeQuery(recodsCount); {
                        while (rs1.next()){
                            int count = rs1.getInt(1);
                            map.put(tableName, count);
                        }
                    };
                }
            }
        } catch (SQLException e) {
              e.printStackTrace();
              }

        return map;

    }

    public  HashMap<String, Integer> KINGBASETablesValidate(DataSource dataSource, DataBaseEntity entity){
        HashMap<String, Integer> map = new HashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            String tableCount = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + entity.getDatabaseName() + "'";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(tableCount)) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    String recodsCount = "SELECT count(*) FROM " + tableName;
                    Statement st1 = conn.createStatement();
                    ResultSet rs1 =   st1.executeQuery(recodsCount); {
                        while (rs1.next()){
                            int count = rs1.getInt(1);
                            map.put(tableName, count);
                        }
                    };

                }
            }
        } catch (SQLException e) {
           e.printStackTrace();
        }
        return map;
    }

    private Map<String, String> getTableStructure(Connection connection, String tableName) throws SQLException {
        Map<String, String> tableStructure = new HashMap<>();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet columns = metaData.getColumns(null, null, tableName, null);

        while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String columnType = columns.getString("TYPE_NAME");
            int columnSize = columns.getInt("COLUMN_SIZE");
            tableStructure.put(columnName, columnType + "(" + columnSize + ")");
        }
        columns.close();
        return tableStructure;
    }
    //启动多线程，每一个表创建一个线程
    public Map<String, Map<String, String>> getTableStructures(DataSource dataSource, List<String> tableNames)  {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Map<String, Future<Map<String, String>>> futures = new HashMap<>();
        int i = 1;

        for (String tableName : tableNames) {
            futures.put(tableName, executorService.submit(() -> {
                try (Connection connection = dataSource.getConnection()) {
                    return getTableStructure(connection, tableName);
                } catch (SQLException e) {
                    throw new RuntimeException("获取表结构失败: " + tableName, e);
                }
            }));
        }

        Map<String, Map<String, String>> tableStructures = new HashMap<>();
        try {
            for (Map.Entry<String, Future<Map<String, String>>> entry : futures.entrySet()) {
                try {
                    tableStructures.put(entry.getKey(), entry.getValue().get());
                } catch (ExecutionException e) {
                    System.err.println("Error getting table structure for table: " + entry.getKey());
                    e.getCause().printStackTrace();
                } catch (InterruptedException e) {
                    System.err.println("Task interrupted for table: " + entry.getKey());
                    Thread.currentThread().interrupt();
                }
            }
        } finally {
            executorService.shutdown();
        }
        return tableStructures;
    }

    // 比较两个数据库的表结构
    public void compareTableStructures(Map<String, Map<String, String>> sourceDataBase, Map<String, Map<String, String>> targetDataBase,Integer taskId) {
        ArrayList<TaskDeatil> details = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);
        // 比较两个数据库中共有表的字段结构差异
        for (String tableName : sourceDataBase.keySet()) {
            if (targetDataBase.containsKey(tableName)) {
                Map<String, String> sourceFields = sourceDataBase.get(tableName);
                Map<String, String> targetFields = targetDataBase.get(tableName);

                for (String fieldName : sourceFields.keySet()) {
                    if (targetFields.containsKey(fieldName)) {
                        String sourceField = sourceFields.get(fieldName).toLowerCase();
                        String targetField = targetFields.get(fieldName).toLowerCase();
                        if (!sourceField.equals(targetField)) {
                            StringBuilder differences = new StringBuilder();
                            TaskDeatil deatil = new TaskDeatil();
                            differences.append("表：").append(tableName).append(" 字段：").append(fieldName).append(" 结构不一致:")
                                    .append("源数据库: ").append(sourceFields.get(fieldName))
                                    .append("目标数据库: ").append(targetFields.get(fieldName));
                            deatil.setTaskType("数据库表结构不一致").setTaskDetail(differences.toString());
                            System.out.println(differences.toString());
                            details.add(deatil);
                        }
                    }
                }
            }
        }
        taskDao.insetTaskDetail(taskId, details,formattedDate);
    }


    public String validateDataSource(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            String validationQuery = "SELECT 1";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(validationQuery)) {
                if (!(rs.next() && rs.getInt(1) == 1)) {
                    throw new RuntimeException("数据源连接验证失败：查询结果不正确");
                }
                return "数据源连接验证成功";
            }
        } catch (SQLException e) {
            throw new RuntimeException("数据源连接验证失败", e);
        }
    }

}
