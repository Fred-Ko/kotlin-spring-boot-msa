#!/bin/bash

# 도메인 이름을 파라미터로 받기
if [ -z "$1" ]; then
    echo "사용법: $0 <도메인_이름>"
    exit 1
fi

DOMAIN_NAME="$1"
DOMAIN_NAME_CAMEL="$(tr '[:lower:]' '[:upper:]' <<<${DOMAIN_NAME:0:1})${DOMAIN_NAME:1}"

# 패키지 이름 설정 (예: com.example)
BASE_PACKAGE="com.ddd"

# 패키지 경로를 폴더 구조로 변환
BASE_DIR="restaurant/domains"
PACKAGE_PATH=$(echo "$BASE_PACKAGE" | sed 's/\./\//g')

# 루트 디렉토리 생성
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service"

# apps 모듈 생성
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service/apps/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/apps"
touch "$BASE_DIR/$DOMAIN_NAME-service/apps/build.gradle.kts"
touch "$BASE_DIR/$DOMAIN_NAME-service/apps/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/apps/${DOMAIN_NAME_CAMEL}Application.kt"

# presentation 모듈 생성
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service/presentation/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/presentation/api/v1/command"
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service/presentation/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/presentation/api/v1/query"
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service/presentation/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/presentation/dto/request"
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service/presentation/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/presentation/dto/response"
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service/presentation/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/presentation/config"
touch "$BASE_DIR/$DOMAIN_NAME-service/presentation/build.gradle.kts"
touch "$BASE_DIR/$DOMAIN_NAME-service/presentation/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/presentation/api/v1/command/${DOMAIN_NAME_CAMEL}CommandController.kt"
touch "$BASE_DIR/$DOMAIN_NAME-service/presentation/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/presentation/api/v1/query/${DOMAIN_NAME_CAMEL}QueryController.kt"
touch "$BASE_DIR/$DOMAIN_NAME-service/presentation/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/presentation/dto/request/${DOMAIN_NAME_CAMEL}Request.kt"
touch "$BASE_DIR/$DOMAIN_NAME-service/presentation/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/presentation/dto/response/${DOMAIN_NAME_CAMEL}Response.kt"
touch "$BASE_DIR/$DOMAIN_NAME-service/presentation/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/presentation/config/WebConfig.kt"
touch "$BASE_DIR/$DOMAIN_NAME-service/presentation/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/presentation/config/SecurityConfig.kt"

# application 모듈 생성
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service/application/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/application/command"
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service/application/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/application/query"
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service/application/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/application/usecase"
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service/application/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/application/saga"
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service/application/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/application/event"
touch "$BASE_DIR/$DOMAIN_NAME-service/application/build.gradle.kts"
touch "$BASE_DIR/$DOMAIN_NAME-service/application/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/application/command/${DOMAIN_NAME_CAMEL}CommandHandler.kt"
touch "$BASE_DIR/$DOMAIN_NAME-service/application/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/application/query/${DOMAIN_NAME_CAMEL}QueryHandler.kt"
touch "$BASE_DIR/$DOMAIN_NAME-service/application/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/application/usecase/${DOMAIN_NAME_CAMEL}UseCase.kt"
touch "$BASE_DIR/$DOMAIN_NAME-service/application/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/application/saga/${DOMAIN_NAME_CAMEL}Saga.kt"
touch "$BASE_DIR/$DOMAIN_NAME-service/application/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/application/event/${DOMAIN_NAME_CAMEL}EventHandler.kt"

# domain 모듈 생성
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service/domain/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/domain/model/aggregate"
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service/domain/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/domain/model/vo"
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service/domain/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/domain/model/event"
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service/domain/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/domain/repository"
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service/domain/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/domain/service"
touch "$BASE_DIR/$DOMAIN_NAME-service/domain/build.gradle.kts"
touch "$BASE_DIR/$DOMAIN_NAME-service/domain/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/domain/model/aggregate/${DOMAIN_NAME_CAMEL}.kt"
touch "$BASE_DIR/$DOMAIN_NAME-service/domain/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/domain/model/event/${DOMAIN_NAME_CAMEL}Event.kt"
touch "$BASE_DIR/$DOMAIN_NAME-service/domain/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/domain/repository/${DOMAIN_NAME_CAMEL}Repository.kt"
touch "$BASE_DIR/$DOMAIN_NAME-service/domain/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/domain/service/${DOMAIN_NAME_CAMEL}DomainService.kt"

# infra 모듈 생성
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service/infrastructure/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/infrastructure/persistence/entity"
mkdir -p "$BASE_DIR/$DOMAIN_NAME-service/infrastructure/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/infrastructure/persistence/repository"
touch "$BASE_DIR/$DOMAIN_NAME-service/infrastructure/build.gradle.kts"
touch "$BASE_DIR/$DOMAIN_NAME-service/infrastructure/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/infrastructure/persistence/entity/${DOMAIN_NAME_CAMEL}JpaEntity.kt"
touch "$BASE_DIR/$DOMAIN_NAME-service/infrastructure/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/infrastructure/persistence/repository/${DOMAIN_NAME_CAMEL}JpaRepository.kt"
touch "$BASE_DIR/$DOMAIN_NAME-service/infrastructure/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/infrastructure/persistence/repository/${DOMAIN_NAME_CAMEL}KotlinJdslRepository.kt"
touch "$BASE_DIR/$DOMAIN_NAME-service/infrastructure/src/main/kotlin/$PACKAGE_PATH/${DOMAIN_NAME}/infrastructure/persistence/repository/${DOMAIN_NAME_CAMEL}Repository.kt"

echo "폴더와 파일 생성이 완료되었습니다."

# ./gen.sh delivery
# ./gen.sh order
# ./gen.sh payment
# ./gen.sh restaurant