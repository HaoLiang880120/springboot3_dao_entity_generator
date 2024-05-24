package com.kryptoncell.gen_dao;

public final class DaoMetadata {

    private final String packageStatement;
    private final String className;
    private final String fileName;
    private final String entityClassName;
    private final String entityPackageName;

    public DaoMetadata(String basePackage, String entityClassName) {
        this.packageStatement = "import " + basePackage + ".dao" + ";";

        this.className = entityClassName.substring(0, entityClassName.length() - "Entity".length()) + "Dao";
        this.fileName = this.className + ".java";
        this.entityClassName = entityClassName;
        this.entityPackageName = basePackage + ".entity";
    }

    public String toFileWriteString() {
        return this.packageStatement + "\n\n" +
                // import语句
                "import " + this.entityPackageName + "." + this.entityClassName + ";\n\n" +
                // 接口定义
                "public interface " + this.className + " extends _BaseDao <Long, " + this.entityClassName + "> {\n\n" +
                "\t//todo 将非_BaseDao中的增删改查方法放到这里\n\n" +
                // 接口定义结束
                "}";
    }

    @SuppressWarnings("unused")
    public String getPackageStatement() {
        return packageStatement;
    }

    @SuppressWarnings("unused")
    public String getClassName() {
        return className;
    }

    @SuppressWarnings("unused")
    public String getFileName() {
        return fileName;
    }

    @SuppressWarnings("unused")
    public String getEntityClassName() {
        return entityClassName;
    }

    @SuppressWarnings("unused")
    public String getEntityPackageName() {
        return entityPackageName;
    }
}
