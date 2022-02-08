This folder holds all the log files for previous builds. Each file is named:
"`build_number`_`type`.log".
* `build_number` tells in what order the commits were built by the ci server.
* `type` tell what type of commit was made (currently only `Log_type.PUSH` and `Log_type.TEST`)

The log files contain
* type: the type of the commit
* refspec: the reference specifier (what branch the commit was made to (eg. refs/heads/master))
* commit_SHA: the SHA hash of the latest commit (in the push)
* date_time: the time in miliseconds since 00:00 01/01/1970 UTC (aka unix epoch)
* status: the success or failure of building (and testing) the commit.