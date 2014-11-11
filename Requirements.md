MaidSafe-CI
===========

Requirements for MaidSafe Continuous Integration and Deployment

General Outline
---------------

MaidSafe-CI is a Jenkins Plugin for cross-platform building, testing, and deploying of the MaidSafe Core project and MaidSafe Examples.  

MaidSafe-CI minimally has to meet following requirements:
### multi-submodule project:
compatibility for code base spread over several GitHub submodules:
- source code management (git): for a given MAID task, pull the task code from forks of developer, use MaidSafe-Off-Of-Next target branch when and where necessary, use latest MaidSafe next for all remaining repositories.
-
