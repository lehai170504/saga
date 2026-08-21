ALTER TABLE jira_boards ADD COLUMN access_token VARCHAR(2048);
ALTER TABLE jira_boards ADD COLUMN refresh_token VARCHAR(2048);

ALTER TABLE git_repos ADD COLUMN access_token VARCHAR(2048);
ALTER TABLE git_repos ADD COLUMN refresh_token VARCHAR(2048);
