app.io-plugin
=============

Jenkins CI plugin for App.io: uploads iOS simulator builds to App.io (via AWS S3 service) so that a live simulator link is available on the main project page.

To run unit tests, you will need to set a number of test properties using the property file at: src/main/resources/org/jenkinsci/plugins/appio/test.properties
Warning: the unit tests will remove the App.io app deployments after running - please use a different app name (Appio.appName) from any existing App.io app deployments.  The unit tests will remove the app
The following properties need to be set:

# AppioServiceTest properties
Appio.appName=<name for app on App.io>
Appio.apiKeyUnencoded=<App.io API key from https://app.io/account/api>
Appio.badKey=<An unrecognized API key>
Appio.badName=<An unrecognized app name: make sure app does not exist>

# S3ServiceTest properties
# Also used in AppioServiceTest.addVersionS3()
S3.accessKey=<AWS Access Key>
S3.secretKey=<AWS Secret Key>
S3.bucketName=<AWS S3 Bucket>
S3.badBucket=<An unrecognized S3 bucket: make sure bucket does not exist>
S3.keyName=<An unrecognized AWS Access Key>
S3.uploadFile=<Path to zipped .app package to use for testing: just zip up any successful simulator build>
S3.badFile=<An unrecognized filepath>



