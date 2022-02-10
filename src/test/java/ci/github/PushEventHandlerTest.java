package ci.github;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static ci.JobQueueConsumer.WorkItem;

public class PushEventHandlerTest {
    // Make sure that we can properly parse a webhook request made by GitHub.
    @Test
    void parseEventBody() {
        // Extracted from a webhook request on the 7th February 2022
        String body = """
        {
            "ref": "refs/heads/main",
            "before": "0000000000000000000000000000000000000000",
            "after": "fdb6f8d7d29dc36bb36ec3783680d7ccb44cea10",
            "repository": {
              "id": 456520470,
              "node_id": "R_kgDOGzXzFg",
              "name": "ci-test-repo",
              "full_name": "nolanderc/ci-test-repo",
              "private": true,
              "owner": {
                "name": "nolanderc",
                "email": "christofer.nolander@gmail.com",
                "login": "nolanderc",
                "id": 16593746,
                "node_id": "MDQ6VXNlcjE2NTkzNzQ2",
                "avatar_url": "https://avatars.githubusercontent.com/u/16593746?v=4",
                "gravatar_id": "",
                "url": "https://api.github.com/users/nolanderc",
                "html_url": "https://github.com/nolanderc",
                "followers_url": "https://api.github.com/users/nolanderc/followers",
                "following_url": "https://api.github.com/users/nolanderc/following{/other_user}",
                "gists_url": "https://api.github.com/users/nolanderc/gists{/gist_id}",
                "starred_url": "https://api.github.com/users/nolanderc/starred{/owner}{/repo}",
                "subscriptions_url": "https://api.github.com/users/nolanderc/subscriptions",
                "organizations_url": "https://api.github.com/users/nolanderc/orgs",
                "repos_url": "https://api.github.com/users/nolanderc/repos",
                "events_url": "https://api.github.com/users/nolanderc/events{/privacy}",
                "received_events_url": "https://api.github.com/users/nolanderc/received_events",
                "type": "User",
                "site_admin": false
              },
              "html_url": "https://github.com/nolanderc/ci-test-repo",
              "description": null,
              "fork": false,
              "url": "https://github.com/nolanderc/ci-test-repo",
              "forks_url": "https://api.github.com/repos/nolanderc/ci-test-repo/forks",
              "keys_url": "https://api.github.com/repos/nolanderc/ci-test-repo/keys{/key_id}",
              "collaborators_url": "https://api.github.com/repos/nolanderc/ci-test-repo/collaborators{/collaborator}",
              "teams_url": "https://api.github.com/repos/nolanderc/ci-test-repo/teams",
              "hooks_url": "https://api.github.com/repos/nolanderc/ci-test-repo/hooks",
              "issue_events_url": "https://api.github.com/repos/nolanderc/ci-test-repo/issues/events{/number}",
              "events_url": "https://api.github.com/repos/nolanderc/ci-test-repo/events",
              "assignees_url": "https://api.github.com/repos/nolanderc/ci-test-repo/assignees{/user}",
              "branches_url": "https://api.github.com/repos/nolanderc/ci-test-repo/branches{/branch}",
              "tags_url": "https://api.github.com/repos/nolanderc/ci-test-repo/tags",
              "blobs_url": "https://api.github.com/repos/nolanderc/ci-test-repo/git/blobs{/sha}",
              "git_tags_url": "https://api.github.com/repos/nolanderc/ci-test-repo/git/tags{/sha}",
              "git_refs_url": "https://api.github.com/repos/nolanderc/ci-test-repo/git/refs{/sha}",
              "trees_url": "https://api.github.com/repos/nolanderc/ci-test-repo/git/trees{/sha}",
              "statuses_url": "https://api.github.com/repos/nolanderc/ci-test-repo/statuses/{sha}",
              "languages_url": "https://api.github.com/repos/nolanderc/ci-test-repo/languages",
              "stargazers_url": "https://api.github.com/repos/nolanderc/ci-test-repo/stargazers",
              "contributors_url": "https://api.github.com/repos/nolanderc/ci-test-repo/contributors",
              "subscribers_url": "https://api.github.com/repos/nolanderc/ci-test-repo/subscribers",
              "subscription_url": "https://api.github.com/repos/nolanderc/ci-test-repo/subscription",
              "commits_url": "https://api.github.com/repos/nolanderc/ci-test-repo/commits{/sha}",
              "git_commits_url": "https://api.github.com/repos/nolanderc/ci-test-repo/git/commits{/sha}",
              "comments_url": "https://api.github.com/repos/nolanderc/ci-test-repo/comments{/number}",
              "issue_comment_url": "https://api.github.com/repos/nolanderc/ci-test-repo/issues/comments{/number}",
              "contents_url": "https://api.github.com/repos/nolanderc/ci-test-repo/contents/{+path}",
              "compare_url": "https://api.github.com/repos/nolanderc/ci-test-repo/compare/{base}...{head}",
              "merges_url": "https://api.github.com/repos/nolanderc/ci-test-repo/merges",
              "archive_url": "https://api.github.com/repos/nolanderc/ci-test-repo/{archive_format}{/ref}",
              "downloads_url": "https://api.github.com/repos/nolanderc/ci-test-repo/downloads",
              "issues_url": "https://api.github.com/repos/nolanderc/ci-test-repo/issues{/number}",
              "pulls_url": "https://api.github.com/repos/nolanderc/ci-test-repo/pulls{/number}",
              "milestones_url": "https://api.github.com/repos/nolanderc/ci-test-repo/milestones{/number}",
              "notifications_url": "https://api.github.com/repos/nolanderc/ci-test-repo/notifications{?since,all,participating}",
              "labels_url": "https://api.github.com/repos/nolanderc/ci-test-repo/labels{/name}",
              "releases_url": "https://api.github.com/repos/nolanderc/ci-test-repo/releases{/id}",
              "deployments_url": "https://api.github.com/repos/nolanderc/ci-test-repo/deployments",
              "created_at": 1644241059,
              "updated_at": "2022-02-07T13:37:39Z",
              "pushed_at": 1644241586,
              "git_url": "git://github.com/nolanderc/ci-test-repo.git",
              "ssh_url": "git@github.com:nolanderc/ci-test-repo.git",
              "clone_url": "https://github.com/nolanderc/ci-test-repo.git",
              "svn_url": "https://github.com/nolanderc/ci-test-repo",
              "homepage": null,
              "size": 0,
              "stargazers_count": 0,
              "watchers_count": 0,
              "language": null,
              "has_issues": true,
              "has_projects": true,
              "has_downloads": true,
              "has_wiki": true,
              "has_pages": false,
              "forks_count": 0,
              "mirror_url": null,
              "archived": false,
              "disabled": false,
              "open_issues_count": 0,
              "license": null,
              "allow_forking": true,
              "is_template": false,
              "topics": [
          
              ],
              "visibility": "private",
              "forks": 0,
              "open_issues": 0,
              "watchers": 0,
              "default_branch": "main",
              "stargazers": 0,
              "master_branch": "main"
            },
            "pusher": {
              "name": "nolanderc",
              "email": "christofer.nolander@gmail.com"
            },
            "sender": {
              "login": "nolanderc",
              "id": 16593746,
              "node_id": "MDQ6VXNlcjE2NTkzNzQ2",
              "avatar_url": "https://avatars.githubusercontent.com/u/16593746?v=4",
              "gravatar_id": "",
              "url": "https://api.github.com/users/nolanderc",
              "html_url": "https://github.com/nolanderc",
              "followers_url": "https://api.github.com/users/nolanderc/followers",
              "following_url": "https://api.github.com/users/nolanderc/following{/other_user}",
              "gists_url": "https://api.github.com/users/nolanderc/gists{/gist_id}",
              "starred_url": "https://api.github.com/users/nolanderc/starred{/owner}{/repo}",
              "subscriptions_url": "https://api.github.com/users/nolanderc/subscriptions",
              "organizations_url": "https://api.github.com/users/nolanderc/orgs",
              "repos_url": "https://api.github.com/users/nolanderc/repos",
              "events_url": "https://api.github.com/users/nolanderc/events{/privacy}",
              "received_events_url": "https://api.github.com/users/nolanderc/received_events",
              "type": "User",
              "site_admin": false
            },
            "created": true,
            "deleted": false,
            "forced": false,
            "base_ref": null,
            "compare": "https://github.com/nolanderc/ci-test-repo/commit/fdb6f8d7d29d",
            "commits": [
              {
                "id": "fdb6f8d7d29dc36bb36ec3783680d7ccb44cea10",
                "tree_id": "abe369fbe0bdaf26bcca5eabf25ad58ea0ae7e68",
                "distinct": true,
                "message": "blah",
                "timestamp": "2022-02-07T14:46:15+01:00",
                "url": "https://github.com/nolanderc/ci-test-repo/commit/fdb6f8d7d29dc36bb36ec3783680d7ccb44cea10",
                "author": {
                  "name": "Christofer Nolander",
                  "email": "christofer.nolander@gmail.com",
                  "username": "nolanderc"
                },
                "committer": {
                  "name": "Christofer Nolander",
                  "email": "christofer.nolander@gmail.com",
                  "username": "nolanderc"
                },
                "added": [
                  "blah.txt"
                ],
                "removed": [
          
                ],
                "modified": [
          
                ]
              }
            ],
            "head_commit": {
              "id": "fdb6f8d7d29dc36bb36ec3783680d7ccb44cea10",
              "tree_id": "abe369fbe0bdaf26bcca5eabf25ad58ea0ae7e68",
              "distinct": true,
              "message": "blah",
              "timestamp": "2022-02-07T14:46:15+01:00",
              "url": "https://github.com/nolanderc/ci-test-repo/commit/fdb6f8d7d29dc36bb36ec3783680d7ccb44cea10",
              "author": {
                "name": "Christofer Nolander",
                "email": "christofer.nolander@gmail.com",
                "username": "nolanderc"
              },
              "committer": {
                "name": "Christofer Nolander",
                "email": "christofer.nolander@gmail.com",
                "username": "nolanderc"
              },
              "added": [
                "blah.txt"
              ],
              "removed": [
          
              ],
              "modified": [
          
              ]
            }
          }
        """;

        JSONObject object = new JSONObject(body);
        WorkItem item = PushEventHandler.extractWorkItem(object);

        assertEquals("https://github.com/nolanderc/ci-test-repo", item.repoUrl);
        assertEquals("https://api.github.com/repos/nolanderc/ci-test-repo/statuses/fdb6f8d7d29dc36bb36ec3783680d7ccb44cea10", item.statusUrl);
        assertEquals("refs/heads/main", item.ref);
        assertEquals("fdb6f8d7d29dc36bb36ec3783680d7ccb44cea10", item.head);
        assertEquals("blah", item.message);
    }
}
