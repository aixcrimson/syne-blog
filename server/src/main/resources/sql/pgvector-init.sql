-- pgvector 初始化脚本
-- 用于支持 RAG 检索增强生成功能

-- 1. 启用 pgvector 扩展
CREATE EXTENSION IF NOT EXISTS vector;

-- 2. 创建文章向量嵌入表
CREATE TABLE IF NOT EXISTS article_embeddings (
    id BIGSERIAL PRIMARY KEY,
    article_id BIGINT NOT NULL,
    chunk_index INTEGER DEFAULT 0,
    chunk_content TEXT NOT NULL,
    embedding vector(1536) NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- 外键约束：关联文章表
    CONSTRAINT fk_article_embeddings_article
        FOREIGN KEY (article_id)
        REFERENCES articles(id)
        ON DELETE CASCADE,

    -- 唯一约束：同一篇文章的同一个分块
    CONSTRAINT uk_article_chunk
        UNIQUE (article_id, chunk_index)
);

-- 3. 创建 HNSW 索引（加速向量相似度搜索）
CREATE INDEX IF NOT EXISTS idx_article_embeddings_vector
    ON article_embeddings
    USING hnsw (embedding vector_cosine_ops)
    WITH (m = 16, ef_construction = 64);

-- 4. 创建文章ID索引
CREATE INDEX IF NOT EXISTS idx_article_embeddings_article_id
    ON article_embeddings(article_id);

-- 5. 创建更新时间触发器
CREATE OR REPLACE FUNCTION update_embedding_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

DROP TRIGGER IF EXISTS update_article_embeddings_modtime ON article_embeddings;
CREATE TRIGGER update_article_embeddings_modtime
    BEFORE UPDATE ON article_embeddings
    FOR EACH ROW
    EXECUTE FUNCTION update_embedding_modified_column();

-- 6. 添加注释
COMMENT ON TABLE article_embeddings IS '文章向量嵌入表，用于 RAG 检索';
COMMENT ON COLUMN article_embeddings.article_id IS '关联的文章ID';
COMMENT ON COLUMN article_embeddings.chunk_index IS '文章分块索引（一篇文章可能被分成多个块）';
COMMENT ON COLUMN article_embeddings.chunk_content IS '分块的文本内容';
COMMENT ON COLUMN article_embeddings.embedding IS '1536维向量嵌入（text-embedding-ada-002）';
COMMENT ON COLUMN article_embeddings.metadata IS '元数据JSON（标题、分类、标签等）';
