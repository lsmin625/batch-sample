DROP TABLE IF EXISTS media_size;

CREATE TABLE media_size
  (
    region VARCHAR(16) NOT NULL,
    media_id VARCHAR(32) NOT NULL,
    m4a_name VARCHAR(64),
    m4a_size LONG,
    m4v_name VARCHAR(64),
    m4v_size LONG,
    mp4_name VARCHAR(64),
    mp4_size LONG,
    PRIMARY KEY (region, media_id)
);
commit;