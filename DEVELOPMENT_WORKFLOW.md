# Development Workflow

This project uses `master` as the production branch.

## Branching model
- `master`: production code only.
- `feature/<short-name>`: new features.
- `fix/<short-name>`: bug fixes.

Never commit directly to `master`.

## Daily flow
1. Update local master:
   - `git checkout master`
   - `git pull origin master`
2. Create branch:
   - `git checkout -b feature/<your-task>`
3. Develop and test locally:
   - `./gradlew bootRun` (Windows: `gradlew.bat bootRun`)
   - `./gradlew test` (Windows: `gradlew.bat test`)
4. Push branch:
   - `git push -u origin feature/<your-task>`
5. Open Pull Request to `master`.
6. Wait for CI to pass (build + tests).
7. Merge PR.
8. Merge to `master` triggers production deployment automatically.

## CI/CD behavior
- Pull requests to `master`: build and tests only.
- Push to `master`: build + tests + deploy to Azure Web App `FamilyHub`.

## Hotfix flow
1. Create `fix/<issue>` from latest `master`.
2. Apply minimal fix.
3. Open PR to `master`.
4. Merge after CI is green.

## Safety rules
- Keep secrets only in Azure App Settings / GitHub Secrets.
- Do not store passwords in repository files.
- If production fails, revert with a new PR to `master` quickly.

## Useful commands (Windows)
- Run app: `gradlew.bat bootRun`
- Run tests: `gradlew.bat test`
- Build jar: `gradlew.bat clean bootJar`
