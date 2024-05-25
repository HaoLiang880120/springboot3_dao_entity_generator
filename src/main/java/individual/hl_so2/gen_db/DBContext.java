package individual.hl_so2.gen_db;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
public class DBContext {

    private final Logger LOGGER = LoggerFactory.getLogger(DBContext.class);

    /* 想要生成哪个数据库的表 */
    private final String wantGenDB;

    /* 想要生成哪些表，如果为空则为生成所有的表 */
    private final List<String> wantGenTables = new ArrayList<>();

    /* 想要生成的表的列信息， 表名 => 列信息 键值对 */
    private final Map<String, List<TableColumnMetadata>> wantGenTableColumns = new LinkedHashMap<>();

    /* 想要生成的表注释, 表名 => 表注释 */
    private final Map<String, String> wantGenTableComments = new HashMap<>();

    private final InformationSchemaDao informationSchemaDao;

    public DBContext(@Value("${generate.db.name}") String cmdDBName,
                     @Value("${generate.db.tables}") String cmdTables,
                     InformationSchemaDao informationSchemaDao) {

        this.wantGenDB = cmdDBName;
        this.informationSchemaDao = informationSchemaDao;

        if (nonNull(cmdTables) && !cmdTables.isEmpty() && !cmdTables.isBlank() && !"null".equalsIgnoreCase(cmdTables.trim())) {
            this.wantGenTables.addAll(
                    Arrays.stream(cmdTables.trim().split(",")).distinct().toList()
            );
        }

    }

    /**
     * 初始化DBContext，将要生成的表以及列信息全部加载完毕
     */
    @PostConstruct
    public void initContext() throws Exception {
        this.validateTableExists();

        if (this.wantGenTables.isEmpty()) {
            this.fetchAllTableNames();
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("\n - - 共有 {} 张表需要生成代码, 正在获取表的列信息...", this.wantGenTables.size());
        }

        this.fetchTableColumns();
        this.fetchTableComments();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("\n - - 所有表的列信息已获取完毕...");
        }
    }

    /**
     * 检查想要生成的表是否存在
     */
    private void validateTableExists() throws Exception {
        // 如果没有输入想要生成的表，那么会默认生成所有表的pojo、dao
        // 因此这里直接返回，无需验证想要生成的表是否存在
        if (this.wantGenTables.isEmpty()) {
            return;
        }

        var tablesInDB = this.informationSchemaDao.getTables(this.wantGenDB, this.wantGenTables);

        if (Objects.equals(tablesInDB.size(), this.wantGenTables.size())) {
            return;
        }

        for (var tb : this.wantGenTables) {
            if (!tablesInDB.contains(tb)) {
                throw new Exception("表 " + tb + " 不存在。");
            }
        }
    }

    /**
     * 获取所有的表名
     * 这里为了避免大量的
     */
    private void fetchAllTableNames() {
        var tableCount = this.informationSchemaDao.getAllTableCount(this.wantGenDB);
        if (0 == tableCount) {
            return;
        }

        // 一次性查询100张表的表名
        var batchSize = 100;
        for (int i = 0; i <= tableCount / batchSize; i++) {
            this.wantGenTables.addAll(
                    this.informationSchemaDao.getTables(this.wantGenDB, batchSize, i * batchSize)
            );
        }
    }

    /**
     * 获取想要生成代码的表的列信息
     */
    private void fetchTableColumns() {

        // 分批次获取，一次性只获取最多10张表的列信息
        var batchSize = 10;
        var tableCount = this.wantGenTables.size();
        for (int i = 0; i <= tableCount / batchSize; i++) {
            var subList = this.wantGenTables.subList(
                    i * batchSize,
                    Math.min(i * batchSize + batchSize, this.wantGenTables.size())
            );

            this.wantGenTableColumns.putAll(
                    this.informationSchemaDao.getColumns(this.wantGenDB, subList)
            );
        }

        // 对每张表按照表名排序
        var sorted = this.wantGenTableColumns
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        // 同时每张表的列信息按照顺序进行排序
        sorted.forEach(
                (k, v) -> v.sort(Comparator.comparingInt(TableColumnMetadata::position))
        );

        this.wantGenTableColumns.clear();
        this.wantGenTableColumns.putAll(sorted);
    }

    /**
     * 获取想要生成的表注释
     */
    private void fetchTableComments() {

        // 分批次获取，一次性获取20张表的注释
        var batchSize = 20;
        var tableCount = this.wantGenTables.size();
        for (int i = 0; i <= tableCount / batchSize; i++) {
            var subList = this.wantGenTables.subList(
                    i * batchSize,
                    Math.min(i * batchSize + batchSize, this.wantGenTables.size())
            );

            this.wantGenTableComments.putAll(
                    this.informationSchemaDao.getTableComments(this.wantGenDB, subList)
            );
        }
    }

    @SuppressWarnings("unused")
    public Map<String, List<TableColumnMetadata>> getWantGenTableColumns() {
        return wantGenTableColumns;
    }

    @SuppressWarnings("unused")
    public Map<String, String> getWantGenTableComments() {
        return wantGenTableComments;
    }
}
