@echo off
rem !/bin/sh
rem
rem content in local.properties
rem sdk.dir=/path/to/your/androidSdkDir
rem
set ANDROID_SDK=D:/Android/android-sdk
set NDK_ROOT=D:/Android/android-ndk-r9

If "%ANDROID_SDK%"=="" goto :error
If "%NDK_ROOT%"=="" goto :error

echo off
echo sdk.dir=%ANDROID_SDK%> ./Eatly/local.properties
echo ndk.dir=%NDK_ROOT%>> ./Eatly/local.properties
echo param settings for ant are done!
goto :end

:error
echo ANDROID_SDK or NDK_ROOT is not set
echo ANDROID_SDK is %ANDROID_SDK%
echo NDK_ROOT is %NDK_ROOT%

:end
pause
