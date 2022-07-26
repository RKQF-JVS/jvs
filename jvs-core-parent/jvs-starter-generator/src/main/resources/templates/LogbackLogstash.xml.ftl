<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false">
    <springProperty scope="context" name="name" source="spring.application.name"/>
    <springProperty scope="context" name="version" source="project.version"/>
    <springProperty scope="context" name="logstash.server-addr" source="logstash.server-addr"/>
    <!-- 控制台输出 -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout class="org.apache.skywalking.apm.toolkit.log.logback.v1.x.TraceIdPatternLogbackLayout">
                <pattern>%d{yyyy-MM-dd} %d{HH:mm:ss.SSS} %tid  ${r"${"}name}  [%thread] %-5level logger_name:%logger{36} - message:%msg%n
                </pattern>
            </layout>
        </encoder>
    </appender>

    <!-- 按照每天生成日志文件 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--日志文件输出的文件名-->
            <FileNamePattern>log/${r"${"}name}/Web.log.%d{yyyy-MM-dd}.log</FileNamePattern>
            <!--日志文件保留天数-->
            <MaxHistory>90</MaxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout class="org.apache.skywalking.apm.toolkit.log.logback.v1.x.TraceIdPatternLogbackLayout">
                <pattern>%d{yyyy-MM-dd} %d{HH:mm:ss.SSS} %tid  ${r"${"}name}  [%thread] %-5level logger_name:%logger{36} -   message:%msg%n
                </pattern>
            </layout>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>log/${r"${"}name}${r"${"}file.separator}%d{yyyy-MM-dd}${r"${"}file.separator}%d{yyyy-MM-dd}.%i.log.zip
            </fileNamePattern>
            <maxFileSize>200MB</maxFileSize>
        </rollingPolicy>
    </appender>

    <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>${r"${"}logstash.server-addr}</destination>
        <includeCallerData>true</includeCallerData>
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers class="net.logstash.logback.composite.loggingevent.LoggingEventJsonProviders">
                <timestamp>
                    <timeZone>Asia/Shanghai</timeZone>
                </timestamp>
                <!--自定义日志输出格式-->
                <pattern>
                    <pattern>
                        {
                        "service": "${r"${"}name}",
                        "tid": "%tid",
                        "day": "%d{\"yyyy-MM-dd\"}",
                        "thread": "%thread",
                        "level": "%level",
                        "filepath": "%class:%line",
                        "message": "%msg"
                        }
                    </pattern>
                </pattern>
            </providers>
        </encoder>
    </appender>

    <logger name="org.mybatis">
        <level value="INFO"/>
    </logger>

    <!-- 日志输出级别 -->
    <root level="info">
        <appender-ref ref="LOGSTASH"/>
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="FILE"/>
    </root>

</configuration>
