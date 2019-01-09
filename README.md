# cordova-camscanner

##Summary
Send the URI of the image you want to scan, after scanning the plugin will respond with a base64 string representation of the image.

## Install

### Android
Run `cordova plugin add cordova-camscanner --variable ANDROID_APP_KEY=YOUR_APP_KEY`.

### IOS
Run `cordova plugin add cordova-camscanner --variable IOS_APP_KEY=YOUR_APP_KEY --variable BUNDLE_ID=YOUR_BUNDLE_ID`.

## Usage
```

navigator.camera.getPicture(
  function(srcUri){
    cordovaCamscanner.scan(
      srcUri,
      function(response){console.log("success: " + response);},
      function(response){console.log("error: " + response);}
    );
  },
  function(error){console.log(error);},
  {
    quality: 100,
    destinationType: navigator.camera.DestinationType.FILE_URI,
    sourceType: navigator.camera.PictureSourceType.PHOTOLIBRARY
  }
);

```