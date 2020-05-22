

package backUps;

import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static backUps.Constant.DATE_FORMATE;
import static backUps.Constant.UNDERLINE;

/**
 * @author 志军
 * 表备份通用抽象类
 */
public abstract class AbstractCommonTableBackups implements TableBackups {


    /**
     * 每日秒数
     */
    private final long oneDaySeconds = 24 * 60 * 60 * 1000;

    @Override
    public String backups() throws Exception {
       throw new RuntimeException("i have no realization");
    }


    /**
     * 通过被备份的表名和其他字段获得到新的表名
     */
    String getNewTableName(String tableName, String... strings) {
        if (StringUtils.isEmpty(tableName)) {
            throw new RuntimeException("表名不能为空");
        }
        List<String> list = Arrays.asList(strings);
        StringBuilder name = new StringBuilder();
        name.append(tableName);
        list.forEach((s) -> name.append(UNDERLINE).append(s));
        return name.toString();
    }

    /**
     * 判断是否在被执行时间内
     */
    boolean isIntervalIn(Date endTime, Date beginTime, int interval) throws ParseException {
        DateFormat df = new SimpleDateFormat(DATE_FORMATE);
        long end = df.parse(df.format(endTime)).getTime();
        long begin = df.parse(df.format(beginTime)).getTime();
        long date = (end - begin) / oneDaySeconds;
        return date > interval;
    }


    /**
     * 通过当前时间获取到下一刻的执行时间
     */
    Date createIntervalTime(Date begin, int interval) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(begin);
        calendar.add(Calendar.DAY_OF_MONTH, interval);
        return calendar.getTime();
    }


    /**
     *  通过时间判断得到应该执行的次数
     */
    int getForOperationNum(Date endTime, Date beginTime, int interval) throws ParseException {
        DateFormat df = new SimpleDateFormat(DATE_FORMATE);
        long end = df.parse(df.format(endTime)).getTime();
        long begin = df.parse(df.format(beginTime)).getTime();
        long date = (end - begin) / oneDaySeconds;
        int result = (int) (date / interval);
        int remainder = (int) (date % interval);
        if (result == 0) {
            return 0;
        }
        return (remainder == 0 ? result : result + 1);
    }

    /**
     * 根据数据量确定一定的备份时间
     */
    String createBackupsTime() {
        return null;
    }

    /**
     * 根据获取到的备份时间，将属性写入到properties文件
     */
    void storeBackupsTime(String backupsTime) {

    }
}
