//package cn.bctools.message.push.utils;
//
//import cn.bctools.auth.api.api.AuthJobServiceApi;
//import cn.bctools.auth.api.api.UserGroupServiceApi;
//import cn.bctools.auth.api.dto.SysDeptDto;
//import cn.bctools.auth.api.dto.SysJobDto;
//import cn.bctools.auth.api.dto.SysRoleDto;
//import cn.bctools.auth.api.dto.UserGroupDto;
//import cn.bctools.common.entity.dto.UserDto;
//import cn.bctools.common.utils.R;
//import cn.bctools.oauth2.utils.AuthorityManagementUtils;
//import cn.bctools.oauth2.utils.UserCurrentUtils;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
///**
// * @author admin
// * @Description: 用户相关组件 API调用
// */
//
//@Slf4j
//@Component
//@AllArgsConstructor
//public class UserComponent {
//
//    private final UserGroupServiceApi userGroupServiceApi;
//    private final AuthJobServiceApi authJobServiceApi;
//
//    /**
//     * 获取用户头像map
//     *
//     * @param userIds 用户id集合
//     */
//    public Map<String, UserDto> getUserMap(List<String> userIds) {
//        if(userIds==null || userIds.isEmpty()){
//            return new HashMap<>();
//        }
//        return AuthorityManagementUtils.getUsersByIds(userIds).stream().collect(Collectors.toMap(UserDto::getId, Function.identity()));
//    }
//
//    /**
//     * 获取角色信息
//     * @param roleIds 角色id列表
//     * @return  角色数据
//     */
//    public Map<String, SysRoleDto> getJvsRoleMap(List<String> roleIds) {
//        if(roleIds==null || roleIds.isEmpty()){
//            return new HashMap<>();
//        }
//        return AuthorityManagementUtils.getRolesByIds(roleIds).stream().collect(Collectors.toMap(SysRoleDto::getId, Function.identity()));
//    }
//
//    /**
//     * 获取部门信息
//     * @return 部门数据
//     */
//    public Map<String, SysDeptDto> getJvsDeptMap() {
//        List<SysDeptDto> deptTree = AuthorityManagementUtils.getDeptTree();
//        List<SysDeptDto> sysDeptDtoList = this.dismantleDeptTree(deptTree);
//        return sysDeptDtoList.stream().collect(Collectors.toMap(SysDeptDto::getId, Function.identity()));
//    }
//
//    /**
//     * 查询分组信息
//     * @param groupIdList 群组id
//     * @return 分组数据
//     */
//    public Map<String,UserGroupDto> getJvsGroupMap(List<String> groupIdList){
//        if(groupIdList==null || groupIdList.isEmpty()){
//            return new HashMap<>();
//        }
//        R<List<UserGroupDto>> groupList = userGroupServiceApi.getByIds(groupIdList);
//        List<UserGroupDto> userGroupDtoList = validResult(groupList, Collections.emptyList());
//        return userGroupDtoList.stream().collect(Collectors.toMap(UserGroupDto::getId, Function.identity()));
//    }
//
//    /**
//     * 查询分组信息
//     * @return 岗位数据
//     */
//    public Map<String,SysJobDto> getJvsJobMap(){
//        R<List<SysJobDto>> allJob = authJobServiceApi.getAll();
//        List<SysJobDto> sysJobDtoList = validResult(allJob, Collections.emptyList());
//        return sysJobDtoList.stream().collect(Collectors.toMap(SysJobDto::getId, Function.identity()));
//    }
//
//    /**
//     * 查询当前用户
//     * @return
//     */
//    public UserDto getCurrentUser(){
//        try {
//            return UserCurrentUtils.getCurrentUser();
//        } catch (Exception e) {
//            return new UserDto().setId("2").setRealName("系统推送");
//        }
//    }
//
//    private List<SysDeptDto> dismantleDeptTree(List<SysDeptDto> deptTree){
//        List<SysDeptDto> allDept = new ArrayList<>();
//        if(deptTree!=null){
//            for (SysDeptDto sysDeptDto : deptTree) {
//                allDept.add(sysDeptDto);
//                if(sysDeptDto.getChildList()!=null && !sysDeptDto.getChildList().isEmpty()){
//                    this.recursionDeptTree(sysDeptDto.getChildList(),allDept);
//                }
//            }
//        }
//        return allDept;
//    }
//
//    private void recursionDeptTree(List<SysDeptDto> deptTree,List<SysDeptDto> result){
//        for (SysDeptDto sysDeptDto : deptTree) {
//            result.add(sysDeptDto);
//            if(sysDeptDto.getChildList()!=null && !sysDeptDto.getChildList().isEmpty()){
//                this.recursionDeptTree(sysDeptDto.getChildList(),result);
//            }
//        }
//    }
//
//    /**
//     * 校验接口返回对象是否可用
//     *
//     * @param r 接口返回对象
//     * @return 校验结果
//     */
//    private static <T> T validResult(R<T> r, T defaultValue) {
//        if (Objects.isNull(r) || !r.is()) {
//            return defaultValue;
//        }
//        return r.getData();
//    }
//}
