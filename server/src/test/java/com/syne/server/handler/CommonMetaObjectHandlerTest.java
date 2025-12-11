package com.syne.server.handler;

import com.syne.server.entity.Article;
import com.syne.server.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

/**
 * 公共字段自动填充测试类
 */
@SpringBootTest
public class CommonMetaObjectHandlerTest {

    @Test
    public void testUserEntityFieldFill() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedpassword");

        System.out.println("用户实体测试:");
        System.out.println("创建时间: " + user.getCreateTime());
        System.out.println("更新时间: " + user.getUpdateTime());
    }

    @Test
    public void testArticleEntityFieldFill() {
        Article article = new Article();
        article.setTitle("测试文章");
        article.setSummary("测试文章摘要");
        article.setContent("测试文章内容");
        article.setCategoryId(1L);

        System.out.println("文章实体测试:");
        System.out.println("创建者ID: " + article.getCreateBy());
        System.out.println("更新者ID: " + article.getUpdateBy());
        System.out.println("创建时间: " + article.getCreateTime());
        System.out.println("更新时间: " + article.getUpdateTime());
    }
}