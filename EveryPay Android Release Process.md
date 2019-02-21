## A guide on how to release EveryPay Android SDK

## NB! All SDK releases should be done under dev branch in order to test them right away in master branch.

## CONTENTS:
- A. Prerequisites
- B. Getting SDK to Bintray
- C. Getting SDK from private package to Everypay organization package
- D. Testing Bintray dependency and updating git repo
- E. Standard  troubleshooting questions


## A. Prerequisites

- 0) Set BINTRAY_USER and BINTRAY_API_KEY environment variables according to your Bintray credentials.
- 1) Update version under project root build.gradle file. Version number according to scheme:

    a)    MAJOR version when you make incompatible API changes.
    
    b)   MINOR version when you add functionality in a backwards-compatible manner.
    
    c)  PATCH version when you make backwards-compatible bug fixes.
    
Additional labels for pre-release and build metadata are available as extensions to the MAJOR.MINOR.PATCH format.


## B. Getting SDK to Bintray
- 0) Every development of SDK should be done in dev branch. There is another package named android-sdk-beta in Bintray for pre-releases.
- 1)  In command line/terminal : Do ./graldew clean build, to rule out any lint errors
- 2) In command line/terminal: Do ./graldew bintrayUpload
- 3) Verify that packages has new version with previously specified version number under your maven repo in Bintray at: http://jcenter.bintray.com/com/everypay/sdk/android-sdk-beta/ NOTE: This is beta pacakge used to test out the published binaries from jcenter before releasing acutal new binary to main maven package.


## D. Testing Bintray dependency and updating git repo

- 1) Change to everypay-android git repo master branch
- 2) Change dependency version under app module build.gradle to latest SDK version
- 3) Sync gradle files
- 4) Build application and test through all the flows
- 5) If everything is correct then in command line/terminal call ./graldew bintrayUpload in master branch to publish the new release
- 6) Make new release with tag with prefix v and corresponding SDK version.


## E. Standard troubleshooting questions

- 1) What version of SDK are you using ? 
- 2) Is dependency strict or not ? 
- 3) Are you receiving any callback from Every Pay server ?
- 4) Could you send us logs about the issue,if you have any.
