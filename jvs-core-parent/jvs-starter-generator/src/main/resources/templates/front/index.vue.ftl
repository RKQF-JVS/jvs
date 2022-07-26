<template>
  <div class="table-show">
    <zebra-table
      :pageheadertitle='pageheadertitle'
      :option="option"
      :data="tableData"
      :loading="tableLoading"
      :page="page"
      @on-load="getList"
      @search-change="searchChange"
      @addRow="addRowHandle"
      @editRow="editRowHandle"
      @delRow="delRowHandle"
    ></zebra-table>
  </div>
</template>
<script>
import { tableOption } from './option'
import { pageList, add, edit, del } from './api'
export default {
  props: {
    propData: {
      type: Object
    }
  },
  data () {
    return {
      pageheadertitle: '${tableInfo}', // fixed me  表名
      tableLoading: false, // loading显示
      tableData: [], // 列表数据
      option: tableOption,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 20, // 每页显示多少条
      },
      queryParams: {}, // 查询条件
    }
  },
  created () { },
  methods: {
    // 表格分页查询
    getList (page) {
      let obj={
        size: this.page.pageSize,
        current: this.page.currentPage
      }
      pageList(Object.assign(obj, this.queryParams)).then(res => {
        if (res.data.code==0) {
          this.tableData=res.data.data.records
          this.page.currentPage=res.data.data.current
          this.page.total=res.data.data.total
        }
      })
    },
    // 条件查询
    searchChange (form) {
      this.queryParams=form
      this.getList()
    },
    // 新增
    addRowHandle (form) {
      add(form).then(res => {
        if(res.data.code == 0) {
          this.$message.success('新增成功')
          this.getList()
        }
      })
    },
    // 编辑
    editRowHandle (form) {
      edit(form).then(res => {
        if(res.data.code == 0) {
          this.$message.success('编辑成功')
          this.getList()
        }
      })
    },
    // 删除
    delRowHandle (row) {
      del(row).then(res => {
        if(res.data.code == 0) {
          this.$message.success('删除成功')
          this.getList()
        }
      })
    }
  }
}
</script>
<style lang="scss"></style>