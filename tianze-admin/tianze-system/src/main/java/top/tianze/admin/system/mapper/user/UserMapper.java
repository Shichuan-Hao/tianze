/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.tianze.admin.system.mapper.user;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.tianze.admin.common.base.mapper.DataPermissionMapper;
import top.tianze.admin.system.model.entity.user.UserDO;
import top.tianze.admin.system.model.entity.user.UserTenantIdentityDO;
import top.tianze.admin.system.model.resp.user.UserDetailResp;
import top.continew.starter.encrypt.field.annotation.FieldEncrypt;
import top.continew.starter.extension.datapermission.annotation.DataPermission;

import java.util.List;

/**
 * 用户 Mapper
 *
 * @author Charles7c
 * @since 1.0.0
 */
public interface UserMapper extends DataPermissionMapper<UserDO> {

    /**
     * 分页查询列表
     *
     * @param page         分页条件
     * @param queryWrapper 查询条件
     * @return 分页列表信息
     */
    @DataPermission(tableAlias = "t1")
    IPage<UserDetailResp> selectUserPage(@Param("page") IPage<UserDO> page,
        @Param(Constants.WRAPPER) QueryWrapper<UserDO> queryWrapper);

    /**
     * 查询列表
     *
     * @param queryWrapper 查询条件
     * @return 列表信息
     */
    @DataPermission(tableAlias = "t1")
    List<UserDetailResp> selectUserList(
        @Param(Constants.WRAPPER) QueryWrapper<UserDO> queryWrapper);

    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    UserDO selectByUsername(@Param("username") String username);

    /**
     * 跨租户按用户名查询身份（忽略租户行级过滤，仅用于登录租户解析）
     *
     * @param username 用户名
     * @return 用户租户身份列表
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT id, username, tenant_id FROM sys_user WHERE username = #{username} AND deleted = 0")
    List<UserTenantIdentityDO> selectTenantIdentitiesByUsername(@Param("username") String username);

    /**
     * 根据手机号查询
     *
     * @param phone 手机号
     * @return 用户信息
     */
    @Select("SELECT * FROM sys_user WHERE phone = #{phone} AND deleted = 0")
    UserDO selectByPhone(@FieldEncrypt @Param("phone") String phone);

    /**
     * 跨租户按手机号查询身份（忽略租户行级过滤，仅用于登录租户解析）
     *
     * @param phone 手机号
     * @return 用户租户身份列表
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT id, username, tenant_id FROM sys_user WHERE phone = #{phone} AND deleted = 0")
    List<UserTenantIdentityDO> selectTenantIdentitiesByPhone(@FieldEncrypt @Param("phone") String phone);

    /**
     * 根据邮箱查询
     *
     * @param email 邮箱
     * @return 用户信息
     */
    @Select("SELECT * FROM sys_user WHERE email = #{email} AND deleted = 0")
    UserDO selectByEmail(@FieldEncrypt @Param("email") String email);

    /**
     * 跨租户按邮箱查询身份（忽略租户行级过滤，仅用于登录租户解析）
     *
     * @param email 邮箱
     * @return 用户租户身份列表
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT id, username, tenant_id FROM sys_user WHERE email = #{email} AND deleted = 0")
    List<UserTenantIdentityDO> selectTenantIdentitiesByEmail(@FieldEncrypt @Param("email") String email);

    /**
     * 根据 ID 查询昵称
     *
     * @param id ID
     * @return 昵称
     */
    @Select("SELECT nickname FROM sys_user WHERE id = #{id} AND deleted = 0")
    String selectNicknameById(@Param("id") Long id);
}
