<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>cn.bctools</groupId>
        <artifactId>jvs-apply-flowable</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>${moduleName}-mgr</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <dependency>
            <groupId>cn.bctools</groupId>
            <artifactId>${moduleName}-common</artifactId>
            <version>1.0.0</version>
        </dependency>
        <dependency>
            <groupId>cn.bctools</groupId>
            <artifactId>jvs-starter-web</artifactId>
            <version>1.0.0</version>
        </dependency>
        <dependency>
            <groupId>cn.bctools</groupId>
            <artifactId>jvs-starter-oauth2</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-javadoc-plugin</artifactId>
                <version>2.9.1</version>
                <configuration>
                    <aggregate>true</aggregate>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>2.5.4</version>
            </plugin>
        </plugins>
    </build>

</project>

