-- 创建space
CREATE SPACE IF NOT EXISTS java_maven (partition_num=15, replica_factor=1, vid_type=INT64);

-- Maven plugins

-- 创建schema信息
-- 创建TAG的schema信息
USE java_maven;
CREATE TAG IF NOT EXISTS git(remote_url string);
CREATE TAG IF NOT EXISTS model(group_id string, artifact_id string, type string, version string);
-- 创建EDGE的schema信息
USE java_maven;
CREATE EDGE IF NOT EXISTS dependency_management(branch string, commit_id string);
CREATE EDGE IF NOT EXISTS dependencies(commit_id string);
CREATE EDGE IF NOT EXISTS dependency(scope string);

-- 创建TAG git的索引（创建git点本身索引）
USE java_maven;
CREATE TAG INDEX index_git ON git();
REBUILD TAG INDEX index_git;

-- 创建TAG model的索引
USE java_maven;
CREATE TAG INDEX index_model ON model();
REBUILD TAG INDEX index_model;

-- Maven jar
-- 创建TAG的schema信息
USE java_maven;
CREATE TAG IF NOT EXISTS jar(name string);
CREATE TAG IF NOT EXISTS parent_model(group_id string, artifact_id string, version string);

-- 创建TAG git的索引（创建git点本身索引）
USE java_maven;
CREATE TAG INDEX index_jar ON jar();
REBUILD TAG INDEX index_jar;

CREATE TAG INDEX index_parent_model ON parent_model();
REBUILD TAG INDEX index_parent_model;

-- 创建EDGE的schema信息
USE java_maven;
CREATE EDGE IF NOT EXISTS parent();
