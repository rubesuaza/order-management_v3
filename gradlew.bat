@rem Gradle wrapper script for Windows.
@rem Run "gradle wrapper" to generate gradle-wrapper.jar for offline use.
@echo off
set DIRNAME=%~dp0
cd /d "%DIRNAME%"
if exist "gradle\wrapper\gradle-wrapper.jar" (
    java -jar gradle\wrapper\gradle-wrapper.jar %*
) else (
    call gradle %*
)
