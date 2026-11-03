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

package top.tianze.admin.auth.tenant;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.tianze.admin.auth.enums.AuthTypeEnum;
import top.tianze.admin.auth.model.req.AccountLoginReq;
import top.tianze.admin.auth.model.req.EmailLoginReq;
import top.tianze.admin.auth.model.req.LoginReq;
import top.tianze.admin.auth.model.req.PhoneLoginReq;
import top.tianze.admin.common.config.TenantExtensionProperties;
import top.tianze.admin.system.mapper.user.UserMapper;
import top.tianze.admin.system.model.entity.user.UserTenantIdentityDO;
import top.continew.starter.core.util.validation.ValidationUtils;
import top.continew.starter.extension.tenant.annotation.ConditionalOnEnabledTenant;
import top.continew.starter.extension.tenant.context.TenantContextHolder;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 基于登录身份的租户解析器
 *
 * <p>
 * 当请求未显式携带租户 ID/编码时，按用户名、手机号或邮箱跨租户定位唯一账号所属租户，
 * 从而支持登录页仅输入账号密码。若请求已显式指定租户，则不介入，保持原有行为。
 * </p>
 *
 * @author tianze
 * @since 4.2.0
 */
@Order(100)
@Component
@ConditionalOnEnabledTenant
@RequiredArgsConstructor
public class IdentityLoginTenantResolver implements LoginTenantResolver {

    private final UserMapper userMapper;
    private final TenantExtensionProperties tenantExtensionProperties;

    @Override
    public boolean supports(LoginReq req, HttpServletRequest request) {
        if (!TenantContextHolder.isTenantEnabled()) {
            return false;
        }
        // 显式租户头优先，避免覆盖域名绑定或手工指定的租户上下文
        return !hasExplicitTenantHeader(request);
    }

    @Override
    public Long resolve(LoginReq req, HttpServletRequest request) {
        List<UserTenantIdentityDO> identities = listIdentities(req);
        if (CollUtil.isEmpty(identities)) {
            // 找不到账号时不切换租户，交由原登录流程返回统一错误信息
            return null;
        }
        ValidationUtils.throwIf(identities.size() > 1, "存在多个同名账号，请联系管理员通过专属域名登录");
        Long tenantId = identities.get(0).getTenantId();
        if (tenantId == null) {
            return tenantExtensionProperties.getDefaultTenantId();
        }
        return tenantId;
    }

    private List<UserTenantIdentityDO> listIdentities(LoginReq req) {
        AuthTypeEnum authType = req.getAuthType();
        if (AuthTypeEnum.ACCOUNT.equals(authType) && req instanceof AccountLoginReq accountLoginReq) {
            String username = StrUtil.trim(accountLoginReq.getUsername());
            if (StrUtil.isBlank(username)) {
                return List.of();
            }
            return userMapper.selectTenantIdentitiesByUsername(username);
        }
        if (AuthTypeEnum.PHONE.equals(authType) && req instanceof PhoneLoginReq phoneLoginReq) {
            String phone = StrUtil.trim(phoneLoginReq.getPhone());
            if (StrUtil.isBlank(phone)) {
                return List.of();
            }
            return userMapper.selectTenantIdentitiesByPhone(phone);
        }
        if (AuthTypeEnum.EMAIL.equals(authType) && req instanceof EmailLoginReq emailLoginReq) {
            String email = StrUtil.trim(emailLoginReq.getEmail());
            if (StrUtil.isBlank(email)) {
                return List.of();
            }
            return userMapper.selectTenantIdentitiesByEmail(email);
        }
        return List.of();
    }

    private boolean hasExplicitTenantHeader(HttpServletRequest request) {
        String tenantId = request.getHeader("X-Tenant-Id");
        String tenantCode = request.getHeader(tenantExtensionProperties.getTenantCodeHeader());
        return StrUtil.isNotBlank(tenantId) || StrUtil.isNotBlank(tenantCode);
    }
}
