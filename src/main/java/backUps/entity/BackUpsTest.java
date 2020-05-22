
package backUps.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "T_BACKUPS_TEST")
public class BackUpsTest {

    private String id;


    /**
     * 被备份表
     */
    @Column(name = "BACKUPS_TABLE")
    private String backupsTable;


    /**
     * 备份时间
     */
    @Column(name = "BACKUPS_TIME")
    private Date backupsTime;


    /**
     * 备份保存表
     */
    @Column(name = "BACKUPS_SAVE_TABLE")
    private String backupsSaveTable;


    /**
     * 备份的数量
     */
    @Column(name = "NUM")
    private long num;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBackupsTable() {
        return backupsTable;
    }

    public void setBackupsTable(String backupsTable) {
        this.backupsTable = backupsTable;
    }

    public Date getBackupsTime() {
        return backupsTime;
    }

    public void setBackupsTime(Date backupsTime) {
        this.backupsTime = backupsTime;
    }

    public String getBackupsSaveTable() {
        return backupsSaveTable;
    }

    public void setBackupsSaveTable(String backupsSaveTable) {
        this.backupsSaveTable = backupsSaveTable;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }
}
