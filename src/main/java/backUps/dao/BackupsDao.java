

package backUps.dao;

import backUps.entity.BackUpsTest;
import backUps.table.ColumnAttribute;
import backUps.table.PrimaryKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface BackupsDao {

    //删除原表数据 --暂时不管
    boolean delete(@Param("tableName") String tableName, @Param("primaryKey") String primaryKey, @Param("value") String value);

    boolean batchInsert(List list);

    //这里的做法是通过传入列和数据的数组然后在里面去拼接   --插入被备份表数据
    boolean insert(@Param("columns") List<String> columns, @Param("datas") List<Object> datas, @Param("tableName") String tableName);

    //判断用户表中是否存在这个表
    List<String> tableExistInDb(@Param("tableName") String tableName);

    //从备份记录中拿一条时间排序的数据
    List<BackUpsTest> getBackupsLog(@Param("tableName") String tableName);

    /**
     * 从备份主表拿时间数据
     * @param tableName 表名
     * @param column 依据的字段名
     * @return 时间数据
     */
    List<Date> getDateFromBackUps(@Param("tableName") String tableName, @Param("column") String column);

    //建备份表操作
    boolean createTable(@Param("newTable") String newTable, @Param("oldTable") String oldTable);

    //插入日志  --暂时不管
    boolean insertLogs(BackUpsTest backUpsTest);

    //获取总数
    long count(@Param("tableName") String tableName, @Param("column") String column, @Param("begin")
            Date Begin, @Param("end") Date end);

    //拿到表结构
    List<ColumnAttribute> getColumn(@Param("tableName") String TableName);

    //查询一定条数的数据 拿主表数据
    List<Map<String, Object>> getDateByNum(@Param("num") long num, @Param("tableName") String tableName, @Param("column") String column,
                                           @Param("begin") Date Begin, @Param("end") Date end);

    //获取表的主键信息
    List<PrimaryKey> getPrimaryKeyInTable(@Param("tableName") String tableName);

    boolean dropTable(@Param("tableName") String tableName);
}
