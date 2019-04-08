@echo off
setlocal ENABLEDELAYEDEXPANSION
rem Windows batch file that helps to boot the engine.
rem Provided without warranty.

rem Include the path of the JDK installed.
rem set JAVA_HOME=D:\java\64bit\jdk1.8.0_05

if not defined JAVA_HOME  (
   echo Edit this batch file and set the appropriate JAVA_HOME to JDK 1.8 installed on this machine
   goto :EXIT
)

set classpath=.\libs\*;.\*;%CLASSPATH%
echo CLASSPATH=%classpath%

rem Command line argument 1 : Relative path of Config.xls/xlsm
rem Command line argument 2 : Application Name (Billing/Claims/PAS)

java -classpath %CLASSPATH% com.majesco.itaf.main.ITAFWebDriver Claim\CommonResources\Config.xls Claims
rem -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000 

:EXIT
