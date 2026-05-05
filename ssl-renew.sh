#!/bin/bash
# SSL 证书续期脚本

# 续期证书
certbot renew --quiet

# 复制新证书到 Nginx 目录
cp /etc/letsencrypt/live/cyneblog.top/fullchain.pem /opt/syne-blog/nginx/ssl/
cp /etc/letsencrypt/live/cyneblog.top/privkey.pem /opt/syne-blog/nginx/ssl/

# 重启 Nginx
docker restart syne-blog-nginx

echo "SSL证书已续期并重启Nginx"
