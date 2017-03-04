DROP TABLE IF EXISTS user_dimension;
CREATE TABLE user_dimension (
  user_id varchar(13) NOT NULL,
  user_nif varchar(9) NOT NULL,
  user_nome varchar(80) NOT NULL,
  user_telefone varchar(26) NOT NULL,
  PRIMARY KEY (user_id)
);

INSERT INTO user_dimension
SELECT 
  concat('user',nif) as user_id,
  nif as user_nif,
  nome as user_nome,
  telefone as user_telefone
FROM user;
