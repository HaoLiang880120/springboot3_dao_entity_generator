package individual.hl_so2.gen_dao_impl;

import individual.hl_so2.gen_entity.EntityMetadata;
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

@Component
public class DaoImplContext {

    private final static Logger LOGGER = LoggerFactory.getLogger(DaoImplContext.class);

    private final String basePackage;
    private final String outputDaoImplDir;
    private final List<DaoImplMetadata> daoImplMetadataList = new ArrayList<>();

    public DaoImplContext(@Value("${generate.base_package}") String basePackage,
                          @Value("${generate.gen_output_dir}") String outputDir) throws Exception {
        this.basePackage = basePackage;

        if (!outputDir.endsWith(File.separator)) {
            outputDir = outputDir + File.separator;
        }
        this.outputDaoImplDir = outputDir + basePackage.replace(".", File.separator) + File.separator + "dao" + File.separator + "impl";
        var dir = new File(this.outputDaoImplDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new Exception("Unable to create directory " + this.outputDaoImplDir);
            }
        }


    }

    public void init(List<EntityMetadata> entityMetadataList) {
        for (var entityMetadata : entityMetadataList) {
            this.daoImplMetadataList.add(
                    new DaoImplMetadata(this.basePackage, entityMetadata)
            );
        }
    }

    public void writeDaoImplFiles() throws Exception {
        for (var daoImplMetadata : this.daoImplMetadataList) {
            try(var writer = new FileWriter(this.outputDaoImplDir + File.separator + daoImplMetadata.getFileName(), StandardCharsets.UTF_8, true)) {
                writer.write(daoImplMetadata.toFileWriteString());
                writer.flush();
            } catch (IOException ex) {
                throw new Exception(ex);
            }
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(
                    "\n - - 共 {} 个 daoImpl 文件生成完毕, 位于目录: {}",
                    this.daoImplMetadataList.size(),
                    this.outputDaoImplDir
            );
        }
    }
}
