#!/bin/bash

# 도메인 이름을 파라미터로 받기
if [ -z "$1" ]; then
    echo "사용법: $0 <도메인_이름>"
    exit 1
fi

DOMAIN_NAME="$1"
DOMAIN_NAME_CAMEL="$(echo ${DOMAIN_NAME:0:1} | tr '[:lower:]' '[:upper:]')${DOMAIN_NAME:1}"

# 패키지 이름 설정
BASE_PACKAGE="com.ddd"
PACKAGE_PATH=$(echo "$BASE_PACKAGE" | sed 's/\./\//g')

# 루트 디렉토리 설정
BASE_DIR="restaurant/domains"

# 도메인별 루트 디렉토리 생성
mkdir -p "$BASE_DIR/$DOMAIN_NAME"

# apps 모듈
mkdir -p "$BASE_DIR/$DOMAIN_NAME/apps/src/main/kotlin/$PACKAGE_PATH/apps/$DOMAIN_NAME"
mkdir -p "$BASE_DIR/$DOMAIN_NAME/apps/src/main/resources"
touch "$BASE_DIR/$DOMAIN_NAME/apps/build.gradle.kts"
touch "$BASE_DIR/$DOMAIN_NAME/apps/src/main/kotlin/$PACKAGE_PATH/apps/$DOMAIN_NAME/${DOMAIN_NAME_CAMEL}Application.kt"
touch "$BASE_DIR/$DOMAIN_NAME/apps/src/main/resources/application.yml"

# domain 모듈
mkdir -p "$BASE_DIR/$DOMAIN_NAME/domain/src/main/kotlin/$PACKAGE_PATH/domain/$DOMAIN_NAME"
mkdir -p "$BASE_DIR/$DOMAIN_NAME/domain/src/main/kotlin/$PACKAGE_PATH/domain/$DOMAIN_NAME/aggregate"
mkdir -p "$BASE_DIR/$DOMAIN_NAME/domain/src/main/kotlin/$PACKAGE_PATH/domain/$DOMAIN_NAME/vo"
mkdir -p "$BASE_DIR/$DOMAIN_NAME/domain/src/main/kotlin/$PACKAGE_PATH/domain/$DOMAIN_NAME/repository"
mkdir -p "$BASE_DIR/$DOMAIN_NAME/domain/src/main/kotlin/$PACKAGE_PATH/domain/$DOMAIN_NAME/exception"
touch "$BASE_DIR/$DOMAIN_NAME/domain/build.gradle.kts"
touch "$BASE_DIR/$DOMAIN_NAME/domain/src/main/kotlin/$PACKAGE_PATH/domain/$DOMAIN_NAME/aggregate/${DOMAIN_NAME_CAMEL}.kt"
touch "$BASE_DIR/$DOMAIN_NAME/domain/src/main/kotlin/$PACKAGE_PATH/domain/$DOMAIN_NAME/repository/${DOMAIN_NAME_CAMEL}Repository.kt"
touch "$BASE_DIR/$DOMAIN_NAME/domain/src/main/kotlin/$PACKAGE_PATH/domain/$DOMAIN_NAME/exception/${DOMAIN_NAME_CAMEL}DomainException.kt"

# application 모듈
mkdir -p "$BASE_DIR/$DOMAIN_NAME/application/src/main/kotlin/$PACKAGE_PATH/application/$DOMAIN_NAME/command"
mkdir -p "$BASE_DIR/$DOMAIN_NAME/application/src/main/kotlin/$PACKAGE_PATH/application/$DOMAIN_NAME/query"
mkdir -p "$BASE_DIR/$DOMAIN_NAME/application/src/main/kotlin/$PACKAGE_PATH/application/$DOMAIN_NAME/handler"
mkdir -p "$BASE_DIR/$DOMAIN_NAME/application/src/main/kotlin/$PACKAGE_PATH/application/$DOMAIN_NAME/mapper"
mkdir -p "$BASE_DIR/$DOMAIN_NAME/application/src/main/kotlin/$PACKAGE_PATH/application/$DOMAIN_NAME/common"
touch "$BASE_DIR/$DOMAIN_NAME/application/build.gradle.kts"
touch "$BASE_DIR/$DOMAIN_NAME/application/src/main/kotlin/$PACKAGE_PATH/application/$DOMAIN_NAME/mapper/${DOMAIN_NAME_CAMEL}Mapper.kt"
touch "$BASE_DIR/$DOMAIN_NAME/application/src/main/kotlin/$PACKAGE_PATH/application/$DOMAIN_NAME/common/${DOMAIN_NAME_CAMEL}ApplicationException.kt"

# infrastructure 모듈
mkdir -p "$BASE_DIR/$DOMAIN_NAME/infrastructure/src/main/kotlin/$PACKAGE_PATH/infrastructure/$DOMAIN_NAME/entity"
mkdir -p "$BASE_DIR/$DOMAIN_NAME/infrastructure/src/main/kotlin/$PACKAGE_PATH/infrastructure/$DOMAIN_NAME/mapper"
mkdir -p "$BASE_DIR/$DOMAIN_NAME/infrastructure/src/main/kotlin/$PACKAGE_PATH/infrastructure/$DOMAIN_NAME/repository"
touch "$BASE_DIR/$DOMAIN_NAME/infrastructure/build.gradle.kts"
touch "$BASE_DIR/$DOMAIN_NAME/infrastructure/src/main/kotlin/$PACKAGE_PATH/infrastructure/$DOMAIN_NAME/entity/${DOMAIN_NAME_CAMEL}Entity.kt"
touch "$BASE_DIR/$DOMAIN_NAME/infrastructure/src/main/kotlin/$PACKAGE_PATH/infrastructure/$DOMAIN_NAME/mapper/${DOMAIN_NAME_CAMEL}EntityMapper.kt"
touch "$BASE_DIR/$DOMAIN_NAME/infrastructure/src/main/kotlin/$PACKAGE_PATH/infrastructure/$DOMAIN_NAME/repository/SpringDataJpa${DOMAIN_NAME_CAMEL}Repository.kt"
touch "$BASE_DIR/$DOMAIN_NAME/infrastructure/src/main/kotlin/$PACKAGE_PATH/infrastructure/$DOMAIN_NAME/repository/${DOMAIN_NAME_CAMEL}RepositoryImpl.kt"

# presentation 모듈
mkdir -p "$BASE_DIR/$DOMAIN_NAME/presentation/src/main/kotlin/$PACKAGE_PATH/presentation/$DOMAIN_NAME/v1/command"
mkdir -p "$BASE_DIR/$DOMAIN_NAME/presentation/src/main/kotlin/$PACKAGE_PATH/presentation/$DOMAIN_NAME/v1/query"
mkdir -p "$BASE_DIR/$DOMAIN_NAME/presentation/src/main/kotlin/$PACKAGE_PATH/presentation/$DOMAIN_NAME/v1/dto/request"
mkdir -p "$BASE_DIR/$DOMAIN_NAME/presentation/src/main/kotlin/$PACKAGE_PATH/presentation/$DOMAIN_NAME/v1/dto/response"
touch "$BASE_DIR/$DOMAIN_NAME/presentation/build.gradle.kts"
touch "$BASE_DIR/$DOMAIN_NAME/presentation/src/main/kotlin/$PACKAGE_PATH/presentation/$DOMAIN_NAME/v1/command/${DOMAIN_NAME_CAMEL}CommandController.kt"
touch "$BASE_DIR/$DOMAIN_NAME/presentation/src/main/kotlin/$PACKAGE_PATH/presentation/$DOMAIN_NAME/v1/query/${DOMAIN_NAME_CAMEL}QueryController.kt"

echo "폴더와 파일 생성이 완료되었습니다."

# ./gen.sh delivery
# ./gen.sh order
# ./gen.sh payment
# ./gen.sh restaurant
# ./gen.sh user
