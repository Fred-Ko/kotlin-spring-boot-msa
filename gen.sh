#!/bin/bash

# 스크립트 사용법 안내
usage() {
    echo "Usage: $0 <new_domain_name> <organization_name>"
    echo "Example: $0 order restaurant"
    exit 1
}

# 매개변수 확인
if [ $# -ne 2 ]; then
    usage
fi

# 새로운 도메인 이름 (소문자로 통일)
NEW_DOMAIN=$1
NEW_DOMAIN_LOWER=$(echo "$NEW_DOMAIN" | tr '[:upper:]' '[:lower:]')
NEW_DOMAIN_CAP=$(echo "${NEW_DOMAIN_LOWER^}") # 첫 글자만 대문자

# 조직 이름 (패키지 경로에 사용)
ORG_NAME=$2
ORG_NAME_LOWER=$(echo "$ORG_NAME" | tr '[:upper:]' '[:lower:]')

# 기본 경로 설정
BASE_DIR="restaurant/domains/$NEW_DOMAIN_LOWER"

# 디렉토리 생성 함수
create_directory() {
    local dir="$1"
    echo "Creating directory: $dir"
    mkdir -p "$dir"
}

# 빈 파일 생성 함수
create_empty_file() {
    local file="$1"
    echo "Creating file: $file"
    touch "$file"
}

# 메인 디렉토리 구조 생성
echo "Creating domain structure for: $NEW_DOMAIN_LOWER under $ORG_NAME_LOWER"
create_directory "$BASE_DIR"

# application 모듈
create_directory "$BASE_DIR/application/src/main/kotlin/com/$ORG_NAME_LOWER/application/$NEW_DOMAIN_LOWER"
create_directory "$BASE_DIR/application/src/main/kotlin/com/$ORG_NAME_LOWER/application/$NEW_DOMAIN_LOWER/command"
create_directory "$BASE_DIR/application/src/main/kotlin/com/$ORG_NAME_LOWER/application/$NEW_DOMAIN_LOWER/command/handler"
create_directory "$BASE_DIR/application/src/main/kotlin/com/$ORG_NAME_LOWER/application/$NEW_DOMAIN_LOWER/common"
create_directory "$BASE_DIR/application/src/main/kotlin/com/$ORG_NAME_LOWER/application/$NEW_DOMAIN_LOWER/exception"
create_directory "$BASE_DIR/application/src/main/kotlin/com/$ORG_NAME_LOWER/application/$NEW_DOMAIN_LOWER/extensions"
create_directory "$BASE_DIR/application/src/main/kotlin/com/$ORG_NAME_LOWER/application/$NEW_DOMAIN_LOWER/query"
create_directory "$BASE_DIR/application/src/main/kotlin/com/$ORG_NAME_LOWER/application/$NEW_DOMAIN_LOWER/query/dto"
create_directory "$BASE_DIR/application/src/main/kotlin/com/$ORG_NAME_LOWER/application/$NEW_DOMAIN_LOWER/query/handler"
create_directory "$BASE_DIR/application/src/test/kotlin/com/$ORG_NAME_LOWER/application/$NEW_DOMAIN_LOWER"
create_directory "$BASE_DIR/application/src/test/kotlin/com/$ORG_NAME_LOWER/application/$NEW_DOMAIN_LOWER/command/handler"
create_directory "$BASE_DIR/application/src/test/resources"
create_empty_file "$BASE_DIR/application/build.gradle.kts"
create_empty_file "$BASE_DIR/application/src/test/resources/application.yml"

# apps 모듈
create_directory "$BASE_DIR/apps/src/main/kotlin/com/$ORG_NAME_LOWER/apps/$NEW_DOMAIN_LOWER"
create_directory "$BASE_DIR/apps/src/main/kotlin/com/$ORG_NAME_LOWER/apps/$NEW_DOMAIN_LOWER/config"
create_directory "$BASE_DIR/apps/src/main/resources"
create_empty_file "$BASE_DIR/apps/build.gradle.kts"
create_empty_file "$BASE_DIR/apps/src/main/kotlin/com/$ORG_NAME_LOWER/apps/$NEW_DOMAIN_LOWER/${NEW_DOMAIN_CAP}Application.kt"
create_empty_file "$BASE_DIR/apps/src/main/kotlin/com/$ORG_NAME_LOWER/apps/$NEW_DOMAIN_LOWER/config/SwaggerConfig.kt"
create_empty_file "$BASE_DIR/apps/src/main/resources/application.yml"
create_empty_file "$BASE_DIR/apps/src/main/resources/application-dev.yml"
create_empty_file "$BASE_DIR/apps/src/main/resources/application-prod.yml"
create_empty_file "$BASE_DIR/apps/src/main/resources/application-test.yml"

# domain 모듈
create_directory "$BASE_DIR/domain/src/main/kotlin/com/$ORG_NAME_LOWER/domain/$NEW_DOMAIN_LOWER"
create_directory "$BASE_DIR/domain/src/main/kotlin/com/$ORG_NAME_LOWER/domain/$NEW_DOMAIN_LOWER/aggregate"
create_directory "$BASE_DIR/domain/src/main/kotlin/com/$ORG_NAME_LOWER/domain/$NEW_DOMAIN_LOWER/entity"
create_directory "$BASE_DIR/domain/src/main/kotlin/com/$ORG_NAME_LOWER/domain/$NEW_DOMAIN_LOWER/exception"
create_directory "$BASE_DIR/domain/src/main/kotlin/com/$ORG_NAME_LOWER/domain/$NEW_DOMAIN_LOWER/repository"
create_directory "$BASE_DIR/domain/src/main/kotlin/com/$ORG_NAME_LOWER/domain/$NEW_DOMAIN_LOWER/vo"
create_directory "$BASE_DIR/domain/src/test/kotlin/com/$ORG_NAME_LOWER/domain/$NEW_DOMAIN_LOWER"
create_directory "$BASE_DIR/domain/src/test/kotlin/com/$ORG_NAME_LOWER/domain/$NEW_DOMAIN_LOWER/aggregate"
create_directory "$BASE_DIR/domain/src/test/kotlin/com/$ORG_NAME_LOWER/domain/$NEW_DOMAIN_LOWER/vo"
create_empty_file "$BASE_DIR/domain/build.gradle.kts"
create_empty_file "$BASE_DIR/domain/src/main/kotlin/com/$ORG_NAME_LOWER/domain/$NEW_DOMAIN_LOWER/aggregate/${NEW_DOMAIN_CAP}.kt"
create_empty_file "$BASE_DIR/domain/src/main/kotlin/com/$ORG_NAME_LOWER/domain/$NEW_DOMAIN_LOWER/repository/${NEW_DOMAIN_CAP}Repository.kt"
create_empty_file "$BASE_DIR/domain/src/main/kotlin/com/$ORG_NAME_LOWER/domain/$NEW_DOMAIN_LOWER/exception/${NEW_DOMAIN_CAP}DomainException.kt"

# infrastructure 모듈
create_directory "$BASE_DIR/infrastructure/src/main/kotlin/com/$ORG_NAME_LOWER/infrastructure/$NEW_DOMAIN_LOWER"
create_directory "$BASE_DIR/infrastructure/src/main/kotlin/com/$ORG_NAME_LOWER/infrastructure/$NEW_DOMAIN_LOWER/entity"
create_directory "$BASE_DIR/infrastructure/src/main/kotlin/com/$ORG_NAME_LOWER/infrastructure/$NEW_DOMAIN_LOWER/entity/extensions"
create_directory "$BASE_DIR/infrastructure/src/main/kotlin/com/$ORG_NAME_LOWER/infrastructure/$NEW_DOMAIN_LOWER/repository"
create_empty_file "$BASE_DIR/infrastructure/build.gradle.kts"
create_empty_file "$BASE_DIR/infrastructure/src/main/kotlin/com/$ORG_NAME_LOWER/infrastructure/$NEW_DOMAIN_LOWER/entity/${NEW_DOMAIN_CAP}Entity.kt"
create_empty_file "$BASE_DIR/infrastructure/src/main/kotlin/com/$ORG_NAME_LOWER/infrastructure/$NEW_DOMAIN_LOWER/repository/${NEW_DOMAIN_CAP}RepositoryImpl.kt"

# presentation 모듈
create_directory "$BASE_DIR/presentation/src/main/kotlin/com/$ORG_NAME_LOWER/presentation/$NEW_DOMAIN_LOWER"
create_directory "$BASE_DIR/presentation/src/main/kotlin/com/$ORG_NAME_LOWER/presentation/$NEW_DOMAIN_LOWER/v1"
create_directory "$BASE_DIR/presentation/src/main/kotlin/com/$ORG_NAME_LOWER/presentation/$NEW_DOMAIN_LOWER/v1/command"
create_directory "$BASE_DIR/presentation/src/main/kotlin/com/$ORG_NAME_LOWER/presentation/$NEW_DOMAIN_LOWER/v1/command/dto"
create_directory "$BASE_DIR/presentation/src/main/kotlin/com/$ORG_NAME_LOWER/presentation/$NEW_DOMAIN_LOWER/v1/command/dto/request"
create_directory "$BASE_DIR/presentation/src/main/kotlin/com/$ORG_NAME_LOWER/presentation/$NEW_DOMAIN_LOWER/v1/dto"
create_directory "$BASE_DIR/presentation/src/main/kotlin/com/$ORG_NAME_LOWER/presentation/$NEW_DOMAIN_LOWER/v1/dto/request"
create_directory "$BASE_DIR/presentation/src/main/kotlin/com/$ORG_NAME_LOWER/presentation/$NEW_DOMAIN_LOWER/v1/extensions"
create_directory "$BASE_DIR/presentation/src/main/kotlin/com/$ORG_NAME_LOWER/presentation/$NEW_DOMAIN_LOWER/v1/query"
create_directory "$BASE_DIR/presentation/src/main/kotlin/com/$ORG_NAME_LOWER/presentation/$NEW_DOMAIN_LOWER/v1/query/dto"
create_directory "$BASE_DIR/presentation/src/main/kotlin/com/$ORG_NAME_LOWER/presentation/$NEW_DOMAIN_LOWER/v1/query/dto/response"
create_directory "$BASE_DIR/presentation/src/main/resources"
create_empty_file "$BASE_DIR/presentation/build.gradle.kts"
create_empty_file "$BASE_DIR/presentation/src/main/kotlin/com/$ORG_NAME_LOWER/presentation/$NEW_DOMAIN_LOWER/v1/command/${NEW_DOMAIN_CAP}CommandControllerV1.kt"
create_empty_file "$BASE_DIR/presentation/src/main/kotlin/com/$ORG_NAME_LOWER/presentation/$NEW_DOMAIN_LOWER/v1/query/${NEW_DOMAIN_CAP}QueryControllerV1.kt"
create_empty_file "$BASE_DIR/presentation/src/main/resources/application.yml"

echo "Domain structure for $NEW_DOMAIN_LOWER created successfully!"
