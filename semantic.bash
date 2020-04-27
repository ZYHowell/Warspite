set ff=UNIX
set -e
cd "$(dirname "$0")"
export CCHK="java -classpath ./lib/antlr-4.7.2-complete.jar:./bin: Main -test -semantic"
cat > test.mx   # save everything in stdin to test.mx
$CCHK
