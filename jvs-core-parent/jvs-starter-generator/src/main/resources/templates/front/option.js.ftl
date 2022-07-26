export const tableOption={
  page: true,
  canncal: false,
  align: 'center',
  menuAlign: 'center',
  search: true,
  cancal: false,
  showoverflow: true,
  column: [
<#list tableFields as item>
    {
      label: '${item.desc}',
      prop: '${item.name}',
    <#if item.isNum==true>
      type: 'inputNumber',
    </#if>
    },
</#list>
  ]
}