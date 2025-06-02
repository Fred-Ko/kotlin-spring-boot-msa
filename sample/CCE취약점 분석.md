# CCE 취약점 분석

---

## Server / Linux

### 패스워드 복잡도 체크 정책 미적용

#### pwquality

- 실제 패스워드가 복잡한 규칙으로 생성된것과 상관없음.
- passwd 설정시 정책에 만족하는지 확인하는 정책 옵션.
- pwquality 설정 해야함.
  - enforce_for_root도 필수적으로 적용을 해서 루트 사용자에게도 정책이 적용되도록 해야함.
- nks worker node는 스케일링에 따라 생성되고 삭제되는데 이거는 어떻게 대응해야 되나요?
  - init script나 문의를 통해서 시작시 스크립트가 동작할 수 있도록 해서 대응해야함.

추가적인 tip

- 개보위법에서는 password에 대한 정책이 대부분 삭제됨.
- 하지만 CSAP에는 남아있는 경우가 있음.
- 즉 개보위법에서 없고 CSAP에서 다루지 않는것들에 대한 정책은 아헤플 내부 정책을 따름.
예) 패스워드 교체 주기 정책
- 개보위법에서는 삭제됨.
- CSAP에서 다루지 않음.
- 아헤플 내부 정책에 6개월 마다 교체를 한다라고 정책을 정했다면 내부정책에 따라 6개월 주기로 수정해야함.
- But 아헤플 내부정책에서도 삭제된다면 교체는 안해도됨.

#### 계정잠금 임계값

사용자 인증 실패를 추적하고, 일정 횟수 이상의 연속된 실패 시 계정을 잠금 처리하는 기능.
Ubuntu 20.04 (pam_tally)
pam_tally가 deprecated되었고 대신 pam_faillock 사용. 따라서 이행점검때 따로 조치를 하지 않는다면 취약 -> 양호판정으로 될 예정 (왜냐하면 nks xen 클러스터에서 우분투 최신버전 제공이 20.04 이미지이기 때문.)

하지만, 22.04 이미지에서는 기본 모듈이 pam_failock 이고 pam_failock은 명령어로 잠금처리에 대한 설정을 해줘야함.
또한 20.04 에서도 pam_failock으로 설정이 가능하지만 아헤플 판단에 따라서 결정하면 됨.

#### 디렉토리 및 파일 권한

group이 없는 디렉토리나 파일에 다한 부분을 기본 루트계정인 ncloud나 ncloud가 속한 그룹으로 소유권을 지정해주면 됨.

#### NGS

##### usernetctl에 대한 조치가 필요함. 무슨말인지 이해를 못해서 gpt에 대한 결과를 아래 첨부함

```markdown
### usernetctl과 SUID/SGID 권한 제거 권고 요약

1. **usernetctl이란?**
   - 리눅스에서 네트워크 인터페이스(예: eth0, ppp0)를 활성화(up), 비활성화(down), 상태 확인(report)하는 도구.
   - 주로 Red Hat 계열 시스템(CentOS, Fedora)에서 사용.
   - 일반 사용자가 루트 권한 없이 네트워크를 제어할 수 있도록 SUID가 설정된 setuid 루트 바이너리(`/usr/sbin/usernetctl`).

2. **SUID/SGID란?**
   - **SUID**: 파일 실행 시 소유자(보통 root) 권한으로 실행.
   - **SGID**: 파일 실행 시 소유 그룹 권한으로 실행.
   - usernetctl은 SUID(예: 권한 4755)로 설정되어 일반 사용자가 네트워크 제어 가능.

3. **취약점 분석에서 SUID/SGID 제거 권고 이유**
   - SUID/SGID는 권한 상승(privilege escalation) 취약점의 잠재적 원인.
   - usernetctl이 시스템에서 불필요하거나, 악용 가능성이 감지됨.
   - 최소 권한 원칙에 따라 불필요한 특수 권한 제거 권장.

4. **권고의 의미**
   - usernetctl의 SUID/SGID 비트를 제거해 일반 권한(예: 755)으로 변경.
   - 제거 시 일반 사용자는 `ifup`, `ifdown`으로 네트워크 제어 불가, root 또는 sudo 필요.

5. **대응 방법**
   - **사용 여부 확인**: usernetctl이 시스템에서 필요한지 점검.
   - **권한 확인**: `ls -l /usr/sbin/usernetctl`로 SUID(`-rwsr-xr-x`) 또는 SGID 확인.
   - **SUID/SGID 제거**:
     ```bash
     sudo chmod u-s /usr/sbin/usernetctl  # SUID 제거
     sudo chmod g-s /usr/sbin/usernetctl  # SGID 제거
     sudo chmod 755 /usr/sbin/usernetctl  # 일반 권한 설정
     ```
   - **영향 테스트**: 제거 후 네트워크 제어 동작 확인, 로그 점검(`/var/log/messages`, `/var/log/secure`).
   - **대체 도구**: NetworkManager(`nmcli`) 사용 고려.

6. **주의사항**
   - SUID/SGID 제거는 네트워크 관리 방식에 영향. 시스템 요구사항 확인 필수.
   - SELinux 환경에서는 컨텍스트 확인(`ls -Z /usr/sbin/usernetctl`) 및 수정(`restorecon`).
   - 패키지 업데이트로 취약점 패치 여부 확인(`yum update` 또는 `dnf update`).

### 추가
- 시스템 환경(예: OS 버전, usernetctl 사용 여부)이나 취약점 세부 정보(CVE 등)를 알려주시면 더 구체적인 도움 가능.
```

##### word writable

"world writable"는 리눅스/유닉스 시스템에서 파일이나 디렉토리의 권한 설정과 관련된 용어로, **모든 사용자(others)**가 해당 파일이나 디렉토리에 쓰기 권한을 가지고 있음을 의미

*/didim365/**/* 관련된 디렉토리가 취약점으로 분석되었음. 아마도 로그 파일인것 같아서 제거조치 필요.

##### cron

- root 사용자만 사용할 수 있도록 해야함.
- cron allow,deny 파일이 없으면 생성하고 빈파일로 유지하면 자동적으로 root 사용자만 cron을 사용하게끔 되어있음.

#### rpc 서비스

nfs를 사용안하는 거라면 서비스 제거 조치
바인드 패키지를 제거하면된다고 함.

#### nks 우분투 이미지

xen에서 os 버전 20.04 이미지가 최신버전이나 eol 되었음.
또한 20.04에 사용된 linux kernel 버전도 문제가 있음.
우리가 할 조치는 없을것으로 예상되나 ncloud에 문의해서 답변을 받아서 증적자료로 남겨야함.

#### logging

전반적으로 미비

- 법적으로 꼭 지켜야하는 부분
  - 개인정보처리시스템의 접근 권한 변경 기록: 최소 3년 보관
- 나머지는 아헤플 정책으로 정하기 나름일꺼 같다는 의견

## POD

### 디렉토리 및 파일

소유자나 그룹이 없는 디렉토리나 파일이 검출되었습니다.
소유자나 그룹이 없는걸 전부다 제거하세요.

### wall

wall 명령어는 시스템에 로그인한 모든 사용자에게 메시지를 보내는 프로그램.
SUID/SGID가 필요한 경우가 아니라 불필요하게 설정되어 있거나, 취약한 프로그램에 설정된 경우 시스템의 보안에 취약한 설정 상태.
