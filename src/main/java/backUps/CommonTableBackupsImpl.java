

package backUps;


import backUps.dao.BackupsDao;
import backUps.entity.BackUpsTest;
import backUps.table.ColumnAttribute;
import backUps.table.PrimaryKey;
import backUps.table.TableData;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 通用备份表实现
 * @author 志军
 */
@Service
public class CommonTableBackupsImpl extends AbstractCommonTableBackups {

    private Logger logger = Logger.getLogger(getClass());

    private DateFormat df = new SimpleDateFormat("yyyyMMdd");

    /**
     * 备份表名
     */
    private  String backupsTableName = "backups.table.name";

    /**
     * 多久备份一次
     */
    private  String backupsTime = "backups.time";

    /**
     * 配置的一次备份数量
     */
    private  String numOneTime = "backups.num.one.time";

    /**
     * 备份依据
     */
    private  String timeBasis = "backups.time.basis";

    /**
     * 每次备份数据的大小(默认为100)
     */
    private int backUpsNumOneTime = 100;


    @Autowired
    private BackupsDao backupsDao;

    @Autowired
    private Environment environment;

    /**
     * 通用模板
     */
    @Override
    public String backups() throws Exception {
        //获取表名，并判断这个表是不是存在
        String tableName = environment.getRequiredProperty(backupsTableName);
        if (!isTableExist(tableName)) {
            throw new RuntimeException("该表不存在，表名为：" + tableName);
        }
        //获取备份间隔
        int intTime = Integer.parseInt(environment.getRequiredProperty(backupsTime));
        //获取每次备份的数量
        backUpsNumOneTime = Integer.parseInt(environment.getProperty(numOneTime));

        //获取备份字段依据，这边是时间
        String basis = environment.getRequiredProperty(timeBasis);
        Date lastTime = getLogDate(tableName, basis);

        //判断当前时间和最后一次备份时间的间隔次数，确认循环次数。（可能备份过程中会失败）
        int forOperationNum = getForOperationNum(new Date(), lastTime, intTime);

        //开始循环备份，这个备份是重复失败的备份过程 //最后一个备份时间lastBackUpsTime
        forBackups(forOperationNum, lastTime, intTime, tableName, basis);
        return null;
    }

    private boolean isTableExist(String tableName) {
        List<String> strings = backupsDao.tableExistInDb(tableName);
        if (strings == null || strings.isEmpty()) {
            logger.error("表不存在");
            return false;
        }
        logger.info("表存在");
        return true;
    }

    private void forBackups(int forOperationNum, Date begin, int intTime, String tableName, String basis) throws Exception {
        Date end;
        for (int i = 0; i < forOperationNum; i++) {
            //计算最后一次成功n天之内的时间
            end = createIntervalTime(begin, intTime);
            //如果间隔时间大于目前的时间，应该break，不备份
            if (isIntervalIn(end, new Date(), intTime)) {
                break;
            }
            //创建新表表名 -- 应该判断数据库是不是有这个表
            String dbTableName = getNewTableName(tableName, df.format(end));
            //建表操作
            createSqlOperate(tableName, dbTableName);
            //对数据进行归档操作
            long total = logBackups(begin, end, tableName, dbTableName, basis);
            //完成一次备份进行备份表的更新
            
            //记录日志表
            BackUpsTest backUpsTest = new BackUpsTest();
            backUpsTest.setBackupsTable(tableName);
            backUpsTest.setBackupsTime(end);
            backUpsTest.setBackupsSaveTable(dbTableName);
            backUpsTest.setNum(total);
            insertBackupsLog(backUpsTest);
            //最后一个备份时间变化了
            begin = end;
        }
    }


    /**
     *  日志操作
     */
    @Transactional(rollbackFor = Exception.class)
    protected long logBackups(Date begin, Date end, String tableName, String dbTableName, String basis)  {
        TableData tableData = new TableData();
        //新建的表
        tableData.setTableName(dbTableName);
        //获取列的信息
        List<ColumnAttribute> attributes = backupsDao.getColumn(tableName);
        tableData.setColumnAttributes(attributes);
        long count = backupsDao.count(tableName, basis, begin, end);
        long total = backupsDao.count(tableName, basis, begin, end);
        //todo 这里应该考虑到一个数据量大的问题
        while (count > 0) {
            try {
                //TODO 查询100条数据 --查询操作
                List<Map<String, Object>> dataList = backupsDao.getDateByNum(backUpsNumOneTime, tableName, basis, begin, end);
                //插入数据到新表
                dataList.forEach(data -> {
                    insert(data, tableData);
                    //删除当前数据 --删除数据
                    delete(tableName, data);
                });
            } catch (Exception ex) {
                backupsDao.dropTable(dbTableName);
                logger.info("备份失败，进行表删除：" + dbTableName);
                throw new RuntimeException("备份出错了" + ex.getMessage(), ex);
            }
            count = backupsDao.count(tableName, basis, begin, end);
        }
        return total;
    }

    private void delete(String tableName, Map<String, Object> data) {
        List<PrimaryKey> keyList = backupsDao.getPrimaryKeyInTable(tableName);
        if (keyList == null || keyList.isEmpty()) {
            throw new RuntimeException("目前不支持没有主键的表，gun");
        }
        String primaryKey = keyList.get(0).getColumnName();
        final String[] v = {""};
        data.forEach((key, value) -> {
            if (primaryKey.equals(key)) {
                v[0] = (String) value;
            }
        });

        backupsDao.delete(tableName, primaryKey, v[0]);
    }

    private void insert(Map<String, Object> map, TableData tableData) {
        String tableName = tableData.getTableName();
        List<ColumnAttribute> attributes = tableData.getColumnAttributes();
        //把列的信息获取出来
        List<String> columns = new ArrayList<>();
        attributes.forEach(a -> columns.add(a.getColumnName()));

        //获取数据的信息
        List<Object> data = new ArrayList<>();
        //备份操作
        List<String> nullObj = new ArrayList<>();
        columns.forEach(a -> map.forEach((key, value) -> {
            if (a.equals(key)) {
                if (value != null) {
                    data.add(value);
                } else {
                    nullObj.add(a);
                }
            }
        }));
        nullObj.forEach(columns::remove);
        backupsDao.insert(columns, data, tableName);
    }


    /**
     * 根据时间获取最后一个的备份对象
     */
    private void getLastBackupsLog() {

    }

    private Date getLogDate(String tableName, String basis) {
        List<BackUpsTest> list = backupsDao.getBackupsLog(tableName);
        Date time;
        if (list == null || list.isEmpty()) {
            logger.info("备份表没有备份数据");
            List<Date> date = backupsDao.getDateFromBackUps(tableName, basis);
            if (date == null || date.isEmpty()) {
                throw new RuntimeException("主表没数据，或者sql出错，终止备份");
            }
            time = date.get(0);
        } else {
            time = list.get(0).getBackupsTime();
        }

        return time;
    }


    /**
     * 通过sql创建一张备份表
     */
    private void createSqlOperate(String oldTable, String newTable) {
        backupsDao.createTable(newTable, oldTable);
    }

    /**
     * 备份成功，记录备份日志
     */
    private void insertBackupsLog(BackUpsTest backUpsTest) {
        // jpaDao.update(backUpsTest);
    }

    //--------------------------------------get\set方法-----------------------------------------

    public String getBackupsTableName() {
        return backupsTableName;
    }

    public String getBackupsTime() {
        return backupsTime;
    }

    public String getTimeBasis() {
        return timeBasis;
    }

    public int getBackUpsNumOneTime() {
        return backUpsNumOneTime;
    }

    public void setBackUpsNumOneTime(int backUpsNumOneTime) {
        this.backUpsNumOneTime = backUpsNumOneTime;
    }

    public void setBackupsTableName(String backupsTableName) {
        this.backupsTableName = backupsTableName;
    }

    public void setBackupsTime(String backupsTime) {
        this.backupsTime = backupsTime;
    }

    public void setNumOneTime(String numOneTime) {
        this.numOneTime = numOneTime;
    }

    public void setTimeBasis(String timeBasis) {
        this.timeBasis = timeBasis;
    }
}
