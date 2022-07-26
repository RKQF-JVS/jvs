package cn.bctools.generator.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.generator.entity.DatabaseConfig;
import cn.bctools.generator.entity.TableColumnInfo;
import cn.bctools.generator.entity.TableInfo;
import com.zaxxer.hikari.util.DriverDataSource;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据源代码生成工具
 *
 * @author GuoZi
 **/
@Slf4j
public class GenCodeUtil {

    private GenCodeUtil() {
    }

    private static final String REG_MODULE_NAME = "(0-9a-z-)*";
    private static final Configuration CONFIGURATION = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

    private static final String TEMPLATE_APPLICATION = "/templates/Java_Application.java.ftl";
    private static final String TEMPLATE_MAPPER = "/templates/Java_Mapper.java.ftl";
    private static final String TEMPLATE_ENTITY = "/templates/Java_Entity.java.ftl";
    private static final String TEMPLATE_SERVICE = "/templates/Java_Service.java.ftl";
    private static final String TEMPLATE_POM_COMMON = "/templates/Pom_Common.xml.ftl";
    private static final String TEMPLATE_CONTROLLER = "/templates/Java_Controller.java.ftl";
    private static final String TEMPLATE_SERVICE_IMPL = "/templates/Java_ServiceImpl.java.ftl";
    private static final String TEMPLATE_POM_MGR = "/templates/Pom_Mgr.xml.ftl";
    private static final String TEMPLATE_POM = "/templates/Pom_Parent.xml.ftl";
    private static final String TEMPLATE_DOCKERFILE = "/templates/Dockerfile.ftl";
    private static final String LOGBACK_LOGSTASH = "/templates/LogbackLogstash.xml.ftl";
    private static final String BOOTSTRAP = "/templates/Bootstrap.yml.ftl";
    private static final String FRONT_API_JS = "/templates/front/api.js.ftl";
    private static final String FRONT_INDEX_VUE = "/templates/front/index.vue.ftl";
    private static final String FRONT_OPTION_JS = "/templates/front/option.js.ftl";

    static {
        CONFIGURATION.setDefaultEncoding("UTF-8");
        CONFIGURATION.setClassForTemplateLoading(GenCodeUtil.class, "/");
    }

    private static String sql = "SELECT\n" +
            "\tt.TABLE_NAME AS tableName,\n" +
            "\tc.COLUMN_NAME AS fieldName,\n" +
            "\tt.TABLE_SCHEMA AS tableSchema ,\n" +
            "\tt.table_comment as tableInfo,\n" +
            "\n" +
            "c.DATA_TYPE as type,\n" +
            "c.COLUMN_COMMENT as columnComment,\n" +
            "c.COLUMN_KEY as primaryKey,\n" +
            "c.COLUMN_COMMENT as columnComment\n" +
            "FROM\n" +
            "\tinformation_schema.`TABLES` t\n" +
            "\tINNER JOIN information_schema.`COLUMNS` c ON t.TABLE_SCHEMA = c.TABLE_SCHEMA \n" +
            "\tAND t.TABLE_NAME = c.TABLE_NAME  \n" +
            "\tAND t.TABLE_SCHEMA = ";

    @SneakyThrows
    public static void main(String[] args) {

        String data = "jvs-team-work";
        String url = "jdbc:mysql://10.0.0.123:3306/" + data + "?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true";
        String name = "root";
        String password = "root";
        String driverClassName = "com.mysql.cj.jdbc.Driver";
        String moduleName = "teamwork";

        Db db = new Db(new DriverDataSource(url, driverClassName, new Properties(), name, password));
        Map<String, List<TableColumnInfo>> tableColumnInfosMap = db.query(sql.trim() + "'" + data + "'", TableColumnInfo.class)
                .stream()
                .peek(e -> e.setName(com.baomidou.mybatisplus.core.toolkit.StringUtils.underlineToCamel(e.getTableName())))
                .peek(e -> {
                    e.setPrimaryKey(e.getFieldName().equals("id"));
                    Class index = DbColumnType.index(e.getType());
                    if (ObjectNull.isNotNull(index)) {
                        e.setDataType(index);
                    } else {
                        //匹配枚举
                        e.setDataType(String.class);
                    }
                })
                .collect(Collectors.groupingBy(TableColumnInfo::getTableName));


        List<TableInfo> tableInfos = tableColumnInfosMap.keySet()
                .stream()
                .map(e -> new TableInfo().setInfo(tableColumnInfosMap.get(e).get(0).getTableInfo()).setTableName(e).setColumns(tableColumnInfosMap.get(e)))
                .collect(Collectors.toList());

        generateCode(moduleName, tableInfos);

//
//
//        // 数据表字段信息
//        List<TableColumnInfo> tableColumnInfos = new ArrayList<>();
//        tableColumnInfos.add(new TableColumnInfo().setName("id").setPrimary(true).setColumnComment("主键").setDataType(String.class));
//        tableColumnInfos.add(new TableColumnInfo().setName("name").setPrimary(false).setColumnComment("实例名称").setDataType(String.class));
//        tableColumnInfos.add(new TableColumnInfo().setName("createTime").setPrimary(false).setColumnComment("创建时间").setDataType(Date.class));
//        tableColumnInfos.add(new TableColumnInfo().setName("updateTime").setPrimary(false).setColumnComment("修改时间").setDataType(LocalDateTime.class));
//        // 数据表信息
//        TableInfo tableInfo = new TableInfo();
//        tableInfo.setTableName("process_instance_info");
//        tableInfo.setInfo("流程实例信息");
//        tableInfo.setColumns(tableColumnInfos);
//        List<TableInfo> tableInfos = new ArrayList<>();
//        tableInfos.add(tableInfo);
//
    }

    /**
     * 文件转http响应
     *
     * @param file 文件对象
     * @return http响应对象
     */
    public static ResponseEntity<byte[]> file2Response(File file) {
        try (FileInputStream in = IoUtil.toStream(file)) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=".concat(URLEncoder.encode("源码生成.zip", "UTF-8")))
                    .body(IoUtil.readBytes(in));
        } catch (IOException e) {
            return null;
        } finally {
            FileUtil.del(file);
        }
    }

    /**
     * 生成源代码文件
     *
     * @param moduleName 模块名称(数字,小写字母,减号)
     * @param list       数据表集合
     * @return 文件对象
     */
    public static void generateCode(String moduleName, List<TableInfo> list) {
        if (StringUtils.isBlank(moduleName)) {
            log.error("模块名称不能为空");
            return;
        }
//        if (!moduleName.matches(REG_MODULE_NAME)) {
//            log.error("模块名称异常, 只能包含数字,小写字母,减号");
//            return null;
//        }
        String path = String.join(".", moduleName.toLowerCase().split("-"));
        String upperCamelCase = StrUtil.upperFirst(StrUtil.toCamelCase(moduleName.replace("-", "_")).replace("_", ""));
        List<DatabaseConfig.TableInfo> tableInfos = list.stream().map(table -> {
            DatabaseConfig.TableInfo tableInfo = new DatabaseConfig.TableInfo();
            // 因为需要根据表名生成驼峰命名,需要把表名统一转小写后再处理
            tableInfo.setTableInfo(table.getInfo())
                    .setTableName(table.getTableName().toLowerCase())
                    .setEntityName(StrUtil.upperFirst(StrUtil.toCamelCase(table.getTableName()).replace("_", "")))
                    .setTableFields(
                            table.getColumns().stream().map(column -> {
                                DatabaseConfig.TableFields tableFields = new DatabaseConfig.TableFields();
                                Class<?> typeClz = column.getDataType();
                                String timeType = "";
                                if (Date.class.equals(typeClz)) {
                                    timeType = "Date";
                                } else if (LocalTime.class.equals(typeClz)) {
                                    timeType = "LocalTime";
                                } else if (LocalDate.class.equals(typeClz)) {
                                    timeType = "LocalDate";
                                } else if (LocalDateTime.class.equals(typeClz)) {
                                    timeType = "LocalDateTime";
                                }
                                tableFields.setFieldName(column.getFieldName()).setName(StrUtil.lowerFirst(StrUtil.toCamelCase(column.getFieldName()).replace("_", ""))).setDesc(column.getColumnComment())
                                        .setIsPri(column.getPrimaryKey())
                                        .setIsLogicDel("del_flag".equals(column.getName()))
                                        .setIsNum(ClassUtil.isAssignable(Number.class, typeClz))
                                        .setType(Optional.ofNullable(typeClz).map(Class::getSimpleName).orElse(String.class.getSimpleName()))
                                        .setTimeType(timeType);
                                return tableFields;
                            }).collect(Collectors.toList())
                    );
            return tableInfo;
        }).collect(Collectors.toList());
        DatabaseConfig config = new DatabaseConfig();
        config.setModuleName(moduleName)
                .setUpperCamelCase(upperCamelCase)
                .setRootPkg("cn.bctools.".concat(path))
                .setTableInfos(tableInfos);
        GenCodeUtil.writeZip(config);
//        return
    }

    /**
     * 数据源代码生成
     *
     * @param config 配置
     * @return zip
     */
    public static void writeZip(DatabaseConfig config) {
        final String moduleName = config.getModuleName();
        final String upperCamelCase = config.getUpperCamelCase();
        String srcPath = String.format("/temp/%s", moduleName);
        try {
            Map<String, Object> objectMap = BeanUtil.beanToMap(config, false, false);
            String basePkgDir = config.getRootPkg().replace(".", "/");
            writer(objectMap, TEMPLATE_POM, mkdirs(String.format("/temp/%s/pom.xml", moduleName)));
            writer(objectMap, TEMPLATE_POM_MGR, mkdirs(String.format("/temp/%s/%s-mgr/pom.xml", moduleName, moduleName)));
            writer(objectMap, TEMPLATE_POM_COMMON, mkdirs(String.format("/temp/%s/%s-common/pom.xml", moduleName, moduleName)));

            writer(objectMap, BOOTSTRAP, mkdirs(String.format("/temp/%s/%s-mgr/src/main/resources/bootstrap.yml", moduleName, moduleName)));
            writer(objectMap, LOGBACK_LOGSTASH, mkdirs(String.format("/temp/%s/%s-mgr/src/main/resources/logback-logstash.xml", moduleName, moduleName)));
            writer(objectMap, TEMPLATE_DOCKERFILE, mkdirs(String.format("/temp/%s/%s-mgr/Dockerfile", moduleName, moduleName)));
            writer(objectMap, TEMPLATE_APPLICATION, mkdirs(String.format("/temp/%s/%s-mgr/src/main/java/%s/%s.java", moduleName, moduleName, basePkgDir, upperCamelCase + "MgrApplication")));

            config.getTableInfos().forEach(t -> t
                    .setModuleName(moduleName)
                    .setRootPkg(config.getRootPkg()));
            config.getTableInfos().forEach(f -> {
                Map<String, Object> vales = BeanUtil.beanToMap(f, false, false);
                writer(vales, TEMPLATE_CONTROLLER, mkdirs(String.format("/temp/%s/%s-mgr/src/main/java/%s/controller/%s.java", moduleName, moduleName, basePkgDir, f.getEntityName() + "Controller")));
                writer(vales, TEMPLATE_SERVICE, mkdirs(String.format("/temp/%s/%s-common/src/main/java/%s/service/%s.java", moduleName, moduleName, basePkgDir, f.getEntityName() + "Service")));
                writer(vales, TEMPLATE_SERVICE_IMPL, mkdirs(String.format("/temp/%s/%s-common/src/main/java/%s/service/impl/%s.java", moduleName, moduleName, basePkgDir, f.getEntityName() + "ServiceImpl")));
                writer(vales, TEMPLATE_MAPPER, mkdirs(String.format("/temp/%s/%s-common/src/main/java/%s/mapper/%s.java", moduleName, moduleName, basePkgDir, f.getEntityName() + "Mapper")));
                writer(vales, TEMPLATE_ENTITY, mkdirs(String.format("/temp/%s/%s-common/src/main/java/%s/entity/%s.java", moduleName, moduleName, basePkgDir, f.getEntityName())));
                writer(vales, FRONT_API_JS, mkdirs(String.format("/temp/%s/front/%s-ui/api.js", moduleName, f.getEntityName())));
                writer(vales, FRONT_INDEX_VUE, mkdirs(String.format("/temp/%s/front/%s-ui/index.vue", moduleName, f.getEntityName())));
                writer(vales, FRONT_OPTION_JS, mkdirs(String.format("/temp/%s/front/%s-ui/option.js", moduleName, f.getEntityName())));
            });
//            return ZipUtil.zip(srcPath, StandardCharsets.UTF_8);
        } finally {
//            FileUtil.del(srcPath);
        }
    }

    /**
     * 〈创建目录〉
     *
     * @param path 文件路径 或 目录路径
     * @return path 原样返回
     */
    private static String mkdirs(String path) {
        String directory;
        if (FileUtil.isDirectory(path)) {
            directory = path;
        } else {
            int index = path.lastIndexOf("/");
            directory = path.substring(0, index);
        }
        File dir = new File(directory);
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            if (result) {
                log.debug("创建目录： [ {} ]", directory);
            }
        }
        return path;
    }

    /**
     * 〈写数据到文件里〉
     *
     * @param objectMap    模板变量数据
     * @param outputFile   输出目录
     * @param templatePath 模板路径
     */
    @SneakyThrows
    private static void writer(Map<String, Object> objectMap, String templatePath, String outputFile) {
        Template template = CONFIGURATION.getTemplate(templatePath);
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            template.process(objectMap, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
        }
        log.debug("文件:{}", outputFile);
    }

}
