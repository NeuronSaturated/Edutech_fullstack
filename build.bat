@echo off
set MVN_CMD=mvn

if "%1"=="clean" goto do_clean
if "%1"=="report" goto do_report
if "%1"=="site"   goto do_site
if "%1"=="all"    goto do_all

echo Usage: build.bat [clean|report|site|all]
goto eof

:do_clean
  echo Limpieza + compilación + tests + JaCoCo
  %MVN_CMD% clean verify
  goto eof

:do_report
  echo Generando informe de tests (Surefire)
  %MVN_CMD% surefire-report:report
  goto eof

:do_site
  echo Generando sitio completo de Maven
  %MVN_CMD% site
  goto eof

:do_all
  call build.bat clean
  call build.bat report
  call build.bat site
  goto eof

:eof
