#!/bin/bash

# File to store the output
OUTPUT_FILE="a"

# Run Gradle clean
./gradlew clean 2>&1 | tee $OUTPUT_FILE

cat <<'EOF' >a
이 문서는
```
#!/bin/bash

# File to store the output
OUTPUT_FILE="a"

# Run Gradle clean
./gradlew clean 2>&1 | tee $OUTPUT_FILE

echo -e "\n--------------------------------------------------------------------\n\n" >> $OUTPUT_FILE

# Run Gradle build for specified modules, excluding tests
./gradlew :domains:user:application:build \
  :apps:user-app:build \
  :domains:user:domain:build \
  :domains:user:infrastructure:build \
  :domains:user:presentation:build \
  :independent:outbox:build \
  -x test 2>&1 | tee -a $OUTPUT_FILE

# Add a section separator
echo -e "\n\n==============================================================" >>$OUTPUT_FILE
echo -e " Gradle Build Completed: $(date '+%Y-%m-%d %H:%M:%S') " >>$OUTPUT_FILE
echo -e "==============================================================\n" >>$OUTPUT_FILE

# Run ktlintFormat
./gradlew ktlintFormat 2>&1 | tee -a $OUTPUT_FILE

# Add another section separator
echo -e "\n\n==============================================================" >>$OUTPUT_FILE
echo -e " ktlintFormat Completed: $(date '+%Y-%m-%d %H:%M:%S') " >>$OUTPUT_FILE
echo -e "==============================================================\n" >>$OUTPUT_FILE

echo -e "\n==============================================================" >>$OUTPUT_FILE
echo -e "\n# Project Structure\n" >>$OUTPUT_FILE
tree domains independent \
  -I 'build|bin|test' \
  -P '*.kt|*.kts|*.gradle' \
  >>$OUTPUT_FILE
echo -e "==============================================================\n\n" >>$OUTPUT_FILE

# Collect and append Kotlin/Gradle files with enhanced separators
find domains/user domains/common independent \
  -type d \( -name build -o -name bin -o -name test \) -prune -o \
  -type f \( -name "*.kt" -o -name "*.kts" -o -name "*.gradle" \) -print |
  sort -u |
  while read -r file; do
    {
      echo -e "\n\n===================================================================="
      echo -e " File: $file"
      echo -e " Path: $(realpath --relative-to=. "$file")"
      echo -e " Timestamp: $(date '+%Y-%m-%d %H:%M:%S')"
      echo -e "===================================================================="
      cat "$file"
      echo -e "\n--------------------------------------------------------------------\n"
    } >> "$OUTPUT_FILE"
  done

# Final footer
echo -e "\n==============================================================" >>$OUTPUT_FILE
echo -e " File Collection Completed: $(date '+%Y-%m-%d %H:%M:%S') " >>$OUTPUT_FILE
echo -e "==============================================================" >>$OUTPUT_FILE
```
명령어의 결과이다.

즉 모듈들의 전체 코드가 첨부되어있다.
코드를 보면 불완전 한 부분도 있고 규칙을 지키지 않는 부분도 있다.
따라서 코드에 기준으로 작업을 완료하려고 하면 완성이 안될꺼다. 왜냐하면 코드가 정말 엉망이기 떄문이다. 어떤 요구사항인지 잘생각해서 수정계획을 해야한다.
그래서 이를 수정하기 위한 작업 지시서가 필요하다. 작업 지시서를 만들어라.
- tree 구조를 보고 규칙에 맞지않는 부분을 찾아서 삭제할 파일, 이동할 파일에 대한 작업지시서는 최우선이다. 이동으로 처리할 일을 삭제후 생성으로 처리하지 말아라.
- 규칙을 지키지 않는 부분에 대한 작업지시서를 작성하라
- 프로그램이 정상적으로 동작하기위해 추가적으로 작업해야하는 작업지시서를 작성하라
- 작업지시서는 단계가 명확하게 표현되어야 한다. 하지만 작업의 순서를 잘생각해서 먼저되어야 하는건 꼭 먼저하도록 단계를 잘 생각하라.
- build.gradle.kts는 최상위 파일에 공통부분을 최대한 모아놓고 각 모듈별로 필요한 부분은 모듈 내부에 작성한다.
- 작업지시서에서는 Gradle 의존성에 대한 버전에 대해서 지적하지 않는다. 모두 최신버전으로 팀내에서 직접 관리하는 영역이다.
- 필요없는 파일은 삭제하도록 지시하라.
- 필요없는 코드는 주석보단 삭제를 하도록 지시하라.
- 현재 코드를 유지하는 경우는 작업지시서에 굳이 명시하지 않아도 된다.
- 작업지시서는 ~을 확인해라. 같은 모호한 문장은 절대로 안된다. ~을 어떻게 고쳐라 같이 매우 명시적이여야 한다.
- 작업지시서는 어떤 파일이 어떤 부분이 잘못작성되었고 어떻게 고쳐야한다. 그리고 근거는 무엇이다 라는 명시적 문장이 있어야 한다.
- 작업지시서 제일 하단에는 모든 작업이 끝나고 확인해야할 체크리스트를 작성한다.
- 이프로젝트는 Kotlin 으로 작성되어있다. Java는 사용하지 않는다.

작업 지시서에 항상 상단에 첨부할 문구
```
다음 작업 지시서를 그대로 따라서 수정작업을 진행하라. 최대한 다른 판단은 히지 않는다.
다음 작업 지시서를 그대로 따라서 수정작업을 진행하라. 최대한 다른 판단은 히지 않는다.
다음 작업 지시서를 그대로 따라서 수정작업을 진행하라. 최대한 다른 판단은 히지 않는다.
다음 작업 지시서를 그대로 따라서 수정작업을 진행하라. 최대한 다른 판단은 히지 않는다.
다음 작업 지시서를 그대로 따라서 수정작업을 진행하라. 최대한 다른 판단은 히지 않는다.

작업시 항상 기억해야할 규칙
- 파일을 수정할때는 import 구문이 완벽한지 한번씩 더 체크하도록 한다. 만약 확인이 필요하다면 다른 파일을 조회할 수 있다.
- 작업을 진행하는 도중에 작업지시서에 없는 수정사항은 일단 보류하고 작업지시서를 최우선으로 수정한다.
- 이런식의 참조는 금지한다. ( ex -> event: com.restaurant.domains.common.domain.event.DomainEvent ) 항상 import 구문을 추가해서 참조하도록 한다.
- com.restaurant.domains.common.* 과 같은 import는 절대로 금지한다.
- 공통으로 수정해야할 패턴이 확실하다면 커맨드 명령어로 한번에 처리한다. 단, 프로젝트 내부에 build,bin 같은 제외할 경로는 명확히 명시한다.
- 필요없는 코드는 주석처리 하지 말고 삭제하도록 하라.
- 파일을 생성 할때는 위치가 올바른지 확인하기 위해서 파일 구조도를 참고하도록 한다.
- 이동으로 처리할 일을 삭제후 생성으로 처리하지 말아라.
- 의존성 버전은 항상 tools 이용해서 최신 스테이블 버전을 활용하도록 한다.
```
EOF
echo -e "\n--------------------------------------------------------------------\n\n" >>$OUTPUT_FILE

# Run Gradle build for specified modules, excluding tests
./gradlew :domains:user:application:build \
  :apps:user-app:build \
  :domains:user:domain:build \
  :domains:user:infrastructure:build \
  :domains:user:presentation:build \
  :independent:outbox:build \
  -x test 2>&1 | tee -a $OUTPUT_FILE

# Add a section separator
echo -e "\n\n==============================================================" >>$OUTPUT_FILE
echo -e " Gradle Build Completed: $(date '+%Y-%m-%d %H:%M:%S') " >>$OUTPUT_FILE
echo -e "==============================================================\n" >>$OUTPUT_FILE

# Run ktlintFormat
./gradlew ktlintFormat 2>&1 | tee -a $OUTPUT_FILE

# Add another section separator
echo -e "\n\n==============================================================" >>$OUTPUT_FILE
echo -e " ktlintFormat Completed: $(date '+%Y-%m-%d %H:%M:%S') " >>$OUTPUT_FILE
echo -e "==============================================================\n" >>$OUTPUT_FILE

echo -e "\n==============================================================" >>$OUTPUT_FILE
echo -e "\n# Project Structure\n" >>$OUTPUT_FILE
tree domains independent \
  -I 'build|bin|test' \
  -P '*.kt|*.kts|*.gradle' \
  >>$OUTPUT_FILE
echo -e "==============================================================\n\n" >>$OUTPUT_FILE

# Collect and append Kotlin/Gradle files with enhanced separators
find domains/user domains/common settings.gradle.kts build.gradle.kts \
  -type d \( -name build -o -name bin -o -name test \) -prune -o \
  -type f \( -name "*.kt" -o -name "*.kts" -o -name "*.gradle" -o -name "settings.gradle.kts" -o -name "build.gradle.kts" \) -print |
  sort -u |
  while read -r file; do
    {
      echo -e "\n\n===================================================================="
      echo -e " File: $file"
      echo -e " Path: $(realpath --relative-to=. "$file")"
      echo -e " Timestamp: $(date '+%Y-%m-%d %H:%M:%S')"
      echo -e "===================================================================="
      cat "$file"
      echo -e "\n--------------------------------------------------------------------\n"
    } >>"$OUTPUT_FILE"
  done

# Final footer
echo -e "\n==============================================================" >>$OUTPUT_FILE
echo -e " File Collection Completed: $(date '+%Y-%m-%d %H:%M:%S') " >>$OUTPUT_FILE
echo -e "==============================================================" >>$OUTPUT_FILE
