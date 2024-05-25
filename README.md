````
命令执行案例：
java -jar springboot3_dao_entity_generator-1.0.jar \
-host 127.0.0.1 \
-port 3306 \
-database my_test_database \
-tables mytable1,mytable2 \
-user my_db_user_name \
-password my_db_password \
-gen-base-package com.java.package \
-gen-remain-table-prefix false \
-gen-output-dir /Users/a123/Downloads



命令行选项说明：
-host 数据库地址（ip或域名）
-port 数据库端口号
-user 数据库用户名
-password 数据库密码(可选，如果数据库没有设密码，可以去掉这个选项)
-database 想要生成代码的表所在的数据库
-tables 想要生成代码的表，用英文逗号分隔（可选，如果想要生成数据库中所有的表，可以去掉这个选项）
-gen-base-package 生成的代码的根包名
-gen-remain-table-prefix 生成的entity名字是否保留t_前缀，如果保留则t_user => TUserEntity，如果不保留则t_user => UserEntity
-gen-output-dir 生成的代码文件存放的位置