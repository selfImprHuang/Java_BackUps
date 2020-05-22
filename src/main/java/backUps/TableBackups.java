package backUps;

import java.text.ParseException;

public interface TableBackups {

    /**
     * 进行备份操作
     * @return 返回备份完整本次的备份表名称
     */
    String backups() throws Exception;
}
