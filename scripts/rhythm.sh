#!/usr/bin/env sh
# RHYTHM_JAR may point to a packaged jar with dependencies.
exec java -cp "${RHYTHM_JAR:-name.ncg777.jar}" name.ncg777.maths.numbers.fixed.rhythm.apps.RhythmExplorerApp "$@"
