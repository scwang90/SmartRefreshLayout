@ECHO OFF

@echo off
set aa=伟大的中国！我为你自豪！
echo 替换前：%aa%
echo 替换后：%aa:中国=中华人民共和国%
echo aa = %aa%
set "aa=%aa:中国=中华人民共和国%"
echo aa = %aa%
pause


SET sdk=null
FOR /F "eol=; tokens=2,2 delims==" %%i IN ('findstr /i "sdk.dir0" local.properties') DO SET sdk=%%i

IF %sdk% == null (
    ECHO 没找到SDK，脚本中止
    GOTO END
) else (
    ECHO SKD = %sdk%
)




echo skd=%sdk%
set sdk = sdk + "\build-tools\"
echo skd=%sdk%

rem ./gradlew assemble
rem dx --dex --verbose --no-strict --output=refresh-layout/build/intermediates/intermediate-jars/debug/classes.jar.dex refresh-layout/build/intermediates/intermediate-jars/debug/classes.jar
rem https://github.com/mihaip/dex-method-counts
rem java -jar ./art/dex-method-counts.jar --filter=all --output-style=tree --package-filter=com.scwang.smartrefresh refresh-layout/build/intermediates/intermediate-jars/debug/classes.jar.dex

:END