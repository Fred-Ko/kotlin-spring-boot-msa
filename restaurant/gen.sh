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
- 작업지시서에 코드 레벨을 너무 자세하게 설명하지 않아도 된다.
- 작업지시서 제일 하단에는 모든 작업이 끝나고 확인해야할 체크리스트를 작성한다.
- 이프로젝트는 Kotlin 으로 작성되어있다. Java는 사용하지 않는다.
- 최신 스테이블 버전 목록은 작업지시서에 생략하지 않고 꼭 기입한다.

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
- 확인하고 싶은 디렉토리 구조가 있다면 tree 명령어로 최대한 효율적이게 진행하라. 단 bin,build 는 제외한다.

### 플러그인 (Plugins) - 최신 스테이블 버전
1. **`org.springframework.boot`**: `3.3.4`  
   - Spring Boot Gradle 플러그인 최신 버전 (Gradle Plugin Portal 및 GitHub 확인).[](https://github.com/spring-projects/spring-boot/releases)
2. **`io.spring.dependency-management`**: `1.1.6`  
   - Spring Dependency Management 플러그인 최신 버전 (Gradle Plugin Portal 확인).
3. **`org.jetbrains.kotlin.jvm`**: `2.0.20`  
   - Kotlin JVM 플러그인 최신 버전 (JetBrains GitHub 및 Maven Central 확인).
4. **`org.jetbrains.kotlin.plugin.spring`**: `2.0.20`  
   - Kotlin Spring 플러그인, Kotlin 버전과 동기화 (JetBrains 공식 문서 확인).
5. **`org.jetbrains.kotlin.plugin.jpa`**: `2.0.20`  
   - Kotlin JPA 플러그인, Kotlin 버전과 동기화 (JetBrains 공식 문서 확인).
6. **`org.jetbrains.kotlin.plugin.allopen`**: `2.0.20`  
   - Kotlin AllOpen 플러그인, Kotlin 버전과 동기화 (JetBrains 공식 문서 확인).
7. **`org.jlleitschuh.gradle.ktlint`**: `12.1.1`  
   - ktlint Gradle 플러그인 최신 버전 (GitHub 릴리스 확인).[](https://github.com/JLLeitschuh/ktlint-gradle)
8. **`com.github.davidmc24.gradle.plugin.avro`**: `1.9.1`  
   - Avro Gradle 플러그인 최신 버전 (GitHub 릴리스 확인).[](https://github.com/davidmc24/gradle-avro-plugin)

### 외부 라이브러리 의존성 - 최신 스테이블 버전
Spring Boot 3.3.4의 BOM(`spring-boot-dependencies`)을 기준으로 관리되는 의존성은 해당 BOM에서 제공하는 버전을 사용합니다. Spring Boot BOM 외의 의존성은 Maven Central 또는 공식 문서에서 최신 버전을 확인했습니다.

1. **`org.jetbrains.kotlin:kotlin-stdlib`**: `2.0.20` (Spring Boot BOM: `2.0.20`)
2. **`org.jetbrains.kotlin:kotlin-reflect`**: `2.0.20` (Spring Boot BOM: `2.0.20`)
3. **`org.jetbrains.kotlin:kotlin-stdlib-common`**: `2.0.20` (Kotlin 버전과 동기화)
4. **`io.github.microutils:kotlin-logging-jvm`**: `3.0.5` (Maven Central 확인)
5. **`org.jetbrains.kotlinx:kotlinx-serialization-core`**: `1.7.3` (Kotlinx GitHub 확인)
6. **`org.springframework.boot:spring-boot-starter`**: `3.3.4` (Spring Boot BOM)
7. **`org.springframework.boot:spring-boot-starter-web`**: `3.3.4` (Spring Boot BOM)
8. **`org.springframework.boot:spring-boot-starter-data-jpa`**: `3.3.4` (Spring Boot BOM)
9. **`org.springframework.boot:spring-boot-starter-validation`**: `3.3.4` (Spring Boot BOM)
10. **`org.springframework.boot:spring-boot-starter-actuator`**: `3.3.4` (Spring Boot BOM)
11. **`org.springframework.boot:spring-boot-starter-security`**: `3.3.4` (Spring Boot BOM)
12. **`org.springframework.boot:spring-boot-starter-aop`**: `3.3.4` (Spring Boot BOM)
13. **`org.springframework.boot:spring-boot-starter-hateoas`**: `3.3.4` (Spring Boot BOM)
14. **`org.springframework:spring-context`**: `6.1.14` (Spring Boot BOM)
15. **`org.springframework:spring-tx`**: `6.1.14` (Spring Boot BOM)
16. **`jakarta.persistence:jakarta.persistence-api`**: `3.1.0` (Spring Boot BOM)
17. **`jakarta.validation:jakarta.validation-api`**: `3.0.2` (Spring Boot BOM)
18. **`com.h2database:h2`**: `2.3.230` (Spring Boot BOM)
19. **`org.postgresql:postgresql`**: `42.7.4` (Spring Boot BOM)
20. **`com.zaxxer:HikariCP`**: `5.1.0` (Spring Boot BOM)
21. **`org.mapstruct:mapstruct`**: `1.6.2` (Spring Boot BOM)
22. **`org.mapstruct:mapstruct-processor`**: `1.6.2` (Spring Boot BOM)
23. **`org.apache.avro:avro`**: `1.12.0` (Spring Boot BOM)
24. **`org.springframework.kafka:spring-kafka`**: `3.2.4` (Spring Boot BOM)
25. **`org.apache.kafka:kafka-clients`**: `3.8.0` (Spring Boot BOM)
26. **`io.confluent:kafka-avro-serializer`**: `7.6.3` (Confluent Maven Repository 확인)
27. **`com.fasterxml.jackson.module:jackson-module-kotlin`**: `2.17.2` (Spring Boot BOM)
28. **`com.fasterxml.jackson.datatype:jackson-datatype-jsr310`**: `2.17.2` (Spring Boot BOM)
29. **`com.fasterxml.jackson.core:jackson-databind`**: `2.17.2` (Spring Boot BOM)
30. **`org.springdoc:springdoc-openapi-starter-webmvc-ui`**: `2.6.0` (Springdoc GitHub 확인)
31. **`io.github.resilience4j:resilience4j-spring-boot3`**: `2.2.0` (Resilience4j GitHub 확인)
32. **`org.slf4j:slf4j-api`**: `2.0.16` (Spring Boot BOM)
33. **`org.springframework.boot:spring-boot-starter-test`**: `3.3.4` (Spring Boot BOM)
34. **`io.kotest:kotest-runner-junit5`**: `5.9.1` (Kotest GitHub 확인)
35. **`io.kotest:kotest-assertions-core`**: `5.9.1` (Kotest GitHub 확인)
36. **`io.mockk:mockk`**: `1.13.13` (MockK GitHub 확인)
37. **`org.mockito.kotlin:mockito-kotlin`**: `5.4.0` (Mockito Kotlin GitHub 확인)
38. **`org.assertj:assertj-core`**: `3.26.3` (Spring Boot BOM)
39. **`org.jetbrains.kotlin:kotlin-test-junit5`**: `2.0.20` (Kotlin 버전과 동기화)
40. **`org.testcontainers:postgresql`**: `1.20.2` (Testcontainers GitHub 확인)
41. **`org.testcontainers:kafka`**: `1.20.2` (Testcontainers GitHub 확인)
42. **`org.testcontainers:junit-jupiter`**: `1.20.2` (Testcontainers GitHub 확인)
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
tree domains independent apps \
  -I 'build|bin|test' \
  -P '*.kt|*.kts|*.gradle' \
  >>$OUTPUT_FILE
echo -e "==============================================================\n\n" >>$OUTPUT_FILE

# Collect and append Kotlin/Gradle files with enhanced separators
find domains/user domains/common settings.gradle.kts build.gradle.kts apps independent \
  -type d \( -name build -o -name bin -o -name test \) -prune -o \
  -type f \( -name "*.kt" -o -name "*.kts" -o -name "*.gradle" -o -name "settings.gradle.kts" -o -name "build.gradle.kts" \) -print | \
  sort -u | \
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
      echo -e "\n--------------------------------------------------------------------\n"
    } >>"$OUTPUT_FILE"
  done

# Final footer
echo -e "\n==============================================================" >>$OUTPUT_FILE
echo -e " File Collection Completed: $(date '+%Y-%m-%d %H:%M:%S') " >>$OUTPUT_FILE
echo -e "==============================================================" >>$OUTPUT_FILE
