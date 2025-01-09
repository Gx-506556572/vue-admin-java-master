package com.lmxdawn.api.admin.service.DataBase;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.basic.BasicDataSourceCreator;
import com.lmxdawn.api.admin.entity.DataBase.DataBaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class MyDataSourceManagement {


    @Autowired
    private BasicDataSourceCreator basicDataSourceCreator;
    @Resource
   private DynamicRoutingDataSource dynamicRoutingDataSource;
    /**
     * 额外数据源
     */
    private Map<String, DataSource> myDataSources = new HashMap<>();


    public DataSource createAndGetDS(DataSourceProperty dataSourceProperty, String dsID){

        DataSource  dataSource = basicDataSourceCreator.createDataSource(dataSourceProperty);
            dynamicRoutingDataSource.addDataSource(dsID, dataSource);

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
}
