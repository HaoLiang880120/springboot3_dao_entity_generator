package com.kryptoncell.runner;

import com.kryptoncell.gen_db.DBContext;
import com.kryptoncell.gen_entity.EntityContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    private final DBContext dbContext;
    private final EntityContext entityContext;

    public Runner(DBContext dbContext, EntityContext entityContext) {
        this.dbContext = dbContext;
        this.entityContext = entityContext;
    }

    @Override
    public void run(String... args) throws Exception {

        /* 生成entity文件 */
        this.entityContext.init(
                this.dbContext.getWantGenTableColumns(),
                this.dbContext.getWantGenTableComments()
        );
        this.entityContext.writeEntityFiles();

    }
}
