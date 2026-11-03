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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockHttpServletRequest;
import top.tianze.admin.auth.enums.AuthTypeEnum;
import top.tianze.admin.auth.model.req.AccountLoginReq;
import top.tianze.admin.common.config.TenantExtensionProperties;
import top.tianze.admin.system.mapper.user.UserMapper;
import top.tianze.admin.system.model.entity.user.UserTenantIdentityDO;
import top.continew.starter.extension.tenant.context.TenantContextHolder;

/**
 * 登录身份租户解析器测试
 */
class IdentityLoginTenantResolverTest {

    @Test
    void shouldSkipWhenTenantHeaderPresent() {
        UserMapper userMapper = mock(UserMapper.class);
        TenantExtensionProperties properties = new TenantExtensionProperties();
        IdentityLoginTenantResolver resolver = new IdentityLoginTenantResolver(userMapper, properties);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Tenant-Code", "shop");
        AccountLoginReq req = new AccountLoginReq();
        req.setAuthType(AuthTypeEnum.ACCOUNT);
        req.setUsername("shopadmin");

        try (MockedStatic<TenantContextHolder> tenantHolder = mockStatic(TenantContextHolder.class)) {
            tenantHolder.when(TenantContextHolder::isTenantEnabled).thenReturn(true);
            assertFalse(resolver.supports(req, request));
        }
    }

    @Test
    void shouldResolveUniqueUsernameTenant() {
        UserMapper userMapper = mock(UserMapper.class);
        TenantExtensionProperties properties = new TenantExtensionProperties();
        IdentityLoginTenantResolver resolver = new IdentityLoginTenantResolver(userMapper, properties);

        UserTenantIdentityDO identity = new UserTenantIdentityDO();
        identity.setId(1L);
        identity.setUsername("shopadmin");
        identity.setTenantId(908059447757111350L);
        when(userMapper.selectTenantIdentitiesByUsername("shopadmin")).thenReturn(List.of(identity));

        MockHttpServletRequest request = new MockHttpServletRequest();
        AccountLoginReq req = new AccountLoginReq();
        req.setAuthType(AuthTypeEnum.ACCOUNT);
        req.setUsername("shopadmin");

        try (MockedStatic<TenantContextHolder> tenantHolder = mockStatic(TenantContextHolder.class)) {
            tenantHolder.when(TenantContextHolder::isTenantEnabled).thenReturn(true);
            assertTrue(resolver.supports(req, request));
            assertEquals(908059447757111350L, resolver.resolve(req, request));
        }
    }

    @Test
    void shouldReturnNullWhenUserMissing() {
        UserMapper userMapper = mock(UserMapper.class);
        TenantExtensionProperties properties = new TenantExtensionProperties();
        IdentityLoginTenantResolver resolver = new IdentityLoginTenantResolver(userMapper, properties);
        when(userMapper.selectTenantIdentitiesByUsername("nobody")).thenReturn(List.of());

        MockHttpServletRequest request = new MockHttpServletRequest();
        AccountLoginReq req = new AccountLoginReq();
        req.setAuthType(AuthTypeEnum.ACCOUNT);
        req.setUsername("nobody");

        try (MockedStatic<TenantContextHolder> tenantHolder = mockStatic(TenantContextHolder.class)) {
            tenantHolder.when(TenantContextHolder::isTenantEnabled).thenReturn(true);
            assertNull(resolver.resolve(req, request));
        }
    }

    @Test
    void shouldRejectAmbiguousUsername() {
        UserMapper userMapper = mock(UserMapper.class);
        TenantExtensionProperties properties = new TenantExtensionProperties();
        IdentityLoginTenantResolver resolver = new IdentityLoginTenantResolver(userMapper, properties);

        UserTenantIdentityDO first = new UserTenantIdentityDO();
        first.setId(1L);
        first.setUsername("dup");
        first.setTenantId(0L);
        UserTenantIdentityDO second = new UserTenantIdentityDO();
        second.setId(2L);
        second.setUsername("dup");
        second.setTenantId(2L);
        when(userMapper.selectTenantIdentitiesByUsername("dup")).thenReturn(List.of(first, second));

        MockHttpServletRequest request = new MockHttpServletRequest();
        AccountLoginReq req = new AccountLoginReq();
        req.setAuthType(AuthTypeEnum.ACCOUNT);
        req.setUsername("dup");

        try (MockedStatic<TenantContextHolder> tenantHolder = mockStatic(TenantContextHolder.class)) {
            tenantHolder.when(TenantContextHolder::isTenantEnabled).thenReturn(true);
            assertThrows(RuntimeException.class, () -> resolver.resolve(req, request));
        }
    }
}
