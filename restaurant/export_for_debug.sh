#!/bin/bash

# ===== 변수 정의 =====
# ===== 변수 정의 및 파라미터 처리 =====
OUTPUT_FILE=${OUTPUT_FILE:-exported_sources.txt}
TREE_IGNORE_DIRS='build|bin|test'
TREE_PATTERNS='*.kt|*.kts|*.gradle|*.avsc'
FIND_IGNORE_DIRS="build bin test"                                                     # 공백으로 구분된 문자열
FIND_PATTERNS="*.kt *.kts *.gradle *toml settings.gradle.kts build.gradle.kts *.avsc" # 공백으로 구분된 문자열

# 파라미터로 TARGET_PATHS를 받음. 없으면 기본값 사용
if [ "$#" -gt 0 ]; then
  TARGET_PATHS=("$@")
else
  echo "Error: No target paths provided. Usage: $0 [target_paths...]"
  exit 1
fi

echo "검색 대상 경로: ${TARGET_PATHS[@]}"


echo "아래는 ${TARGET_PATHS[@]}경로의 프로젝트 구조와 코드가 포함되어 있습니다." >"$OUTPUT_FILE"
echo "아래 정보를 보고 리팩토링 할 부분이 있는지 검토하고 문제점을 지적하라." >>"$OUTPUT_FILE"
echo "단 대화는 모두 한글로 진행한다.." >>"$OUTPUT_FILE"

# ===== 프로젝트 구조 출력 =====
echo -e "\n==============================================================" >>"$OUTPUT_FILE"
echo -e "\n# Project Structure\n" >>"$OUTPUT_FILE"
tree "${TARGET_PATHS[@]}" \
  -I "$TREE_IGNORE_DIRS" \
  -P "$TREE_PATTERNS" \
  >>"$OUTPUT_FILE"
echo -e "==============================================================\n\n" >>"$OUTPUT_FILE"

# ===== Kotlin/Gradle 파일 수집 및 출력 =====
find "${TARGET_PATHS[@]}" \
  -type d \( $(printf -- '-name %s -o ' $FIND_IGNORE_DIRS) -false \) -prune -o \
  -type f \( $(printf -- '-name %s -o ' $FIND_PATTERNS) -false \) -print |
  sort -u |
  while IFS= read -r file; do
    {
      echo -e "\n\n===================================================================="
      echo -e " File: $file"
      # Use realpath if available, fallback to readlink -f for compatibility
      if command -v realpath >/dev/null 2>&1; then
        echo -e " Path: $(realpath --relative-to=. "$file" 2>/dev/null || echo "$file")"
      else
        echo -e " Path: $(readlink -f "$file" 2>/dev/null || echo "$file")"
      fi
      # Use portable date format
      echo -e " Timestamp: $(date -u '+%Y-%m-%d %H:%M:%S')"
      echo -e "===================================================================="
      cat "$file"
      echo -e "\n===================================================================="
    } >>"$OUTPUT_FILE"
  done
