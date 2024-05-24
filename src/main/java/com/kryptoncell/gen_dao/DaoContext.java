package com.kryptoncell.gen_dao;

import com.kryptoncell.gen_entity.EntityMetadata;
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
public class DaoContext {

    private final static Logger LOGGER = LoggerFactory.getLogger(DaoContext.class);

    private final String basePackage;
    private final String daoFileDir;
    private final BaseDaoMetadata baseDaoMetadata;
    private final List<DaoMetadata> daoMetadataList = new ArrayList<>();

    public DaoContext(@Value("${generate.base_package}") String basePackage,
                      @Value("${generate.gen_output_dir}") String outputDir) throws Exception {
        this.basePackage = basePackage;
        this.baseDaoMetadata = new BaseDaoMetadata(basePackage);

        if (!outputDir.endsWith(File.separator)) {
            outputDir = outputDir + File.separator;
        }
        this.daoFileDir = outputDir + basePackage.replace('.', File.separatorChar) + File.separator + "dao";
        var daoDir = new File(daoFileDir);
        if (!daoDir.exists()) {
            if (!daoDir.mkdirs()) {
                throw new Exception("Unable to create directory " + daoFileDir);
            }
        }
    }

    public void init(List<EntityMetadata> entityMetadataList) {
        for (var entityMetadata : entityMetadataList) {
            var daoImplMetadata = new DaoMetadata(this.basePackage, entityMetadata.getEntityClassName());
            this.daoMetadataList.add(daoImplMetadata);
        }
    }

    public void writeBaseDaoFile() throws Exception {
        try(var writer = new FileWriter(this.daoFileDir + File.separator + "_BaseDao.java", StandardCharsets.UTF_8,true)) {
            writer.write(this.baseDaoMetadata.toFileWriteString());
            writer.flush();
        } catch (IOException ex) {
            throw new Exception(ex);
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("\n - - 共 1 个 _BaseDao.java 文件生成完毕, 位于目录: {}", this.daoFileDir + File.separator);
        }
    }

    public void writeDaoFiles() throws Exception {
        for (var daoMetadata : this.daoMetadataList) {
            try(var writer = new FileWriter(this.daoFileDir + File.separator + daoMetadata.getFileName(), StandardCharsets.UTF_8, true)) {
                writer.write(daoMetadata.toFileWriteString());
                writer.flush();
            } catch (IOException ex) {
                throw new Exception(ex);
            }
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(
                    "\n - - 共 {} 个 dao 文件生成完毕, 位于目录: {}",
                    this.daoMetadataList.size(),
                    this.daoFileDir + File.separator
            );
        }
    }
}
