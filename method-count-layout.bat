@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Method Count Starter
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal enabledelayedexpansion

set SDK=
set TOOL=

for /F "delims== tokens=2" %%i IN ('findstr /i "sdk.dir" local.properties') DO SET SDK=%%i
for /F "delims=' tokens=2" %%i IN ('findstr /i "buildToolsVersion" %~dp0app\build.gradle') DO SET TOOL=%%i

echo TOOL=%TOOL%
echo SDK=%SDK%

if "%SDK%" == "" (
    echo 没找到 Android Sdk 路径，脚本中止！
    goto end
)
if "%TOOL%" == "" (
    echo 没找到 Android Build Tool 版本，脚本中止！
    goto end
)

set SDK=%SDK:\\=\%
set SDK=%SDK:\:=:%

set MODULE=refresh-layout-kernel
set OUTPUT=methods-apk.txt

set PATH_TOOL=%SDK%\build-tools\%TOOL%\dx.bat
set PATH_JAR=%MODULE%/build/intermediates/intermediate-jars/debug/classes.jar
set PATH_DEX=%PATH_JAR%.dex

echo 正在构建。。。

call ./gradlew assemble
if "%ERRORLEVEL%" == "0" (
    echo.
    echo 构建成功！，开始生成DEX文件。。。
) else (
    echo.
    echo 构建失败，脚本中止！
    goto end
)

:dex

echo 正在生成DEX。。。

call %PATH_TOOL% --dex --verbose --no-strict --output=%PATH_DEX% %PATH_JAR% >NUL
if "%ERRORLEVEL%" == "0" (
    echo.
    echo 生成DEX文件成功！，开始统计。。。
) else (
    echo.
    echo 生成DEX失败，脚本中止！
    goto end
)

call java -jar ./art/dex-method-counts.jar --filter=all --include-classes --include-detail --output-style=tree   %PATH_DEX% > %OUTPUT%

call ./method-count-words.bat %OUTPUT% %OUTPUT:.txt=-count.txt%

:end

if "%OS%"=="Windows_NT" endlocal