#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# rhythmnetwork-explore.sh
#
# Full pipeline:
#   1. Enumerate all CNF clauses for the given meter
#   2. Score each clause (build network + analyse), saving .rnet files along the way
#   3. Rank by composite score: strongly-connected × (H_stat + H_avg + spectral_gap)
#   4. For each top-N winner: load the saved .rnet, then walk (no rebuild needed)
#
# Usage:
#   ./rhythmnetwork-explore.sh [options]
#
# Options (all have defaults):
#   -L LENGTH         cipher symbols per rhythm     (default: 4)
#   -c CIPHER         Binary|Octal|Hexadecimal       (default: Hexadecimal)
#   -w MAX_WIDTH      max clause literal count       (default: 2)
#   -g MAX_GAP        max gap param for MINIMUM/MAXIMUM_GAP (default: auto)
#   -T TEMP           construction temperature       (default: 1.0)
#   -t NAV_TEMP       navigation temperature         (default: 2.0)
#   -d DISTANCE       HAMMING|JACCARD|INTERVAL_VECTOR (default: HAMMING)
#   -n TOP_N          how many top clauses to walk   (default: 5)
#   -s STEPS          walk length                   (default: 64)
#   -j JUXT           juxtaposition clause string   (default: "")
#   -o OUTPUT_DIR     directory for all output      (default: ./rnet_out)
#   -p PARALLEL       parallel scoring jobs         (default: 4)
#   --positive-only   pass --positive-only to enumerator (default: off)
#
# Environment:
#   JAR   path to the jar (default: name.ncg777.jar)
#   JAVA  java executable (default: java)
# ─────────────────────────────────────────────────────────────────────────────

set -euo pipefail

JAR="${JAR:-name.ncg777.jar}"
JAVA="${JAVA:-java}"
CMD="$JAVA -cp $JAR"

# ── Defaults ──────────────────────────────────────────────────────────────────
L=4
CIPHER=Hexadecimal
MAX_WIDTH=2
MAX_GAP=-1
TEMP=1.0
NAV_TEMP=2.0
DISTANCE=HAMMING
TOP_N=5
STEPS=64
JUXT=""
OUTPUT_DIR="./rnet_out"
PARALLEL=4
POSITIVE_ONLY=""

# ── Argument parsing ──────────────────────────────────────────────────────────
while [[ $# -gt 0 ]]; do
  case "$1" in
    -L) L="$2";              shift 2 ;;
    -c) CIPHER="$2";         shift 2 ;;
    -w) MAX_WIDTH="$2";      shift 2 ;;
    -g) MAX_GAP="$2";        shift 2 ;;
    -T) TEMP="$2";           shift 2 ;;
    -t) NAV_TEMP="$2";       shift 2 ;;
    -d) DISTANCE="$2";       shift 2 ;;
    -n) TOP_N="$2";          shift 2 ;;
    -s) STEPS="$2";          shift 2 ;;
    -j) JUXT="$2";           shift 2 ;;
    -o) OUTPUT_DIR="$2";     shift 2 ;;
    -p) PARALLEL="$2";       shift 2 ;;
    --positive-only) POSITIVE_ONLY="--positive-only"; shift ;;
    *) echo "Unknown option: $1" >&2; exit 1 ;;
  esac
done

mkdir -p "$OUTPUT_DIR"
NETS_DIR="$OUTPUT_DIR/nets"
mkdir -p "$NETS_DIR"
CLAUSES_FILE="$OUTPUT_DIR/clauses.txt"
SCORES_FILE="$OUTPUT_DIR/scores.tsv"
TOP_FILE="$OUTPUT_DIR/top.tsv"

PIPELINE_START=$(date +%s)

# ── Utility: format seconds as Xm Ys ─────────────────────────────────────────
fmt_elapsed() {
  local secs="$1"
  if (( secs >= 60 )); then
    printf '%dm %ds' $((secs / 60)) $((secs % 60))
  else
    printf '%ds' "$secs"
  fi
}

# ── Utility: safe filename from clause string ────────────────────────────────
safe_name() {
  printf '%s' "$1" \
    | tr -cs 'A-Za-z0-9' '_' \
    | sed 's/__*/_/g; s/^_//; s/_$//'
}

# ─────────────────────────────────────────────────── Step 1: enumerate ────────
STEP_START=$(date +%s)
echo "═══ Step 1/4: Enumerating clauses ═══"
echo "  L=$L  cipher=$CIPHER  max-width=$MAX_WIDTH  max-gap=$MAX_GAP"
$CMD name.ncg777.maths.numbers.fixed.rhythm.RhythmClauseEnumeratorApp \
  -L "$L" -c "$CIPHER" \
  --max-width "$MAX_WIDTH" \
  --max-gap "$MAX_GAP" \
  $POSITIVE_ONLY \
  > "$CLAUSES_FILE"

TOTAL=$(wc -l < "$CLAUSES_FILE")
STEP_ELAPSED=$(( $(date +%s) - STEP_START ))
echo "  ✓ $TOTAL clauses enumerated in $(fmt_elapsed $STEP_ELAPSED)"
echo ""

# ─────────────────────────────────────────────────── Step 2: score ────────────
STEP_START=$(date +%s)
echo "═══ Step 2/4: Scoring $TOTAL clauses (parallel=$PARALLEL) ═══"
echo "  distance=$DISTANCE  T=$TEMP  τ=$NAV_TEMP"
echo "  Each clause: build network → analyse → save .rnet"
echo "  Networks are cached in $NETS_DIR/"

# Export variables needed by the scoring sub-shell
export JAVA JAR L CIPHER JUXT DISTANCE TEMP NAV_TEMP NETS_DIR

# Counter file for progress tracking
PROGRESS_FILE=$(mktemp)
echo "0" > "$PROGRESS_FILE"
export PROGRESS_FILE TOTAL

_score_one() {
  local clause="$1"
  # Compute safe filename for .rnet cache
  local safe
  safe=$(printf '%s' "$clause" \
    | tr -cs 'A-Za-z0-9' '_' \
    | sed 's/__*/_/g; s/^_//; s/_$//')
  local net_file="$NETS_DIR/${safe}.rnet"

  "$JAVA" -cp "$JAR" \
    name.ncg777.maths.numbers.fixed.rhythm.apps.RhythmNetworkScoreApp \
    -L "$L" -c "$CIPHER" \
    --vertex "$clause" \
    --juxt "$JUXT" \
    -d "$DISTANCE" \
    -T "$TEMP" \
    -t "$NAV_TEMP" \
    --save "$net_file" \
    2>/dev/null || true

  # Atomic progress increment + display
  local done
  if command -v flock &>/dev/null; then
    done=$(flock "$PROGRESS_FILE" bash -c '
      n=$(cat "'"$PROGRESS_FILE"'"); n=$((n+1)); echo "$n" > "'"$PROGRESS_FILE"'"; echo "$n"')
  else
    done=$(( $(cat "$PROGRESS_FILE") + 1 ))
    echo "$done" > "$PROGRESS_FILE"
  fi
  local pct=$(( done * 100 / TOTAL ))
  local now=$(date +%s)
  local elapsed=$(( now - ${STEP2_START:-$now} ))
  local eta=""
  if (( done > 0 && elapsed > 0 )); then
    local remaining=$(( (elapsed * (TOTAL - done)) / done ))
    eta="  ETA $(fmt_elapsed $remaining)"
  fi
  printf '\r  [%3d%%] %d / %d scored%s    ' "$pct" "$done" "$TOTAL" "$eta" >&2
}
export -f _score_one
export -f fmt_elapsed
export STEP2_START=$(date +%s)

if command -v parallel &>/dev/null; then
  parallel -j"$PARALLEL" _score_one {} < "$CLAUSES_FILE" > "$SCORES_FILE" || true
else
  xargs -P "$PARALLEL" -I{} bash -c '_score_one "$@"' _ {} \
    < "$CLAUSES_FILE" > "$SCORES_FILE" || true
fi
echo ""  # newline after \r progress

rm -f "$PROGRESS_FILE"

SCORED=$(grep -c $'.' "$SCORES_FILE" 2>/dev/null || echo 0)
STEP_ELAPSED=$(( $(date +%s) - STEP_START ))
echo "  ✓ $SCORED / $TOTAL clauses produced non-empty networks"
echo "  ✓ Scoring completed in $(fmt_elapsed $STEP_ELAPSED)"
if (( SCORED > 0 && STEP_ELAPSED > 0 )); then
  echo "  ≈ $(( (STEP_ELAPSED * 1000) / SCORED )) ms per clause average"
fi
echo ""

# ─────────────────────────────────────────────────── Step 3: rank ─────────────
STEP_START=$(date +%s)
echo "═══ Step 3/4: Ranking by composite score ═══"
echo "  Formula: SC × (H_stat + H_avg + spectral_gap)"
# TSV columns: 1=clause 2=V 3=E 4=sc 5=H_avg 6=H_stat 7=gap
awk -F'\t' 'NF==7 {
  sc    = ($4 == "true") ? 1 : 0
  score = sc * ($5 + $6 + $7)
  printf "%s\t%s\t%s\t%s\t%.6f\t%.6f\t%.6f\t%.6f\n", $1,$2,$3,$4,$5,$6,$7,score
}' "$SCORES_FILE" \
  | sort -t$'\t' -k8 -rn \
  | head -n "$TOP_N" \
  > "$TOP_FILE"

STEP_ELAPSED=$(( $(date +%s) - STEP_START ))
echo ""
echo "  Top $TOP_N clauses (ranked in $(fmt_elapsed $STEP_ELAPSED)):"
echo "  ┌────────┬──────┬───────┬───────┬────────┬────────┬────────┬───────────────────────────────────"
printf "  │ score  │  V   │   E   │  SC   │ H_avg  │ H_stat │  gap   │ clause\n"
echo "  ├────────┼──────┼───────┼───────┼────────┼────────┼────────┼───────────────────────────────────"
awk -F'\t' '{
  printf "  │ %6.3f │ %4s │ %5s │ %-5s │ %6.4f │ %6.4f │ %6.4f │ %s\n",
    $8, $2, $3, $4, $5, $6, $7, $1
}' "$TOP_FILE"
echo "  └────────┴──────┴───────┴───────┴────────┴────────┴────────┴───────────────────────────────────"
echo ""

# ─────────────────────────────────────────────────── Step 4: walk top-N ───────
STEP_START=$(date +%s)
echo "═══ Step 4/4: Walking top $TOP_N networks ($STEPS steps each) ═══"
WALK_NUM=0
WALK_TOTAL=$(wc -l < "$TOP_FILE")
while IFS=$'\t' read -r clause vertices edges sc avg_H stat_H gap score; do
  WALK_NUM=$((WALK_NUM + 1))
  safe=$(safe_name "$clause")
  NET_FILE="$NETS_DIR/${safe}.rnet"
  WALK_FILE="$OUTPUT_DIR/${safe}.walk"
  ANALYSIS_FILE="$OUTPUT_DIR/${safe}.analysis"

  echo "  [$WALK_NUM/$WALK_TOTAL] $clause"

  # Prefer loading cached .rnet; only rebuild if missing
  if [[ -f "$NET_FILE" ]]; then
    echo "    → Loading cached network from $NET_FILE"
    LOAD_ARGS="--load $NET_FILE"
    BUILD_ARGS=""
  else
    echo "    → No cached .rnet found; rebuilding from clause"
    LOAD_ARGS=""
    BUILD_ARGS="-L $L -c $CIPHER --vertex \"$clause\" --juxt \"$JUXT\" -d $DISTANCE -T $TEMP --save $NET_FILE"
  fi

  # Analysis pass
  WALK_START=$(date +%s)
  if [[ -n "$LOAD_ARGS" ]]; then
    $CMD name.ncg777.maths.numbers.fixed.rhythm.apps.RhythmNetworkApp \
      $LOAD_ARGS \
      -t "$NAV_TEMP" \
      --analyze \
      --no-walk \
      > "$ANALYSIS_FILE" 2>&1 || { echo "    ⚠ analysis failed — skipped"; continue; }
  else
    eval $CMD name.ncg777.maths.numbers.fixed.rhythm.apps.RhythmNetworkApp \
      $BUILD_ARGS \
      -t "$NAV_TEMP" \
      --analyze \
      --no-walk \
      > "$ANALYSIS_FILE" 2>&1 || { echo "    ⚠ analysis failed — skipped"; continue; }
  fi

  # Walk pass (always load from the saved .rnet if possible)
  if [[ -f "$NET_FILE" ]]; then
    $CMD name.ncg777.maths.numbers.fixed.rhythm.apps.RhythmNetworkApp \
      --load "$NET_FILE" \
      -t "$NAV_TEMP" \
      -s "$STEPS" \
      > "$WALK_FILE" 2>/dev/null || { echo "    ⚠ walk failed — skipped"; continue; }
  else
    eval $CMD name.ncg777.maths.numbers.fixed.rhythm.apps.RhythmNetworkApp \
      $BUILD_ARGS \
      -t "$NAV_TEMP" \
      -s "$STEPS" \
      > "$WALK_FILE" 2>/dev/null || { echo "    ⚠ walk failed — skipped"; continue; }
  fi

  WALK_ELAPSED=$(( $(date +%s) - WALK_START ))
  echo "    ✓ done in $(fmt_elapsed $WALK_ELAPSED)"
  echo "      analysis → $ANALYSIS_FILE"
  echo "      walk     → $WALK_FILE"
  echo "      network  → $NET_FILE"
done < "$TOP_FILE"

STEP_ELAPSED=$(( $(date +%s) - STEP_START ))
TOTAL_ELAPSED=$(( $(date +%s) - PIPELINE_START ))
echo ""
echo "═══ Pipeline complete ═══"
echo "  Step 4 (walks):    $(fmt_elapsed $STEP_ELAPSED)"
echo "  Total pipeline:    $(fmt_elapsed $TOTAL_ELAPSED)"
echo "  Output directory:  $OUTPUT_DIR/"
echo "  Cached networks:   $NETS_DIR/"
echo ""
echo "  Files per winning clause:"
echo "    *.rnet      serialised network (reload with --load)"
echo "    *.walk      rhythm walk ($STEPS steps)"
echo "    *.analysis  human-readable metrics"
