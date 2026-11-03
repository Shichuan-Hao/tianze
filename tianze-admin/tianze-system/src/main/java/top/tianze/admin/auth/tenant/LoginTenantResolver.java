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

import top.tianze.admin.auth.model.req.LoginReq;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 登录租户解析扩展点
 *
 * <p>
 * 在请求未显式携带租户头时，按登录身份解析目标租户。新增策略只需新增实现类，无需修改登录主流程。
 * </p>
 *
 * @author tianze
 * @since 4.2.0
 */
public interface LoginTenantResolver {

    /**
     * 是否支持当前登录请求
     *
     * @param req     登录请求
     * @param request HTTP 请求
     * @return 是否支持
     */
    boolean supports(LoginReq req, HttpServletRequest request);

    /**
     * 解析登录应进入的租户 ID
     *
     * @param req     登录请求
     * @param request HTTP 请求
     * @return 租户 ID；返回 {@code null} 表示沿用当前租户上下文
     */
    Long resolve(LoginReq req, HttpServletRequest request);
}
