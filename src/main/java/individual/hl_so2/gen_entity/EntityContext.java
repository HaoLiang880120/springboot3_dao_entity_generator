package individual.hl_so2.gen_entity;

import individual.hl_so2.gen_db.TableColumnMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class EntityContext {

    private final static Logger LOGGER = LoggerFactory.getLogger(EntityContext.class);

    private final String basePackage;
    private final Boolean remainEntityNameTablePrefix;
    private final String outputDir;
    private final List<EntityMetadata> entityMetadataList = new ArrayList<>();

    public EntityContext(@Value("${generate.base_package}") String basePackage,
                         @Value("${generate.remain_table_prefix}") Boolean remainEntityNameTablePrefix,
                         @Value("${generate.gen_output_dir}") String outputDir) throws Exception {
        this.basePackage = basePackage;
        this.remainEntityNameTablePrefix = remainEntityNameTablePrefix;

        // 输出目录为根目录 + 包名
        if (!outputDir.endsWith(File.separator)) {
            outputDir = outputDir + File.separator;
        }
        this.outputDir = outputDir + basePackage.replace('.', File.separatorChar) + File.separator + "entity";
        // 如果目录不存在，则新建目录
        var dir = new File(this.outputDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new Exception(this.outputDir + " 创建目录失败.");
            }
        }
    }

    public void init(Map<String, List<TableColumnMetadata>> tablesInfo, Map<String, String> tablesComment) {
        tablesInfo.forEach((table, columns) -> {
            var tableComment = tablesComment.get(table);

            entityMetadataList.add(
                    new EntityMetadata(
                            this.basePackage,
                            remainEntityNameTablePrefix,
                            table,
                            tableComment,
                            columns
                    )
            );
        });
    }

    public void writeEntityFiles() throws Exception {
        for (var entityMetadata : entityMetadataList) {
            try(var writer = new FileWriter(this.outputDir + File.separator + entityMetadata.getEntityFileName(), StandardCharsets.UTF_8, true)) {
                writer.write(entityMetadata.toFileWriteString());
                writer.flush();
            } catch (IOException ex) {
                throw new Exception(ex);
            }
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(
                    "\n - - 共 {} 个 entity 文件生成完毕, 位于目录: {}",
                    entityMetadataList.size(),
                    this.outputDir + File.separator
            );
        }
    }

    @SuppressWarnings("unused")
    public List<EntityMetadata> getEntityMetadataList() {
        return entityMetadataList;
    }
}
