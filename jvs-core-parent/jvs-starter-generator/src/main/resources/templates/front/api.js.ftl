import request from '@/router/axios'
let base = '/mgr/${moduleName}'

// 分页查询
export const pageList = (params) => {
  return request({
    url: base + `/${tableName}/page`,
    method: 'get',
    params:  params
  })
}

// 新增
export const add = (data) => {
  return request({
    url: base + `/${tableName}/save`,
    method: 'post',
    data:  data
  })
}

// 编辑
export const edit = (data) => {
  return request({
    url: base + `/${tableName}/edit`,
    method: 'put',
    data:  data
  })
}

// 删除
export const del = (data) => {
  return request({
    url: base + `/${tableName}/del`,
    method: 'delete',
    params:  data
  })
}
