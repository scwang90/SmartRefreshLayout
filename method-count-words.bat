@echo off &
setlocal enabledelayedexpansion

set FILE=%1
set OUTPUT=%2

if "%FILE%" == "" (
    echo "请输入参数！"
    goto :eof
)
REM if "%OUTPUT%" == "" (
REM     echo "使用默认参数OUTPUT"
REM     set OUTPUT=methods-words.txt
REM )

echo 正在统计单词数量。。。

call :test

echo.
echo 输出统计结果

set number=1

for /f "skip=1 tokens=1,* delims=_=" %%i in ('set _') do set /a number+=1

if "%OUTPUT%" == "" (
    echo %number%	: Total Words
) else (
    echo %number%	: Total Words > "%OUTPUT%"
)

for /f "skip=1 tokens=1,* delims=_=" %%i in ('set _') do if "%OUTPUT%" == "" (

    set "var=%%i"
    set "var=!var:$$$=<!"
    set "var=!var:###=>!"

    echo %%j	: !var!
) else (
    set "var=%%i"
    set "var=!var:$$$=<!"
    set "var=!var:###=>!"

    echo %%j	: !var!>>"%OUTPUT%"
)

if not "%OUTPUT%" == "" (
    echo 开始排序结果
    sort -rg "%OUTPUT%">"%OUTPUT:.txt=-sort.txt%"
    echo 结果排序结束
)

echo 输出统计结果完成

goto :eof

:test

for /f "delims=" %%l in (%FILE%) do (
set str=%%l
call :calc

)

goto :eof

:calc

for /f "tokens=1,* delims=':.-=%% " %%i in ("%str%") do (
    REM echo.
    REM echo i=%%i
    set /p="." <nul

    set "var=%%i"
    set "var=!var:<=$$$!"
    set "var=!var:>=###!"
    REM echo "var=%var%"
    REM echo "var=!var!"

    REM set /a "_!var!+=1"

    REM echo "!var!"|findstr /be "\"[0-9]*\"">nul && ECHO "!var! 是纯数字" || ECHO "!var! 不是数字"
    echo "!var!"|findstr /be "\"[0-9]*\"">nul && echo "find">nul || set /a "_!var!+=1"

    set "str=%%j" && echo "find">nul || pause
    REM echo str=%str%
    REM echo str=!str!

    if not "%str%"=="" call :calc
)

goto :eof
