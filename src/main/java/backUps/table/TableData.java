

package backUps.table;

import java.util.List;

/**
 * 表结构属性
 * @author 志军
 */
public class TableData {

    /**
     * 表字段信息
     */
    private List<ColumnAttribute> columnAttributes;

    /**
     * 表名
     */
    private String tableName;

    public List<ColumnAttribute> getColumnAttributes() {
        return columnAttributes;
    }

    public void setColumnAttributes(List<ColumnAttribute> columnAttributes) {
        this.columnAttributes = columnAttributes;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
