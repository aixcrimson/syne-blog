#!/bin/bash
# SSL 证书续期脚本（webroot 模式，配合 nginx 容器内的 ACME 验证路径使用）
#
# 前置条件：
#   1. nginx/conf.d/cyneblog.top.conf 的 80 端口 server 已配置
#      location ^~ /.well-known/acme-challenge/ { root /var/www/certbot; }
#   2. docker-compose.yml 的 nginx 服务已挂载 ./nginx/certbot:/var/www/certbot
#   3. 首次签发使用：
#      certbot certonly --webroot -w /opt/syne-blog/nginx/certbot \
#        -d cyneblog.top -d www.cyneblog.top -d admin.cyneblog.top
#
# 原理：certbot renew 只在证书临近过期（30 天内）时真正续期；
#      --deploy-hook 仅在证书实际更新后触发，未续期时不会复制/重启。
#      本脚本以自身作为 deploy-hook（--deploy 模式），避免依赖 shell 函数导出。
#
# 用法：cron 每 12 小时执行一次，例如：
#   0 */12 * * * /opt/syne-blog/ssl-renew.sh >> /var/log/ssl-renew.log 2>&1

set -euo pipefail

# 项目与证书路径（按实际部署位置调整）
PROJECT_DIR="/opt/syne-blog"
LE_LIVE_DIR="/etc/letsencrypt/live/cyneblog.top"
NGINX_SSL_DIR="${PROJECT_DIR}/nginx/ssl"
NGINX_CONTAINER="syne-blog-nginx"

# ---- deploy-hook 模式：证书真正更新后由 certbot 调用 ----
if [ "${1:-}" = "--deploy" ]; then
    # certbot 通过 RENEWED_LINEAGE 传入新证书目录，兜底用默认路径
    lineage="${RENEWED_LINEAGE:-${LE_LIVE_DIR}}"

    # 校验新证书有效性，避免把坏证书推给 nginx
    if [ ! -f "${lineage}/fullchain.pem" ] || ! openssl x509 -checkend 86400 -noout -in "${lineage}/fullchain.pem"; then
        echo "错误：新证书缺失或校验失败，保持现有证书不变" >&2
        exit 1
    fi

    # 复制新证书到 Nginx 目录
    cp "${lineage}/fullchain.pem" "${NGINX_SSL_DIR}/"
    cp "${lineage}/privkey.pem" "${NGINX_SSL_DIR}/"

    # 校验 nginx 配置后再重载，配置有问题时 nginx 仍持有旧证书继续服务
    docker exec "${NGINX_CONTAINER}" nginx -t
    docker exec "${NGINX_CONTAINER}" nginx -s reload

    echo "SSL 证书已续期并重载 Nginx"
    exit 0
fi

# ---- 常规模式：由 cron 调用 ----
# certbot 安装时自带的 certbot.timer 也会触发续期，但它不会把证书复制到
# nginx/ssl、也不会重载容器里的 nginx，会导致 90 天后证书再次过期。
# 因此这里先停掉 certbot 自带的定时器，保证续期只走本脚本这一条链路。
if command -v systemctl >/dev/null 2>&1 && systemctl is-active --quiet certbot.timer 2>/dev/null; then
    systemctl disable --now certbot.timer
    echo "已停用 certbot 自带的 certbot.timer，续期统一由本脚本负责"
fi

# 未到续期窗口时 certbot 直接跳过，deploy-hook 不会执行
certbot renew --quiet --deploy-hook "${PROJECT_DIR}/ssl-renew.sh --deploy"

echo "本次续期检查完成"
