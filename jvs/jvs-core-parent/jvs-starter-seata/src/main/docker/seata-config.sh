#!/bin/sh

echo "config convert start..."

# registry.conf
registryPath="/seata-server/conf/registry.conf"

# registried by nacos
echo "registry {
  type = \"nacos\"
  loadBalance = \"RandomLoadBalance\"
  loadBalanceVirtualNodes = 10

  nacos {
    application = \"$registry_nacos_application\"
    serverAddr = \"$registry_nacos_serverAddr\"
    group = \"$registry_nacos_group\"
    namespace = \"$registry_nacos_namespace\"
    cluster = \"$registry_nacos_cluster\"
  }
}" >> $registryPath

cat $registryPath

# config.properties
configPath="/seata-server/conf/config.properties"

touch $configPath

echo "
transport.type=$transport_type
transport.server=$transport_server
transport.heartbeat=$transport_heartbeat
transport.enableClientBatchSendRequest=$transport_enableClientBatchSendRequest
transport.threadFactory.bossThreadPrefix=$transport_threadFactory_bossThreadPrefix
transport.threadFactory.workerThreadPrefix=$transport_threadFactory_workerThreadPrefix
transport.threadFactory.serverExecutorThreadPrefix=$transport_threadFactory_serverExecutorThreadPrefix
transport.threadFactory.shareBossWorker=$transport_threadFactory_shareBossWorker
transport.threadFactory.clientSelectorThreadPrefix=$transport_threadFactory_clientSelectorThreadPrefix
transport.threadFactory.clientSelectorThreadSize=$transport_threadFactory_clientSelectorThreadSize
transport.threadFactory.clientWorkerThreadPrefix=$transport_threadFactory_clientWorkerThreadPrefix
transport.threadFactory.bossThreadSize=$transport_threadFactory_bossThreadSize
transport.threadFactory.workerThreadSize=$transport_threadFactory_workerThreadSize
transport.shutdown.wait=$transport_shutdown_wait
service.vgroupMapping.my_test_tx_group=$service_vgroupMapping_my_test_tx_group
service.default.grouplist=$service_default_grouplist
service.enableDegrade=$service_enableDegrade
service.disableGlobalTransaction=$service_disableGlobalTransaction
client.rm.asyncCommitBufferLimit=$client_rm_asyncCommitBufferLimit
client.rm.lock.retryInterval=$client_rm_lock_retryInterval
client.rm.lock.retryTimes=$client_rm_lock_retryTimes
client.rm.lock.retryPolicyBranchRollbackOnConflict=$client_rm_lock_retryPolicyBranchRollbackOnConflict
client.rm.reportRetryCount=$client_rm_reportRetryCount
client.rm.tableMetaCheckEnable=$client_rm_tableMetaCheckEnable
client.rm.sqlParserType=$client_rm_sqlParserType
client.rm.reportSuccessEnable=$client_rm_reportSuccessEnable
client.rm.sagaBranchRegisterEnable=$client_rm_sagaBranchRegisterEnable
client.tm.commitRetryCount=$client_tm_commitRetryCount
client.tm.rollbackRetryCount=$client_tm_rollbackRetryCount
client.tm.defaultGlobalTransactionTimeout=$client_tm_defaultGlobalTransactionTimeout
client.tm.degradeCheck=$client_tm_degradeCheck
client.tm.degradeCheckAllowTimes=$client_tm_degradeCheckAllowTimes
client.tm.degradeCheckPeriod=$client_tm_degradeCheckPeriod
store.mode=$store_mode
store.file.dir=$store_file_dir
store.file.maxBranchSessionSize=$store_file_maxBranchSessionSize
store.file.maxGlobalSessionSize=$store_file_maxGlobalSessionSize
store.file.fileWriteBufferCacheSize=$store_file_fileWriteBufferCacheSize
store.file.flushDiskMode=$store_file_flushDiskMode
store.file.sessionReloadReadSize=$store_file_sessionReloadReadSize
store.db.datasource=$store_db_datasource
store.db.dbType=$store_db_dbType
store.db.driverClassName=$store_db_driverClassName
store.db.url=$store_db_url
store.db.user=$store_db_user
store.db.password=$store_db_password
store.db.minConn=$store_db_minConn
store.db.maxConn=$store_db_maxConn
store.db.globalTable=$store_db_globalTable
store.db.branchTable=$store_db_branchTable
store.db.queryLimit=$store_db_queryLimit
store.db.lockTable=$store_db_lockTable
store.db.maxWait=$store_db_maxWait
store.redis.host=$store_redis_host
store.redis.port=$store_redis_port
store.redis.maxConn=$store_redis_maxConn
store.redis.minConn=$store_redis_minConn
store.redis.maxTotal=$store_redis_maxTotal
store.redis.database=$store_redis_database
store.redis.queryLimit=$store_redis_queryLimit
store.redis.password=$store_redis_password
server.recovery.committingRetryPeriod=$server_recovery_committingRetryPeriod
server.recovery.asynCommittingRetryPeriod=$server_recovery_asynCommittingRetryPeriod
server.recovery.rollbackingRetryPeriod=$server_recovery_rollbackingRetryPeriod
server.recovery.timeoutRetryPeriod=$server_recovery_timeoutRetryPeriod
server.maxCommitRetryTimeout=$server_maxCommitRetryTimeout
server.maxRollbackRetryTimeout=$server_maxRollbackRetryTimeout
server.rollbackRetryTimeoutUnlockEnable=$server_rollbackRetryTimeoutUnlockEnable
client.undo.dataValidation=$client_undo_dataValidation
client.undo.logSerialization=$client_undo_logSerialization
client.undo.onlyCareUpdateColumns=$client_undo_onlyCareUpdateColumns
server.undo.logSaveDays=$server_undo_logSaveDays
server.undo.logDeletePeriod=$server_undo_logDeletePeriod
client.undo.logTable=$client_undo_logTable
client.undo.compress.enable=$client_undo_compress_enable
client.undo.compress.type=$client_undo_compress_type
client.undo.compress.threshold=$client_undo_compress_threshold
log.exceptionRate=$log_exceptionRate
transport.serialization=$transport_serialization
transport.compressor=$transport_compressor
metrics.enabled=$metrics_enabled
metrics.registryType=$metrics_registryType
metrics.exporterList=$metrics_exporterList
metrics.exporterPrometheusPort=$metrics_exporterPrometheusPort
" >> $configPath

cat $configPath

echo "config convert success!"

#echo "start seata with ${seata_server_host}:${seata_server_port}"

#/seata-server/bin/seata-server.sh --host ${seata_server_host} --port ${seata_server_port}

