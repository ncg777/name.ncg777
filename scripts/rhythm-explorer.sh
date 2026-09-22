#!/usr/bin/env sh
exec java -cp "${RHYTHM_JAR:-name.ncg777.jar}" name.ncg777.maths.numbers.fixed.rhythm.apps.RhythmExplorerGUI "$@"
