<!-- omit in toc -->
# CI/CD Mobile — Student Guide
<!-- omit in toc -->
### GitHub Actions • Fastlane • Firebase App Distribution

This guide will walk you through the three steps required to complete the CI/CD pipeline for the Android demo project.

By the end, you will:
*   Create a Firebase Project
*   Configure GitHub Actions environment secrets
*   Complete the missing parts of the workflow file: `CICD_Mobile/CurrencyConverterI18N/.github/workflows/android-ci.yml`

---
- [✅ 1. Create a Firebase Project](#-1-create-a-firebase-project)
- [✅ 2. Create GitHub Environment \& Add Secrets](#-2-create-github-environment--add-secrets)
- [✅ 3. Complete the GitHub Workflow File](#-3-complete-the-github-workflow-file)
- [🎉 You’re Done!](#-youre-done)
---

### ✅ 1. Create a Firebase Project

Follow the steps below:

**1.1 Create the Firebase project**
1.  Visit: [https://console.firebase.google.com](https://console.firebase.google.com)
2.  Click **“Add project”**
3.  Enter any name (e.g., `CurrencyConverterI18N-CI`)
4.  Disable Google Analytics unless required
5.  Click **Create Project**

**1.2 Register the Android app**
Inside your Firebase project:
1.  Go to **Build > App Distribution** (left menu)
2.  Go to **Project Overview → Add App → Android**
3.  Enter the Android package name used in the demo project: `com.example.deviseapp`
4.  Click **Register App**

**1.3 Download `google-services.json`**
After adding the Android app, Firebase will generate the `google-services.json` file.
*   Download it.
*   **Do NOT commit it to GitHub.** You will encode it to Base64 in a later step.

**1.4 Install Firebase CLI and log in**
You need a token for GitHub Actions to authenticate.
Run these commands in your terminal:
```sh
npm install -g firebase-tools
firebase login
firebase login:ci
```
Copy the generated token — you will use it in your GitHub secrets.

---

### ✅ 2. Create GitHub Environment & Add Secrets

To keep deployment secure, GitHub Actions requires secrets stored in an environment.

**2.1 Create a GitHub Environment**
1.  Go to your GitHub repository.
2.  Navigate to **Settings → Secrets and variables → Actions → Manage environment secrets → New environment **.
3.  Name it: `test`
4.  Click **Save environment**.

**2.2 Create GitHub Secrets**
Below is the full list of secrets that must be added inside the `test` environment:

| Secret Name                   | Secret Value Description                                    |
| ----------------------------- |-------------------------------------------------------------|
| `FIREBASE_TOKEN`              | The Firebase CI token generated using `firebase login:ci`.  |
| `FIREBASE_APP_ID_ANDROID`     | Found in Firebase → Project Settings → Your Apps → App ID.  |
| `GOOGLE_SERVICES_JSON_BASE64` | Base64-encoded version of your `google-services.json` file. |
| `FIREBASE_TESTERS`            | A comma-separated list of your testers' email addresses.    |

**2.3 Encode `google-services.json`**
Run the appropriate command for your OS to convert the file to Base64:

*   **macOS / Linux**
    ```sh
    base64 -i google-services.json | pbcopy
    ```
*   **Windows (PowerShell)**
    ```powershell
    certutil -encode google-services.json temp.txt
    # The output is saved to temp.txt. Open it and copy the content.
    ```
Copy the Base64 output and paste it into the `GOOGLE_SERVICES_JSON_BASE64` secret in GitHub.

---

### ✅ 3. Complete the GitHub Workflow File

Your final task is to fill in the missing sections of `CICD_Mobile/CurrencyConverterI18N/.github/workflows/android-ci.yml`. This file is a template with `TODO` comments guiding you through the process.

**Your Goal:** Complete the workflow so it can automatically build, test, and distribute the app.

Below is the structure you will be completing in the `.yml` file:

```yaml
# ... (file header)

jobs:
  build-test-distribute:
    runs-on: ubuntu-latest

    # TODO: Step 1 - Specify the Build Environment

    # TODO: Step 2 - Create Environment Variables from Secrets

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      # TODO: Step 3 - Set up JDK 17

      # TODO: Step 4 - Make the Gradle wrapper executable

      # TODO: Step 5 - Decode and create google-services.json

      # TODO: Step 6 - Build the App and Run Tests

      # TODO: Step 7 - Install the Firebase CLI

      # TODO: Step 8 - Distribute to Firebase App Distribution
```

**Hints for completing the `TODO` steps:**

*   **Environment & Secrets (TODO 1 & 2):** Your `.yml` file needs to know the name of your environment and which secrets to use. Refer to section 2.1 and 2.2 of this guide.

*   **Decode `google-services.json` (TODO 5):**
    ```sh
    echo "${{ env.GOOGLE_SERVICES_JSON_BASE64 }}" | base64 -d > app/google-services.json
    ```

*   **Setup Java (TODO 3):** Use the official action `actions/setup-java@v4`.

*   **Build & Test (TODO 6):** Run the standard Gradle tasks for a debug build.
    ```sh
    ./gradlew clean lint test assembleDebug
    ```

*   **Distribute (TODO 8):** Use the `firebase appdistribution:distribute` command, making sure to use the environment variables you defined (e.g., `${{ env.FIREBASE_APP_ID_ANDROID }}`).

---

### 🎉 You’re Done!

Once you have filled in all the `TODO` sections in the `.yml` file, commit your changes and push to the `exercise` branch. This will trigger your first CI/CD pipeline run!

If you need help, ask your CI/CD group or check the official documentation.
