import os
import yaml

base_package = "com/ddd"

def create_directory(path):
    path = path.replace("{base_package}", base_package)
    """디렉토리 생성 (이미 존재할 경우 무시)"""
    os.makedirs(path, exist_ok=True)

def create_file(file_path):
    file_path = file_path.replace("{base_package}", base_package)
    """빈 파일 생성"""
    with open(file_path, 'w') as f:
        pass

def process_node(current_path, node):
    """
    YAML 노드 처리 (재귀 함수)
    - current_path: 현재 처리 중인 경로
    - node: 처리할 YAML 노드 (dict 또는 list)
    """
    if isinstance(node, dict):
        for key, value in node.items():
            # 값이 True인 경우 파일로 생성
            if value is True:
                file_path = os.path.join(current_path, key)
                create_file(file_path)
                continue
                
            # 키에 '/'가 포함된 경우 전체 경로로 처리
            if '/' in key:
                full_path = os.path.join(current_path, *key.split('/'))
                create_directory(full_path)
                process_node(full_path, value)
            else:
                # 일반 디렉토리 생성
                new_path = os.path.join(current_path, key)
                create_directory(new_path)
                process_node(new_path, value)
    elif isinstance(node, list):
        # 리스트 항목은 모두 파일로 생성
        for item in node:
            if isinstance(item, dict):
                # 사전이 포함된 경우 키를 디렉토리로 생성
                for sub_key, sub_value in item.items():
                    sub_path = os.path.join(current_path, sub_key)
                    create_directory(sub_path)
                    process_node(sub_path, sub_value)
            else:
                # 파일 생성
                file_path = os.path.join(current_path, item)
                create_file(file_path)

def generate_structure_from_yaml(yaml_path, output_dir='.'):
    """YAML 파일을 읽어 구조 생성"""
    with open(yaml_path, 'r') as f:
        yaml_data = yaml.safe_load(f)
    
    for section, content in yaml_data.items():
        section_path = os.path.join(output_dir, section)
        create_directory(section_path)
        process_node(section_path, content)

if __name__ == "__main__":
    # 사용 예시 (실행 전 yaml_path 설정 필요)
    yaml_path = "spec/folder_file2.yaml"
    generate_structure_from_yaml(yaml_path, "restaurant")
    print("폴더 구조 생성 완료!")
