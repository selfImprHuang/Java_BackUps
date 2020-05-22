

package backUps.table;

/**
 *  数据库表的列属性
 * @author 志军
 */
public class ColumnAttribute {

    /**
     * 列名
     */
    private String columnName;

    /**
     * 列的数据属性
     */
    private String dateType;

    /**
     * 规定列的数据长度
     */
    private String dateLength;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getDateLength() {
        return dateLength;
    }

    public void setDateLength(String dateLength) {
        this.dateLength = dateLength;
    }
}
