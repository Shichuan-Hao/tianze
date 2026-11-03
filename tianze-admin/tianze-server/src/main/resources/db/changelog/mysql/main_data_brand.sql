-- liquibase formatted sql

-- changeset tianze:brand-site-title-favicon
-- comment 统一站点标题与小鹿品牌 favicon
UPDATE `sys_option`
SET `default_value` = '天泽',
    `value` = CASE
        WHEN `value` IS NULL OR `value` IN ('Tianze Admin', 'ContiNew Admin', 'Continew Admin') THEN '天泽'
        ELSE `value`
    END
WHERE `code` = 'SITE_TITLE';

UPDATE `sys_option`
SET `default_value` = '/favicon.ico?v=deer6',
    `value` = CASE
        WHEN `value` IS NULL OR `value` IN ('/favicon.ico', '/logo.svg') THEN '/favicon.ico?v=deer6'
        ELSE `value`
    END
WHERE `code` = 'SITE_FAVICON';

UPDATE `sys_option`
SET `default_value` = '/logo.png',
    `value` = CASE
        WHEN `value` IS NULL OR `value` IN ('/logo.svg') THEN '/logo.png'
        ELSE `value`
    END
WHERE `code` = 'SITE_LOGO';
