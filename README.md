app.io-plugin
=============

Jenkins CI plugin for App.io: uploads iOS simulator builds to App.io (via AWS S3 service) so that a live simulator link is available on the main project page.

## Unit Testing
To run unit tests, you will need to set a number of test properties to define the App.io and Amazon S3 credentials to use and other test parameters.  Do this using the property file at: src/main/resources/org/jenkinsci/plugins/appio/test.properties: there is a blank template for this in the repo.

Warning: the unit tests will remove the App.io app deployments after running - please use a different app name (Appio.appName) from any existing App.io app deployments.

### App.io Test Properties
1. Appio.appName=name for app on App.io
2. Appio.apiKeyUnencoded=App.io API key from https://app.io/account/api
3. Appio.badKey=An unrecognized API key
4. Appio.badName=An unrecognized app name: make sure app does not exist

### Amazon S3 Test Properties 
1. S3.accessKey=AWS Access Key
2. S3.secretKey=AWS Secret Key
3. S3.bucketName=AWS S3 Bucket
4. S3.badBucket=An unrecognized S3 bucket: make sure bucket does not exist
5. S3.keyName=An unrecognized AWS Access Key
6. S3.uploadFile=Path to zipped .app package to use for testing: just zip up any successful simulator build
7. S3.badFile=An unrecognized filepath



