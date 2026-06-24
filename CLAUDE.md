# D1 백엔드 (D1-WebServer)

D1 게임의 영속화/인증 백엔드. 언리얼 데디서버와 HTTP/JSON으로 통신.

- **스택:** Spring Boot 4.1.0, Java 21, Spring Data JPA, Spring Security, MySQL 8.0
- **빌드:** Gradle (Kotlin DSL, `build.gradle.kts`)
- **인증:** JWT (jjwt 0.12.6)
- **인프라:** 로컬은 MySQL만 Docker(`docker-compose.yml`), Spring Boot는 IntelliJ `bootRun`. 배포 시 둘 다 Docker.
- **게임 본체 규칙은** `../D1-Unreal/D1/CLAUDE.md` 참조.

---

## 1. AI 작업 가이드 (필수 규칙)

- 백엔드 작업 시작 전 이 파일을 먼저 읽고, 관련 엔티티/스키마 구조 파악.
- **DB 스키마는 `init/01_schema.sql`이 단일 진실 소스(SSOT).** `ddl-auto: validate`이므로 Hibernate가 테이블을 만들지 않음 — 컬럼 추가/변경은 **반드시 SQL 먼저 수정** 후 엔티티 반영.
- 스키마 변경 후에는 컨테이너 재생성 필요: `docker-compose down -v && docker-compose up -d` (※ `-v`는 데이터 전부 삭제 — 운영 배포 시엔 마이그레이션 도구로 대체 예정).
- API 추가/변경 시 언리얼 측 HTTP 호출부와 계약(요청/응답 JSON)이 깨지지 않는지 확인.
- 작업 후 이 파일의 「작업 이력」 갱신.

---

## 2. 아키텍처 규칙 (계층 분리)

```
Controller → Service → Repository → Entity
   (dto)      (@Transactional)   (JPA)
```

- **Controller:** HTTP 요청/응답만 담당. 비즈니스 로직 금지. 요청은 DTO로 받고(`@Valid`), 응답도 DTO로 반환. `@RestController`.
- **Service:** 비즈니스 로직 + 트랜잭션 경계. 엔티티 ↔ DTO 변환은 여기서. 외부에는 DTO만 노출.
- **Repository:** `JpaRepository<Entity, ID>` 상속 인터페이스. 쿼리 메서드 명명 규칙 우선, 복잡하면 `@Query`.
- **Entity:** DB 매핑 전용. **컨트롤러/응답에 엔티티 직접 노출 절대 금지** (지연 로딩·순환 참조·필드 과다 노출 방지).

### 패키지 구조
`config` / `controller` / `dto` / `entity` / `exception` / `repository` / `service` / `util`

---

## 3. Entity 규칙

- **외래키(FK) 사용 안 함** (운영 정책). 연관관계는 `@ManyToOne` 등 매핑 대신 **ID 필드(`Long accountId`)로 직접 보유**, 조회는 `findByAccountId` 같은 인덱스 기반 쿼리. `@JoinColumn`/`@OneToMany` 등 관계 매핑 도입 금지.
- **참조 무결성은 애플리케이션(Service 계층)이 책임진다.** DB FK 제약이 없으므로, 부모 삭제 시 자식 레코드 정리·존재하지 않는 부모 ID 참조 방지 등을 Service 로직 + 트랜잭션으로 직접 보장할 것. cascade 저장/삭제 같은 JPA 편의 기능은 쓸 수 없으므로 명시적으로 처리.
- **Setter 금지.** `@Getter`만 사용(`@Setter`/`@Data` 금지). 값 변경은 의미 있는 비즈니스 메서드로(`stats.update(...)`, `slot.updateItem(...)`).
- **기본 생성자는 `protected`/`public` `@NoArgsConstructor`** (JPA 요구), 실제 생성은 필수값을 받는 명시적 생성자로.
- 컬럼명은 `@Column(name = "snake_case")`로 SQL과 정확히 일치시킬 것. 필드는 camelCase.
- **복합키는 `@EmbeddedId` + 내부 `@Embeddable` 클래스** (예: `EquippedItem.EquippedItemId`). ※ 복합키 클래스는 `equals()`/`hashCode()` 오버라이드 권장 (현재 Hibernate 경고 HHH000038 — 추후 정리).
- 타입 매핑 주의: Java `int` ↔ MySQL `INT` (※ `UNSIGNED`/`TINYINT` 쓰면 `validate` 실패. SQL은 `INT`/`BIGINT`로 단순하게).
- 생성/수정 시각은 `@PrePersist`/`@PreUpdate`로 자동 세팅.

## 4. DTO 규칙

- 요청 DTO: `record` 권장, `@NotNull`/`@Email`/`@Size` 등 Bean Validation 어노테이션 부착.
- 응답 DTO: 엔티티에서 필요한 필드만 추려 생성. **비밀번호 해시 등 민감 필드 절대 포함 금지.**
- 변환 메서드는 DTO의 static factory(`from(Entity)`) 또는 Service에 위치.

## 5. 트랜잭션 / 영속성

- Service 메서드에 `@Transactional` 명시. 조회 전용은 `@Transactional(readOnly = true)`.
- **저장 시점:** 게임 특성상 잦은 변경(인벤토리 정렬 등)은 매번 쓰지 말고, 세이브 시점(로그아웃·던전 클리어)에 일괄 저장하는 API로 설계 (MVP는 Redis 없이 DB 직접).
- 변경 감지(dirty checking) 활용 — 조회한 엔티티의 비즈니스 메서드 호출 후 트랜잭션 종료 시 자동 flush. 불필요한 `save()` 남발 금지.
- `open-in-view`는 `false`로 설정(뷰 렌더링 중 쿼리 방지). LAZY 로딩 데이터는 트랜잭션 안에서 다 끌어올 것.

## 6. 보안 / 인증

- 비밀번호는 `BCryptPasswordEncoder`로 해시. 평문 저장·로그 출력 금지.
- JWT secret/만료시간은 `application.yaml`의 `jwt.*` (운영은 환경변수로 주입, 하드코딩 금지).
- **데디서버 ↔ 웹서버 통신은 별도 API Key 인증** (클라이언트 JWT와 분리). `verify-session` 등 서버 전용 엔드포인트는 일반 사용자 토큰으로 접근 불가.
- **Spring Security 풀 스타터 미사용** (의도적). `spring-security-crypto`(BCrypt만) + 직접 만든 JWT 인증으로 처리. 인증은 `config/JwtInterceptor`(HandlerInterceptor)가 `Authorization: Bearer` 헤더 검증 → `accountId`를 request attribute에 저장. 등록은 `config/WebConfig`에서 `/api/**` 적용, `/api/auth/**` 제외. 컨트롤러는 `@RequestAttribute("accountId")`로 사용자 식별.

## 7. 예외 처리

- `@RestControllerAdvice` + `GlobalExceptionHandler`로 일원화. 컨트롤러에서 try-catch 남발 금지.
- 도메인 예외는 커스텀 예외 클래스로(`exception` 패키지), 적절한 HTTP 상태코드로 매핑.
- 응답 에러 포맷 통일(예: `{ "code": ..., "message": ... }`). 스택트레이스·내부 정보 클라이언트 노출 금지.

## 8. 코딩 규칙

- **Naming:** 클래스 PascalCase, 메서드/변수 camelCase, 상수 UPPER_SNAKE.
- **Lombok:** `@Getter`, `@NoArgsConstructor`, `@RequiredArgsConstructor`(생성자 주입) 위주. `@Data`/`@Setter` 금지.
- **의존성 주입:** 필드 주입(`@Autowired`) 금지, **생성자 주입**(`@RequiredArgsConstructor` + `final`)만 사용.
- **로깅:** `System.out.println` 금지. SLF4J(`@Slf4j`) 사용. 민감정보 로깅 금지.
- 매직 넘버/문자열은 상수화. 슬롯 키('Q'/'W'/'E'/'R', 장비 슬롯명)는 언리얼 enum과 문자열 일치 필수.

---

## 9. DB 스키마 (요약 — 상세는 `init/01_schema.sql`)

FK 없음, 모든 자식 테이블은 `character_id` 인덱스로 연결.

| 테이블 | 키 | 용도 |
|--------|-----|------|
| `accounts` | account_id (PK) | 로그인 계정 (email, password_hash) |
| `characters` | character_id (PK), account_id (idx) | 계정당 다수 캐릭터 (name, class_type) |
| `character_stats` | character_id (PK) | 레벨/XP/포인트/Primary 4종(STR/INT/DEX/LUK) |
| `character_skills` | id (PK), (character_id, skill_tag) uq | 습득 스킬 + 스킬레벨 (Unlocked/Equipped만 저장, Locked/Eligible은 레벨로 자동 결정) |
| `character_skill_slots` | (character_id, slot_key) PK | 스킬 장착 슬롯 Q/W/E/R |
| `inventory_items` | id (PK), (character_id, slot_index) uq | 인벤토리 슬롯 (item_asset_id = DataAsset 이름, quantity) |
| `equipped_items` | (character_id, slot_type) PK | 장착 장비 (slot_type = EEquipmentSlot 이름) |
| `character_quick_slots` | (character_id, slot_key) PK | 아이템 퀵슬롯 1/2/3/4 |

- **`item_asset_id`** = 언리얼 `UD1ItemData` DataAsset 이름 문자열(예: `DA_Sword_01`). 아이템 메타데이터는 언리얼이 보유, DB는 식별자만.
- **`skill_tag`** = GameplayTag 문자열.
- **`slot_type`** = `EEquipmentSlot` enum 이름(Weapon/Helmet/Armor/Gloves/Boots/Necklace/Ring).

---

## 10. 작업 이력

| 날짜 | 요약 |
|------|------|
| 06-14 | 프로젝트 생성(Spring Boot 4.1.0). docker-compose MySQL 셋업. 스키마 8테이블(`init/01_schema.sql`, FK 없음). Entity 8종 작성. `ddl-auto: validate` 통과, DB 연결 확인. |
| 06-14 | Repository 8종(테이블별, 복합키는 `findById_CharacterId`). 깡 JWT 인증 구현(Spring Security 풀 스타터 제거 → `spring-security-crypto` BCrypt만 + `JwtInterceptor`). Auth 계층 완성: `AuthController`(/api/auth/register·login) → `AuthService` → `JwtUtil`. `GlobalExceptionHandler`+`ApiException`+`ErrorResponse`. 회원가입/로그인/중복/오인증 전부 테스트 통과, BCrypt 해시 저장 확인. |
| 06-15 | 캐릭터 API 구현: `GET /api/characters`(목록), `POST /api/characters`(생성). `character_stats`는 생성 시 INSERT 안 함 — 첫 게임 접속 시 언리얼 ScalableFloat 초기값으로 GAS 세팅 후 save API로 저장(SSOT=언리얼). 이름 중복 409 처리. |
| 06-15 | 데디서버 전용 API 구현: `ServerApiKeyInterceptor`(`X-Server-Api-Key` 헤더, `/api/server/**` 적용), `POST /api/server/characters/{id}/verify-session`(캐릭터 데이터 전체 로드, stats=null이면 신규), `POST /api/server/characters/{id}/save`(전체 일괄 저장, delete+insert 방식). DTO 6종(StatsDto/SkillDto/SkillSlotDto/InventoryItemDto/EquippedItemDto/QuickSlotDto). |
| 06-15 | 엔티티 클래스명 `Character` → `GameCharacter` 리네임 (java.lang.Character 충돌 해결). 파일명/Repository명도 동일하게 변경. 언리얼 HTTP 연동 테스트 — 클라이언트에서 로그인 성공 확인. |
| 06-16 | 캐릭터 응답에 `level` 추가 (stats 없으면 1). 계정당 캐릭터 4개 제한. 세션 토큰 기반 접속 플로우 구현: `JwtUtil`에 세션 토큰(sub=characterId, type=session, 60초) 발급/검증 + 로그인/세션 토큰 type 클레임 구분. `POST /api/matchmaking/town`(JWT, 소유권 검증 → 세션 토큰+Town 주소 반환). `verify-session`을 `POST /api/server/sessions/verify`(경로 characterId 제거, 토큰에서 추출)로 변경. `JwtException` → 401 핸들러 추가. Town 주소는 `game-server.town-address` config. API/스키마 문서 `docs/`에 작성. |
| 06-18 | **크로스 프로세스 travel 지원.** `game-server.dungeon-address` config 추가(127.0.0.1:7778). `JwtUtil::generateSessionTokenForServer(characterId)` 추가(서버간 이동 전용, accountId 불필요). `POST /api/server/sessions/issue`(ServerApiKey, IssueSessionRequest{characterId, destination} → IssueSessionResponse{sessionToken, serverAddress}) 신설 — `MatchmakingService::issueSessionToken` + `ServerController` 엔드포인트. destination: "town"→townAddress, "dungeon"→dungeonAddress. |
| 06-22~23 | **HuntingGround 매치메이킹 + 부하테스트 지원.** `game-server.hunting-ground-address`(127.0.0.1:7779) 추가. `MatchmakingService`에 `enterHuntingGround` 추가 — 기존 `enterTown`과 소유권검증+토큰발급 로직 공유(`issueMatchmakingToken` 헬퍼로 추출). `MatchmakingController`에 `POST /api/matchmaking/huntingground` 신설 — 부하테스트용 봇 클라이언트가 Town을 거치지 않고 바로 HuntingGround로 접속할 때 사용. `issueSessionToken`(서버간 이동용)의 destination 분기에도 "huntingground" 케이스 추가. |
