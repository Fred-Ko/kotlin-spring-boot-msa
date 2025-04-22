#!/bin/bash

# File to store the output
OUTPUT_FILE="a"

# Run Gradle clean
./gradlew clean 2>&1 | tee $OUTPUT_FILE

cat << 'EOF' > a
이 문서는
```
#!/bin/bash

# File to store the output
OUTPUT_FILE="a"

# Run Gradle clean
./gradlew clean 2>&1 | tee $OUTPUT_FILE

# Run Gradle build for specified modules, excluding tests
./gradlew :domains:user:application:build \
         :domains:user:apps:build \
         :domains:user:domain:build \
         :domains:user:infrastructure:build \
         :domains:user:presentation:build \
         :independent:outbox:build \
         -x test 2>&1 | tee -a $OUTPUT_FILE

# Add a section separator
echo -e "\n\n==============================================================" >> $OUTPUT_FILE
echo -e " Gradle Build Completed: $(date '+%Y-%m-%d %H:%M:%S') " >> $OUTPUT_FILE
echo -e "==============================================================\n" >> $OUTPUT_FILE

# Run ktlintFormat
./gradlew ktlintFormat 2>&1 | tee -a $OUTPUT_FILE

# Add another section separator
echo -e "\n\n==============================================================" >> $OUTPUT_FILE
echo -e " ktlintFormat Completed: $(date '+%Y-%m-%d %H:%M:%S') " >> $OUTPUT_FILE
echo -e "==============================================================\n" >> $OUTPUT_FILE

# Collect and append Kotlin/Gradle files with enhanced separators
find domains/user domains/common independent \
  -type d \( -name build -o -name bin -o -name test \) -prune -o \
  -type f \( -name "*.kt" -o -name "*.kts" -o -name "*.gradle" \) -print \
  | sort -u \
  | while read -r file; do
      echo -e "\n\n====================================================================" >> $OUTPUT_FILE
      echo -e " File: $file" >> $OUTPUT_FILE
      echo -e " Path: $(realpath "$file")" >> $OUTPUT_FILE
      echo -e " Timestamp: $(date '+%Y-%m-%d %H:%M:%S')" >> $OUTPUT_FILE
      echo -e "====================================================================" >> $OUTPUT_FILE
      cat "$file" >> $OUTPUT_FILE
      echo -e "\n--------------------------------------------------------------------\n" >> $OUTPUT_FILE
  done

# Final footer
echo -e "\n==============================================================" >> $OUTPUT_FILE
echo -e " File Collection Completed: $(date '+%Y-%m-%d %H:%M:%S') " >> $OUTPUT_FILE
echo -e "==============================================================" >> $OUTPUT_FILE
```
명령어의 결과이다.

즉 모듈들의 전체 코드가 첨부되어있다.
코드를 보면 불완전 한 부분도 있고 규칙을 지키지 않는 부분도 있다.
그래서 이를 수정하기 위한 작업 지시서가 필요하다. 작업 지시서를 만들어라.
- 규칙을 지키지 않는 부분에 대한 작업지시서가 우선이다.
- 프로그램이 정상적으로 동작하기위해 추가적으로 작업해야하는 작업지시서는 그다음이다.
- 작업지시서는 단계가 명확하게 표현되어야 한다.
- 작업지시서는 ~을 확인해라. 같은 모호한 문장은 절대로 안된다. ~을 어떻게 고쳐라 같이 매우 명시적이여야 한다.
- 작업지시서는 어떤 파일이 어떤 부분이 잘못작성되었고 어떻게 고쳐야한다. 그리고 근거는 무엇이다 라는 명시적 문장이 있어야 한다.
- 작업지시서 제일 하단에는 모든 작업이 끝나고 확인해야할 체크리스트를 작성한다.
- 작업지시서 제일 하단에는 작업지시서에 나오는 규칙을 중복없이 적어서 작업자가 참고토록 한다. 등장하지 않는 규칙은 적지않고, 등장하는 규칙은 원래 문장 그대로 적어야한다. 생략 불가.

작업 지시서에 항상 상단에 첨부할 문구
```
작업시 항상 기억해야할 규칙
- 파일을 수정할때는 import 구문이 완벽한지 한번씩 더 체크하도록 한다. 만약 확인이 필요하다면 다른 파일을 조회할 수 있다.
- 작업을 진행하는 도중에 작업지시서에 없는 수정사항은 일단 보류하고 작업지시서를 최우선으로 수정한다.
- 이런식의 참조는 금지한다. ( ex -> event: com.restaurant.domains.common.domain.event.DomainEvent ) 항상 import 구문을 추가해서 참조하도록 한다.
```
EOF
echo -e "\n--------------------------------------------------------------------\n\n" >> $OUTPUT_FILE

# Run Gradle build for specified modules, excluding tests
./gradlew :domains:user:application:build \
  :domains:user:apps:build \
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

# Collect and append Kotlin/Gradle files with enhanced separators
find domains/user domains/common independent \
  -type d \( -name build -o -name bin -o -name test \) -prune -o \
  -type f \( -name "*.kt" -o -name "*.kts" -o -name "*.gradle" \) -print |
  sort -u |
  while read -r file; do
    echo -e "\n\n====================================================================" >>$OUTPUT_FILE
    echo -e " File: $file" >>$OUTPUT_FILE
    echo -e " Path: $(realpath "$file")" >>$OUTPUT_FILE
    echo -e " Timestamp: $(date '+%Y-%m-%d %H:%M:%S')" >>$OUTPUT_FILE
    echo -e "====================================================================" >>$OUTPUT_FILE
    cat "$file" >>$OUTPUT_FILE
    echo -e "\n--------------------------------------------------------------------\n" >>$OUTPUT_FILE
  done

# Final footer
echo -e "\n==============================================================" >>$OUTPUT_FILE
echo -e " File Collection Completed: $(date '+%Y-%m-%d %H:%M:%S') " >>$OUTPUT_FILE
echo -e "==============================================================" >>$OUTPUT_FILE


