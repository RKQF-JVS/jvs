<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>cn.bctools</groupId>
        <artifactId>jvs</artifactId>
        <version>2.1.0</version>
    </parent>

    <groupId>cn.bctools</groupId>
    <artifactId>${moduleName}</artifactId>
    <version>2.1.0</version>
    <packaging>pom</packaging>

    <modules>
        <module>${moduleName}-common</module>
        <module>${moduleName}-mgr</module>
    </modules>

</project>
