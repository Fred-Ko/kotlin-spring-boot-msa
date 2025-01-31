1. 사용자 가입 및 프로필 관리
시나리오:

사용자 회원가입 → 프로필 정보(주소, 알림 설정) 업데이트 → 탈퇴
필요 Command (User 도메인):

RegisterUserCommand: 이메일, 비밀번호, 이름으로 회원가입

UpdateUserProfileCommand: 주소, 알림 설정 변경

DeactivateUserCommand: 계정 비활성화

2. 식당 등록 및 메뉴 운영
시나리오:

사장님 식당 정보 등록 → 메뉴 추가/수정 → 영업 상태 변경
필요 Command (Restaurant 도메인):

RegisterRestaurantCommand: 이름, 주소, 영업시간 등록

UpdateMenuCommand: 메뉴 항목 가격/설명 수정

ToggleRestaurantStatusCommand: 영업 중/휴업 상태 전환

3. 주문 생성 ~ 결제 ~ 배달
시나리오:

장바구니 주문 → 결제 → 배달 시작 → 배달 완료
필요 Command (Order 도메인):

PlaceOrderCommand: 메뉴, 수량, 배달 주소 포함 주문 생성

ProcessPaymentCommand: 결제 수단(PG사 연동) 선택

CancelOrderCommand: 주문 취소 (결제 실패 시)

필요 Command (Delivery 도메인):

AssignDeliveryCommand: 라이더에게 배달 할당

UpdateDeliveryStatusCommand: 배달 진행 상태 갱신 (픽업 중, 배달 중, 완료)

4. 재고 관리 및 보상 트랜잭션
시나리오:

주문 시 재고 차감 → 결제 실패 시 재고 복구
필요 Command (Restaurant/Order 도메인 협업):

ReserveInventoryCommand: 주문된 메뉴 재고 임시 확보

CompensateInventoryCommand: 재고 원복 (Saga 보상)

---
위에 시나리오에 대한 application 서비스 스켈레톤 코드를 작성하시오.
폴더는 이미 다 생성되어있으니 절대 생성하지 않는다.
tree 명령어로 구조를 파악하고 시작하라.