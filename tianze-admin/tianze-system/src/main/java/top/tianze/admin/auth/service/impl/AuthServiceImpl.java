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

package top.tianze.admin.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.tianze.admin.auth.LoginHandler;
import top.tianze.admin.auth.LoginHandlerFactory;
import top.tianze.admin.auth.enums.AuthTypeEnum;
import top.tianze.admin.auth.model.req.LoginReq;
import top.tianze.admin.auth.model.resp.LoginResp;
import top.tianze.admin.auth.model.resp.RouteResp;
import top.tianze.admin.auth.service.AuthService;
import top.tianze.admin.auth.tenant.LoginTenantResolver;
import top.tianze.admin.common.context.RoleContext;
import top.tianze.admin.common.enums.DisEnableStatusEnum;
import top.tianze.admin.system.constant.SystemConstants;
import top.tianze.admin.system.enums.MenuTypeEnum;
import top.tianze.admin.system.model.resp.ClientResp;
import top.tianze.admin.system.model.resp.MenuResp;
import top.tianze.admin.system.service.ClientService;
import top.tianze.admin.system.service.MenuService;
import top.tianze.admin.system.service.RoleService;
import top.continew.starter.core.exception.BadRequestException;
import top.continew.starter.core.util.validation.ValidationUtils;
import top.continew.starter.extension.crud.annotation.TreeField;
import top.continew.starter.extension.crud.autoconfigure.CrudProperties;
import top.continew.starter.extension.tenant.util.TenantUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 认证业务实现
 *
 * @author Charles7c
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final LoginHandlerFactory loginHandlerFactory;
    private final ClientService clientService;
    private final RoleService roleService;
    private final MenuService menuService;
    private final CrudProperties crudProperties;
    private final List<LoginTenantResolver> loginTenantResolvers;

    @Override
    public LoginResp login(LoginReq req, HttpServletRequest request, HttpServletResponse response) {
        AuthTypeEnum authType = req.getAuthType();
        // 校验客户端
        ClientResp client = clientService.getByClientId(req.getClientId());
        if (client == null) {
            throw new BadRequestException("客户端不存在");
        }
        ValidationUtils.throwIf(DisEnableStatusEnum.DISABLE.equals(client.getStatus()), "客户端已禁用");
        ValidationUtils.throwIf(!client.getAuthType().contains(authType.getValue()),
            "该客户端暂未授权 [{}] 认证", authType
                .getDescription());
        // 获取处理器
        LoginHandler<LoginReq> loginHandler = loginHandlerFactory.getHandler(authType);
        if (loginHandler == null) {
            throw new BadRequestException("不支持的认证类型 [" + authType.getDescription() + "]");
        }
        // 登录前置处理（验证码等与租户无关）
        loginHandler.preLogin(req, client, request);
        // 未显式指定租户时，通过扩展点解析目标租户后再执行登录
        Long resolvedTenantId = resolveLoginTenantId(req, request);
        if (resolvedTenantId != null) {
            AtomicReference<LoginResp> loginRespRef = new AtomicReference<>();
            TenantUtils.execute(resolvedTenantId, () -> {
                LoginResp loginResp = loginHandler.login(req, client, request, response);
                loginHandler.postLogin(req, client, request);
                loginRespRef.set(loginResp);
            });
            return loginRespRef.get();
        }
        LoginResp loginResp = loginHandler.login(req, client, request, response);
        loginHandler.postLogin(req, client, request);
        return loginResp;
    }

    private Long resolveLoginTenantId(LoginReq req, HttpServletRequest request) {
        if (CollUtil.isEmpty(loginTenantResolvers)) {
            return null;
        }
        return loginTenantResolvers.stream()
            .filter(resolver -> resolver.supports(req, request))
            .map(resolver -> resolver.resolve(req, request))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    @Override
    public List<RouteResp> buildRouteTree(Long userId) {
        Set<RoleContext> roleSet = roleService.listByUserId(userId);
        if (CollUtil.isEmpty(roleSet)) {
            return new ArrayList<>(0);
        }
        // 查询菜单列表
        Set<MenuResp> menuSet = new LinkedHashSet<>();
        if (roleSet.stream().anyMatch(r -> SystemConstants.SUPER_ADMIN_ROLE_ID.equals(r.getId()))) {
            menuSet.addAll(menuService.listByRoleId(SystemConstants.SUPER_ADMIN_ROLE_ID));
        } else {
            roleSet.forEach(r -> menuSet.addAll(menuService.listByRoleId(r.getId())));
        }
        List<MenuResp> menuList =
            menuSet.stream().filter(m -> !MenuTypeEnum.BUTTON.equals(m.getType())).toList();
        if (CollUtil.isEmpty(menuList)) {
            return new ArrayList<>(0);
        }
        // 构建路由树
        TreeField treeField = MenuResp.class.getDeclaredAnnotation(TreeField.class);
        TreeNodeConfig treeNodeConfig =
            crudProperties.getTreeDictModel().genTreeNodeConfig(treeField);
        List<Tree<Long>> treeList =
            TreeUtil.build(menuList, treeField.rootId(), treeNodeConfig, (m, tree) -> {
                tree.setId(m.getId());
                tree.setParentId(m.getParentId());
                tree.setName(m.getTitle());
                tree.setWeight(m.getSort());
                tree.putExtra("type", m.getType().getValue());
                tree.putExtra("path", m.getPath());
                tree.putExtra("name", m.getName());
                tree.putExtra("component", m.getComponent());
                tree.putExtra("redirect", m.getRedirect());
                tree.putExtra("icon", m.getIcon());
                tree.putExtra("isExternal", m.getIsExternal());
                tree.putExtra("isCache", m.getIsCache());
                tree.putExtra("isHidden", m.getIsHidden());
                tree.putExtra("permission", m.getPermission());
            });
        return BeanUtil.copyToList(treeList, RouteResp.class);
    }
}
