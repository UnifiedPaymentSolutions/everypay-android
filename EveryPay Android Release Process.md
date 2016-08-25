## A guide on how to release EveryPay Android SDK

## NB! All SDK releases should be done under dev branch in order to test them right away in master branch.

## CONTENTS:
- A. Prerequisites
- B. Getting SDK to Bintray
- C. Getting SDK from private package to Everypay organization package
- D. Testing Bintray dependency and updating git repo
- E. Troubleshooting standard questions


## A. Prerequisites

- 0) Set environment variables BINTRAY_USER and BINTRAY_API_KEY according to your Bintray credentials.
- 1) Update version under project root build.gradle file. Version number according to scheme:

    a)    MAJOR version when you make incompatible API changes.
    
    b)   MINOR version when you add functionality in a backwards-compatible manner.
    
    c)  PATCH version when you make backwards-compatible bug fixes.
    
Additional labels for pre-release and build metadata are available as extensions to the MAJOR.MINOR.PATCH format.


## B. Getting SDK to Bintray
- 1)  In command line/terminal : Do ./graldew clean build, to rule out any lint errors
- 2) In command line/terminal: Do ./graldew bintrayUpload
- 3) Verify that packages has new version with previously specified version number under your maven repo in Bintray

## C. Getting SDK from private package to Everypay organization package

- 0) WE HAVEN'T FOUND BETTER SOLUTION. THERE CLEARLY SHOULD BE AUTOMATIC SOLUTION, BUT WE WILL USE THIS APPROACH FOR NOW

- 1) Navigate to your package, where you pushed your latest SDK
- 2) Download latest version files
- 3) Navigate to everypay organization maven repo
- 4) Create new version
- 5) Name it same as your package's
- 6) Upload previously downloaded files
- 7) Let Bintray suggest you the path and accept it
- 8) Publish new veresion
- 9) Verifiy that newest version is up for grabs at: http://jcenter.bintray.com/com/everypay/sdk/android-sdk/


## D. Testing Bintray dependency and updating git repo

- 1) Change to everypay-android git repo master branch
- 2) Change dependency version under app module build.gradle to latest SDK version
- 3) Sync gradle files
- 4) Build application and test through all the flows
- 5) If everything is correct then push depencency change to master branch and make new release with tag with prefix v and corresponding SDK version.


## E. Troubleshooting standard questions

- 1) What version of SDK are you using ? 
- 2) Is dependency strict or not ? 
- 3) Are you receiving any callback from Every Pay server ?
- 4) Could you send us logs about the issue,if you have any.
