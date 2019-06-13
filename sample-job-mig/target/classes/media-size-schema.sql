DROP TABLE IF EXISTS media_size;

CREATE TABLE tb_media_size
  (
    region VARCHAR(16) NOT NULL,
    media_id VARCHAR(32) NOT NULL,
    cid VARCHAR(128),
    content_name VARCHAR(128),
    m4a_name VARCHAR(64),
    m4a_size LONG,
    m4v_name VARCHAR(64),
    m4v_size LONG,
    mp4_name VARCHAR(64),
    mp4_size LONG,
    ratio LONG,
    PRIMARY KEY (region, media_id)
);

CREATE TABLE tb_media_size
  (
    region VARCHAR(16) NOT NULL,
    media_id VARCHAR(32) NOT NULL,
    cid VARCHAR(128),
    content_name VARCHAR(128),
    m4a_name VARCHAR(64),
    m4a_size LONG,
    m4v_name VARCHAR(64),
    m4v_size LONG,
    mp4_name VARCHAR(64),
    mp4_size LONG,
    ratio LONG,
    PRIMARY KEY (region, media_id)
);

commit;