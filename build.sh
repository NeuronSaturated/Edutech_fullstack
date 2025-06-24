#!/usr/bin/env bash
#
# build.sh — ¡Tu navaja suiza de Maven!
# Colócalo en la raíz del proyecto y dale permisos:
#   chmod +x build.sh
#
# Uso:
#   ./build.sh [clean|test|report|site|all]
#
MVN_CMD="mvn"

function do_clean() {
  echo ">>> Limpieza + compilación + tests + JaCoCo"
  $MVN_CMD clean verify
}

function do_test_report() {
  echo ">>> Sólo el informe de tests (Surefire)"
  $MVN_CMD surefire-report:report
  echo "  -> target/surefire-reports-html/surefire-report.html"
}

function do_site() {
  echo ">>> Genera el sitio completo Maven (Project Info + Reports)"
  $MVN_CMD site
  echo "  -> target/site/index.html"
}

case "$1" in
  clean)           do_clean         ;;
  test)            do_clean         ;;
  report)          do_test_report   ;;
  site)            do_site          ;;
  all)             do_clean; do_test_report; do_site ;;
  *) 
    cat <<EOF
Usage: $0 [clean|report|site|all]

  clean   = mvn clean verify
  report  = mvn surefire-report:report
  site    = mvn site
  all     = clean + report + site

Ejemplo:
  ./build.sh all
EOF
    ;;
esac
