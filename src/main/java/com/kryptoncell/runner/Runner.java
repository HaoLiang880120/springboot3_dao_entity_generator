package com.kryptoncell.runner;

import com.kryptoncell.gen_dao.DaoContext;
import com.kryptoncell.gen_db.DBContext;
import com.kryptoncell.gen_entity.EntityContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    private final DBContext dbContext;
    private final EntityContext entityContext;
    private final DaoContext daoContext;

    public Runner(DBContext dbContext, EntityContext entityContext, DaoContext daoContext) {
        this.dbContext = dbContext;
        this.entityContext = entityContext;
        this.daoContext = daoContext;
    }

    @Override
    public void run(String... args) throws Exception {

        /* 生成entity文件 */
        this.entityContext.init(
                this.dbContext.getWantGenTableColumns(),
                this.dbContext.getWantGenTableComments()
        );
        this.entityContext.writeEntityFiles();

        /* 生成_BaseDao以及entity dao文件 */
        this.daoContext.init(
                this.entityContext.getEntityMetadataList()
        );
        this.daoContext.writeBaseDaoFile();
        this.daoContext.writeDaoFiles();


    }
}
