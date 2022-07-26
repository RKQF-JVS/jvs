server:
  port: ${r"${random.int[10000,12000]}"}
spring:
  application:
    name: ${r"@artifactId@"}
  main.allow-bean-definition-overriding: true
  cloud:
    nacos:
      discovery:
        server-addr: jvs-nacos:8848
        namespace: jvs
        group: ${r"${spring.cloud.nacos.discovery.namespace}"}
      config:
        group: ${r"${spring.cloud.nacos.discovery.group}"}
        file-extension: yml
        namespace: ${r"${spring.cloud.nacos.discovery.namespace}"}
        server-addr: ${r"${spring.cloud.nacos.discovery.server-addr}"}
        shared-configs:
          - dataId: application.${r"${spring.cloud.nacos.config.file-extension}"}
            refresh: true
          - dataId: ${r"${parent.artifactId}.${spring.cloud.nacos.config.file-extension}"}
            refresh: true
    inetutils:
      #选择使用此网段进行处理
      preferred-networks: 10.*
swagger:
  title: "Auto Generator"
  description: "Auto Generator"
