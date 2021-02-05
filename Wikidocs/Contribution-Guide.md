
# Master

_This guide was modified based on Dr.Fraser's slides._

**Bug-Fixes** and **Features** will be merged into this branch.

-Naming convention for branch(only one branch per issues)

* GA-issues#-name
(eg:GA-33-test)

OR

* task/GA-25 or bug/GA-24

(If branch name starts with ID, GitLab will pull
info from the issue)

-After finishing your issues and make sure it won't break the build you can then

* Pull master into your current branch
* Resolve any merge conflicts
* Push the merges into your branch

-Push a merge request to merge your branch into the master branch and assign **more than one** team member to review the merge request.

* check the CI/CD pipeline that was trigger by this merge request to verify that the changes pass the build and test stages(will be set up later).
* closes issues.

## **Steps**

1. Select an issue to work on from the issues board. Assign issue to yourself

2. Create a branch to address that issue. (only one branch per issues)

    ```$git checkout master
       $git pull #update local master branch
       $git checkout -b GA-issues#-name # (eg: GA-14-game-help-button)
    ```

3. Commit your changes to your branch

    ```$git add -A # stages all of your changes
       $git commit -m "your commit message"
       $git push origin <BRANCH_NAME>
    ```

4. Merge the master branch into **your** working branch to prevent merge conflicts

    ```$git checkout master
       $git pull # update local master branch
       $git checkout <BRANCH_NAME>
       $git merge master # merges master into your feature branch
    ```

    Or

    ```$git fetch # downloads new data from a remote repository
       $git merge origin/master # merges master into your feature branch
    ```

    (Regularly merge the master branch into **your** working branch to prevent future merge conflicts)

5. Put up a merge request to merge your feature branch into the master branch and assign it to **more than one** team member to review the merge request.
    * Check the CI/CD pipeline that was trigged by this merge request to verify that the changes pass the build and test stages(will be set up later)

    * Description: If `Closes #4, #6`, `Related to #5` is included in a Merge Request description, issues #4 and #6 are closed automatically when the MR is merged, but not #5. Using Related to flags #5 as a related issue, but is not closed automatically.

6. Assignee reviews the merge request and submits a feedback/change request to the author if required, else approve.

7. Once the merge request approved, the **repo manager** will be responsible for merging the feature branch into the master.
