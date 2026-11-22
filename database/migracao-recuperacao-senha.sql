-- Recuperação de senha ("Esqueci minha senha")
-- Rode UMA vez no banco gpc que você já tem:  mysql -u root -p gpc < migracao-recuperacao-senha.sql
-- (se você importar o gpc.sql inteiro de novo, essa tabela já vem nele)

CREATE TABLE IF NOT EXISTS `recuperacao_senha` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint NOT NULL,
  `token_hash` char(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `expira_em` datetime NOT NULL,
  `usado_em` datetime DEFAULT NULL,
  `criado_em` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_recuperacao_token_hash` (`token_hash`),
  KEY `idx_recuperacao_usuario` (`usuario_id`),
  CONSTRAINT `recuperacao_senha_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
