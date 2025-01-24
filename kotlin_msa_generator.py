#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys
import argparse

########################################################################
# 1) 프로젝트 정보만 저장하는 ProjectContext
########################################################################
class ProjectContext:
    """프로젝트에서 공통으로 사용되는 Group ID, 프로젝트명, 버전 정보를 보관"""
    def __init__(self, group_id: str, project_name: str, versions: dict, project_version: str):
        """
        :param group_id: 예) "com.mycompany"
        :param project_name: 예) "myProject"
        :param versions: {
            'kotlin_version': '1.8.0',
            'spring_boot_version': '2.7.3',
            'java_version': '17'
        }
        :param project_version: 프로젝트의 버전 예) "1.0.0-SNAPSHOT"
        """
        self.group_id = group_id
        self.project_name = project_name
        self.versions = versions
        self.project_version = project_version

########################################################################
# 2) 디렉토리 생성과 Path 관련 책임을 담당하는 DirectoryService
########################################################################
class DirectoryService:
    """디렉토리 생성 및 path 변환을 담당(SRP)"""
    def __init__(self, context: ProjectContext):
        self.context = context

    def make_dir(self, path: str):
        """디렉토리 생성 유틸. 이미 존재하면 무시."""
        os.makedirs(path, exist_ok=True)

    def group_id_to_path_list(self):
        """
        com.mycompany => ["com", "mycompany"] 와 같이 변환
        """
        return self.context.group_id.split(".")

########################################################################
# 3) Gradle 설정 파일 생성/수정 책임을 담당하는 GradleFileService
########################################################################
class GradleFileService:
    """Gradle 설정 파일 생성/갱신 책임(SRP)"""
    def __init__(self, context: ProjectContext, directory_service: DirectoryService):
        self.context = context
        self.ds = directory_service
        self.root_dir = context.project_name  # 루트 디렉토리

    def create_settings_gradle(self):
        """루트 settings.gradle.kts 생성 (없으면 생성)"""
        settings_gradle_path = os.path.join(self.root_dir, "settings.gradle.kts")
        if os.path.exists(settings_gradle_path):
            print(f"[INFO] {settings_gradle_path} 이미 존재합니다.")
            return

        content = f"""\
rootProject.name = "{self.context.project_name}"

include("shared:shared-common")
include("shared:shared-events")
include("shared:shared-utils")

// 도메인, 라이브러리는 추가 시점에 append
"""
        with open(settings_gradle_path, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"[INFO] settings.gradle.kts 생성 완료: {settings_gradle_path}")

    def create_root_build_gradle(self):
        """루트 build.gradle.kts 생성 (없으면 생성)"""
        root_build_gradle_path = os.path.join(self.root_dir, "build.gradle.kts")
        if os.path.exists(root_build_gradle_path):
            print(f"[INFO] {root_build_gradle_path} 이미 존재합니다.")
            return

        content = f"""\
plugins {{
    // 루트에서만 적용하는 plugin
    id("org.jetbrains.kotlin.jvm") version "{self.context.versions['kotlin_version']}" apply false
    id("org.springframework.boot") version "{self.context.versions['spring_boot_version']}" apply false
    id("io.spring.dependency-management") version "1.0.15.RELEASE" apply false
}}

allprojects {{
    group = "{self.context.group_id}"
    version = "{self.context.project_version}" 

    repositories {{
        mavenCentral()
    }}
}}

subprojects {{
    apply(plugin = "org.jetbrains.kotlin.jvm")

    java.sourceCompatibility = JavaVersion.VERSION_{self.context.versions['java_version']}

    dependencies {{
        implementation(kotlin("stdlib"))
    }}
}}
"""
        with open(root_build_gradle_path, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"[INFO] 루트 build.gradle.kts 생성 완료: {root_build_gradle_path}")

    def append_to_settings_gradle(self, text: str):
        """
        settings.gradle.kts에 모듈 include를 append
        이미 존재하면 생략
        """
        settings_gradle_path = os.path.join(self.root_dir, "settings.gradle.kts")
        if not os.path.exists(settings_gradle_path):
            return

        with open(settings_gradle_path, "r", encoding="utf-8") as f:
            lines = f.read().splitlines()

        if text not in lines:
            with open(settings_gradle_path, "a", encoding="utf-8") as f:
                f.write("\n" + text + "\n")
            print(f"[INFO] settings.gradle.kts에 다음 내용을 추가했습니다: {text}")
        else:
            print(f"[INFO] settings.gradle.kts에 이미 존재하는 내용: {text}")

    def create_shared_build_gradle(self, module_name: str):
        """shared 모듈용 build.gradle.kts 생성"""
        build_file_path = os.path.join(self.root_dir, "shared", module_name, "build.gradle.kts")
        if os.path.exists(build_file_path):
            return
        content = """\
plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    // 필요시 의존성 추가
}
"""
        with open(build_file_path, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"[INFO] shared 모듈 빌드파일 생성: {module_name}")

    def create_domain_build_gradle(self, domain_name: str, layer_name: str):
        """도메인 내 각 layer용 build.gradle.kts 생성"""
        base_domain_path = os.path.join(self.root_dir, "domains", domain_name, layer_name)
        build_file_path = os.path.join(base_domain_path, "build.gradle.kts")
        if os.path.exists(build_file_path):
            return

        content = f"""\
plugins {{
    id("org.jetbrains.kotlin.jvm")
    // Spring Boot를 사용하는 계층이라면 추가
    // id("org.springframework.boot")
}}

dependencies {{
    implementation(project(":shared:shared-common"))
    implementation(project(":shared:shared-events"))
    implementation(project(":shared:shared-utils"))
}}
"""
        with open(build_file_path, "w", encoding="utf-8") as f:
            f.write(content)

        # settings.gradle.kts에 include 추가
        self.append_to_settings_gradle(f'include("domains:{domain_name}:{layer_name}")')
        print(f"[INFO] 도메인 {domain_name}의 {layer_name} 모듈 빌드파일 생성 완료.")

    def create_library_build_gradle(self, library_name: str):
        """라이브러리용 build.gradle.kts 생성"""
        base_lib_path = os.path.join(self.root_dir, "libraries", library_name)
        build_file_path = os.path.join(base_lib_path, "build.gradle.kts")
        if os.path.exists(build_file_path):
            return

        content = f"""\
plugins {{
    id("org.jetbrains.kotlin.jvm")
}}

dependencies {{
    implementation(project(":shared:shared-common"))
    implementation(project(":shared:shared-events"))
    implementation(project(":shared:shared-utils"))
}}
"""
        with open(build_file_path, "w", encoding="utf-8") as f:
            f.write(content)

        # settings.gradle.kts에 include 추가
        self.append_to_settings_gradle(f'include("libraries:{library_name}")')
        print(f"[INFO] 라이브러리 {library_name} 모듈 빌드파일 생성 완료.")

########################################################################
# 4) 공유 모듈 생성만 담당하는 SharedModuleCreator
########################################################################
class SharedModuleCreator:
    """shared-* 모듈 구조 및 파일 생성을 담당(SRP)"""
    def __init__(self, context: ProjectContext, directory_service: DirectoryService, gradle_service: GradleFileService):
        self.context = context
        self.ds = directory_service
        self.gs = gradle_service

    def create_shared_modules(self):
        """shared-common, shared-events, shared-utils 디렉토리와 빌드파일 생성"""
        shared_path = os.path.join(self.context.project_name, "shared")
        self.ds.make_dir(shared_path)

        # shared-common
        self._create_one_shared_module("shared-common", [
            os.path.join("src", "main", "kotlin",
                         *self.ds.group_id_to_path_list(), "common", "exception"),
            os.path.join("src", "main", "kotlin",
                         *self.ds.group_id_to_path_list(), "common", "util")
        ])

        # shared-events
        self._create_one_shared_module("shared-events", [
            os.path.join("src", "main", "kotlin",
                         *self.ds.group_id_to_path_list(), "events")
        ])

        # shared-utils
        self._create_one_shared_module("shared-utils", [
            os.path.join("src", "main", "kotlin",
                         *self.ds.group_id_to_path_list(), "utils", "extensions"),
            os.path.join("src", "main", "kotlin",
                         *self.ds.group_id_to_path_list(), "utils", "coroutine")
        ])

    def _create_one_shared_module(self, module_name: str, sub_dirs: list):
        base_path = os.path.join(self.context.project_name, "shared", module_name)
        self.ds.make_dir(base_path)
        # build.gradle.kts 생성
        self.gs.create_shared_build_gradle(module_name)

        # 내부 디렉토리 생성
        for sdir in sub_dirs:
            self.ds.make_dir(os.path.join(base_path, sdir))

########################################################################
# 5) 도메인 모듈(계층 구조) 생성 책임: DomainModuleCreator
########################################################################
class DomainModuleCreator:
    """도메인별 presentation/application/domain/infrastructure 구조 생성을 담당(SRP)"""
    def __init__(self, context: ProjectContext, directory_service: DirectoryService, gradle_service: GradleFileService):
        self.context = context
        self.ds = directory_service
        self.gs = gradle_service

    def add_domain(self, domain_name: str):
        """새로운 도메인 모듈을 추가"""
        print(f"[INFO] 새 도메인 모듈을 추가합니다: {domain_name}")

        base_domain_path = os.path.join(self.context.project_name, "domains", domain_name)

        # 1) presentation 모듈
        self._create_presentation_module(base_domain_path, domain_name)

        # 2) application 모듈
        self._create_application_module(base_domain_path, domain_name)

        # 3) domain 모듈
        self._create_domain_module(base_domain_path, domain_name)

        # 4) infrastructure 모듈
        self._create_infrastructure_module(base_domain_path, domain_name)

        print(f"[INFO] 도메인 {domain_name} 모듈 생성 완료.")

    def _create_presentation_module(self, base_path, domain_name):
        presentation_path = os.path.join(base_path, "presentation")
        self.ds.make_dir(presentation_path)
        self.gs.create_domain_build_gradle(domain_name, "presentation")

        kotlin_presentation_path = os.path.join(
            presentation_path, "src", "main", "kotlin",
            *self.ds.group_id_to_path_list(), domain_name, "presentation"
        )
        self.ds.make_dir(kotlin_presentation_path)

        # 예시 하위 디렉토리
        self.ds.make_dir(os.path.join(kotlin_presentation_path, "controller", "command"))
        self.ds.make_dir(os.path.join(kotlin_presentation_path, "controller", "query"))
        self.ds.make_dir(os.path.join(kotlin_presentation_path, "dto", "request"))
        self.ds.make_dir(os.path.join(kotlin_presentation_path, "dto", "response"))
        self.ds.make_dir(os.path.join(kotlin_presentation_path, "config"))

    def _create_application_module(self, base_path, domain_name):
        application_path = os.path.join(base_path, "application")
        self.ds.make_dir(application_path)
        self.gs.create_domain_build_gradle(domain_name, "application")

        kotlin_application_path = os.path.join(
            application_path, "src", "main", "kotlin",
            *self.ds.group_id_to_path_list(), domain_name, "application"
        )
        self.ds.make_dir(kotlin_application_path)

        # 예시 하위 디렉토리
        self.ds.make_dir(os.path.join(kotlin_application_path, "command", "handler"))
        self.ds.make_dir(os.path.join(kotlin_application_path, "command", "service"))
        self.ds.make_dir(os.path.join(kotlin_application_path, "command", "dto"))
        self.ds.make_dir(os.path.join(kotlin_application_path, "query", "handler"))
        self.ds.make_dir(os.path.join(kotlin_application_path, "query", "service"))
        self.ds.make_dir(os.path.join(kotlin_application_path, "query", "dto"))
        self.ds.make_dir(os.path.join(kotlin_application_path, "port"))

    def _create_domain_module(self, base_path, domain_name):
        domain_layer_path = os.path.join(base_path, "domain")
        self.ds.make_dir(domain_layer_path)
        self.gs.create_domain_build_gradle(domain_name, "domain")

        kotlin_domain_path = os.path.join(
            domain_layer_path, "src", "main", "kotlin",
            *self.ds.group_id_to_path_list(), domain_name, "domain"
        )
        self.ds.make_dir(kotlin_domain_path)

        # 예시 하위 디렉토리
        self.ds.make_dir(os.path.join(kotlin_domain_path, "model"))
        self.ds.make_dir(os.path.join(kotlin_domain_path, "vo"))
        self.ds.make_dir(os.path.join(kotlin_domain_path, "event"))
        self.ds.make_dir(os.path.join(kotlin_domain_path, "exception"))

    def _create_infrastructure_module(self, base_path, domain_name):
        infrastructure_path = os.path.join(base_path, "infrastructure")
        self.ds.make_dir(infrastructure_path)
        self.gs.create_domain_build_gradle(domain_name, "infrastructure")

        kotlin_infra_path = os.path.join(
            infrastructure_path, "src", "main", "kotlin",
            *self.ds.group_id_to_path_list(), domain_name, "infrastructure"
        )
        self.ds.make_dir(kotlin_infra_path)

        # 예시 하위 디렉토리
        adapter_path = os.path.join(kotlin_infra_path, "adapter")
        self.ds.make_dir(adapter_path)
        self.ds.make_dir(os.path.join(adapter_path, "persistence", "entity"))
        self.ds.make_dir(os.path.join(adapter_path, "messaging"))

        config_path = os.path.join(kotlin_infra_path, "config")
        self.ds.make_dir(config_path)

        repository_path = os.path.join(kotlin_infra_path, "repository")
        self.ds.make_dir(repository_path)

########################################################################
# 6) 라이브러리 모듈 생성 책임: LibraryModuleCreator
########################################################################
class LibraryModuleCreator:
    """라이브러리 모듈 생성 담당(SRP)"""
    def __init__(self, context: ProjectContext, directory_service: DirectoryService, gradle_service: GradleFileService):
        self.context = context
        self.ds = directory_service
        self.gs = gradle_service

    def add_library(self, library_name: str):
        """라이브러리 모듈 추가"""
        print(f"[INFO] 새로운 라이브러리 모듈을 추가합니다: {library_name}")

        base_library_path = os.path.join(self.context.project_name, "libraries", library_name)
        self.ds.make_dir(base_library_path)

        # build.gradle.kts 생성
        self.gs.create_library_build_gradle(library_name)

        # src/main/kotlin/{group_id}/{libraryName} 디렉토리 구성
        kotlin_lib_path = os.path.join(
            base_library_path, "src", "main", "kotlin",
            *self.ds.group_id_to_path_list(), library_name
        )
        self.ds.make_dir(kotlin_lib_path)

        # 예시 디렉토리 구성(필요시 수정)
        self.ds.make_dir(os.path.join(kotlin_lib_path, "config"))
        self.ds.make_dir(os.path.join(kotlin_lib_path, "domain"))
        self.ds.make_dir(os.path.join(kotlin_lib_path, "scheduler"))
        self.ds.make_dir(os.path.join(kotlin_lib_path, "repository"))

        print(f"[INFO] 라이브러리 {library_name} 모듈 생성 완료.")

########################################################################
# 7) 전체 과정을 조율(초기화, 도메인 추가, 라이브러리 추가)하는 ProjectInitializer
########################################################################
class ProjectInitializer:
    """프로젝트 초기화 및 모듈 생성을 통합적으로 조율하는 클래스(SRP: '조율자')"""
    def __init__(self, context: ProjectContext):
        self.context = context
        # 하위 서비스/생성기들을 의존성 주입
        self.directory_service = DirectoryService(context)
        self.gradle_service = GradleFileService(context, self.directory_service)

        self.shared_creator = SharedModuleCreator(context, self.directory_service, self.gradle_service)
        self.domain_creator = DomainModuleCreator(context, self.directory_service, self.gradle_service)
        self.library_creator = LibraryModuleCreator(context, self.directory_service, self.gradle_service)

    def init_project(self):
        """
        프로젝트를 초기화: 루트 디렉토리 생성, gradle/settings 파일 생성, shared 모듈 구조 생성 등
        """
        print(f"[INFO] 프로젝트를 초기화합니다. 프로젝트명: {self.context.project_name}, groupId: {self.context.group_id}")

        # 루트 디렉토리 생성
        self.directory_service.make_dir(self.context.project_name)

        # Gradle 파일들 생성
        self.gradle_service.create_settings_gradle()
        self.gradle_service.create_root_build_gradle()

        # shared-* 모듈들 생성
        self.shared_creator.create_shared_modules()

        print("[INFO] 프로젝트 초기화가 완료되었습니다.")

    def add_domain(self, domain_name: str):
        """새 도메인(모듈)을 추가"""
        self.domain_creator.add_domain(domain_name)

    def add_library(self, library_name: str):
        """새 라이브러리(모듈)를 추가"""
        self.library_creator.add_library(library_name)


########################################################################
# 8) 메인 함수: argparse로 명령어 파싱 후 ProjectInitializer를 통해 실행
########################################################################
def main():
    parser = argparse.ArgumentParser(description="프로젝트 초기화 스크립트")
    parser.add_argument("command", choices=["init", "domain", "library"],
                        help="실행할 명령을 지정하세요. (init/domain/library)")
    parser.add_argument("name", nargs="?", default=None,
                        help="도메인명 혹은 라이브러리명을 지정하세요.")

    args = parser.parse_args()

    # 버전, groupId, 프로젝트명 등은 여기서 일괄 수정 가능
    versions = {
        "kotlin_version": "2.1.0",
        "spring_boot_version": "3.4.1",
        "java_version": "21"
    }
    group_id = "com.fredko.restaurant"
    project_name = "restaurant-msa"
    project_version = "1.0.0-SNAPSHOT"  # 프로젝트 버전 추가

    # ProjectContext 생성
    context = ProjectContext(group_id, project_name, versions, project_version)

    # ProjectInitializer (조율자) 생성
    initializer = ProjectInitializer(context)

    if args.command == "init":
        initializer.init_project()
    elif args.command == "domain":
        if not args.name:
            print("[ERROR] 도메인명을 지정해야 합니다. 예) python project_initializer.py domain user")
            sys.exit(1)
        initializer.add_domain(args.name)
    elif args.command == "library":
        if not args.name:
            print("[ERROR] 라이브러리명을 지정해야 합니다. 예) python project_initializer.py library outbox")
            sys.exit(1)
        initializer.add_library(args.name)

if __name__ == "__main__":
    main()
