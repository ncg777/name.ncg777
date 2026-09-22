@echo off
if not defined RHYTHM_JAR set "RHYTHM_JAR=name.ncg777.jar"
java -cp "%RHYTHM_JAR%" name.ncg777.maths.numbers.fixed.rhythm.apps.RhythmExplorerApp %*
