# 온라인 중고 경매 프로젝트

<img src="https://img.shields.io/badge/springboot-6DB33F?style=flat&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/react-61DAFB?style=flat&logo=react&logoColor=black"> <img src="https://img.shields.io/badge/mysql-4479A1?style=flat&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/Redis-FF4438?style=flat&logo=redis&logoColor=white"> <img src="https://img.shields.io/badge/nginx-009639?style=flat&logo=nginx&logoColor=white"> <img src="https://img.shields.io/badge/docker-2496ED?style=flat&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/Amazon%20EC2-FF9900?style=flat&logo=Amazon%20EC2&logoColor=white"> <img src="https://img.shields.io/badge/GitHubActions-2088FF?style=flat&logo=GitHubActions&logoColor=white">

### 사용자 간 중고 상품의 등록, 입찰, 낙찰까지의 흐름을 지원하는 실시간 경매 플랫폼입니다.

[OUA_V1](https://oua-v1.duckdns.org)

> 현재 혼자 개발하며 공부중인 프로젝트 입니다.

## 성능 개선

### 상품 조회 API & 내 주문 조회 API (Keyset Pagination & N+1 문제) 

- **상품 조회**
  * 평균 응답 시간: **35% 개선**
  * 대표 이미지 조회: **N+1 문제 제거** + **불필요한 응답 데이터 최소화**
  * 전체 쿼리 수 3개 → 1개로 축소

- **내 주문 조회**
  * 평균 응답 시간: **12% 개선**
  * 상품 조회 N+1 문제 제거 (ToOne 관계 → 페치 조인)
  * 전체 쿼리 수 2개 → 1개로 축소

 <details>
   <summary><h4>개선 과정</h4></summary>


   ### 개선 목적
   
   기존의 상품 조회와 내 주문 조회 API는 페이지 수가 증가할수록 응답 속도 저하와 부하가 발생했고, 이미지 조회 방식과 상품 조회 방식에서도 불필요한 N+1 쿼리 문제로 인해 성능이 저하되고 있었습니다. 
   
   이를 해결하기 위해 **Keyset Pagination 도입**, **이미지 조회 방식 개선**, **페치 조인**을 통해 성능을 향상시켰습니다.
   
   ### 기존 문제점
   
   #### 1. Offset Pagination 기반 조회 방식
   
   * `Pageable`을 이용한 `offset/limit` 기반 페이징.
   * 데이터가 많아질수록 오프셋 이후의 레코드를 **스캔해야 하므로** 시간이 기하급수적으로 증가.
   * 두 번의 쿼리 수행 (실제 데이터 조회 + count 쿼리) → 요청당 DB 부하 2배.
   
   #### 2. 대표 이미지 조회의 N+1 문제
   
   * 각 상품마다 별도의 `product_images` 테이블 쿼리 수행.
   * 조회된 상품 수가 많을수록 네트워크와 DB I/O 낭비 발생.
   * 프론트에서 모든 이미지 중 첫 이미지만 보여줌 → 불필요한 데이터 송수신.
 
   #### 3. 상품 정보 조회 시 N+1 문제
   
   * 주문 리스트에서 상품 정보를 DTO 변환 과정에서 별도로 조회 (`getProduct()`).
   * 상품:주문 = N:1 관계 → **ToOne 관계에서는 페치 조인 적용 가능**.
   * 결과적으로 조회 수만큼 쿼리 발생 → 전체 성능 저하.
   
  **기존 SQL 로그**
     
   ```sql
     -- 상품 조회
     select * from product where ... order by created_date desc limit ?, ?;
     
     -- Count 쿼리
     select count(distinct product_id) from product where ...;
     
     -- N+1 이미지 조회
     select * from product_images where product_id in (...);
   ```
 
   ```sql
     -- 주문 조회
     select * from orders where ... order by created_date desc limit ?, ?;
     
     -- N+1 상품 조회
     select * from product where orders_id in (...);
   ```
   
   ### 개선 작업 요약
   
   | 개선 항목          | 조치 내용                                                       |
   | -------------- | ----------------------------------------------------------- |
   | **페이징 전략**     | `Offset Pagination → Keyset Pagination`으로 전환                |
   | **쿼리 최적화**     | count 쿼리 제거, 단일 쿼리로 조회 수행                                   |
   | **이미지 조회 방식**  | `product_images` 테이블 조회 제거, `Product` 엔티티에 대표 이미지 URL 필드 추가 |
   | **데이터 전송량 감소** | 필요한 필드만 선택적으로 조회하여 응답 페이로드 축소                               |
   | **주문 조회 N+1 문제**  | 주문 → 상품 관계는 ToOne이므로 **Fetch Join**을 적용하여 단일 쿼리로 최적화 |
   
   ### 성능 비교
 
   #### 상품 조회
   
   | 항목                   | 개선 전       | 개선 후       | 변화율           |
   | -------------------- | ---------- | ---------- | ------------------------- |
   | **총 요청 수**           | 100건         | 100건         | 동일            |
   | **평균 응답 시간**         | 3,494ms    | 2,263ms    | ⬇️ **35.2% 감소**  |
   | **최소 응답 시간**         | 63ms       | 28ms       | ⬇️ 55.6% 감소      |
   | **최대 응답 시간**         | 10,078ms   | 6,633ms    | ⬇️ 34.2% 감소      |
   | **표준편차**             | 2,002ms    | 1,467ms    | ⬇️ 26.7% 감소         |
   | **처리량 (Throughput)** | 8.63 req/s | 7.85 req/s | ⬇️ 소폭 감소           |
   | **오류율**              | 0.0%       | 0.0%       | ✅ 동일                |
   | **평균 수신 바이트**        | 15.73 KB   | 14.11 KB   | ⬇️ 10.3% 감소      |
   | **평균 전송 바이트**        | 1.37 KB    | 1.25 KB    | ⬇️ 8.8% 감소       |
 
   #### 내 주문 조회
 
   | 항목                   | 개선 전         | 개선 후         | 변화율             |
   | -------------------- | ---------- | ------------ | --------------- |
   | **총 요청 수**           | 100건       | 100건         | 동일              |
   | **평균 응답 시간**         | 2,015ms    | 1,774ms      | ⬇️ **12.0% 감소** |
   | **최소 응답 시간**         | 50ms       | 22ms         | ⬇️ 56.0% 감소     |
   | **최대 응답 시간**         | 9,194ms    | 7,302ms      | ⬇️ 20.6% 감소     |
   | **표준편차**             | 1,890ms    | 1,523ms    | ⬇️ 19.3% 감소     |
   | **처리량 (Throughput)** | 8.82 req/s | 8.29 req/s | 소폭 감소           |
   | **오류율**              | 0.0%       | 0.0%         | ✅ 동일            |
   | **평균 수신 바이트**        | 8.25 KB    | 6.96 KB      | ⬇️ 15.6% 감소     |
   | **평균 전송 바이트**        | 2.87 KB    | 2.69 KB      | ⬇️ 6.3% 감소      |
 
 </details>

---

### 상품 상태 변경 시 동시성 제어 (ReentrantLock + 트랜잭션 커밋 후 해제)

  * 트랜잭션 경계 내에서 **안정적인 동시성 제어 확보**
  * 커밋 이후 락이 해제되어, **다른 쓰레드의 작업이 안전하게 처리됨**

   > [동시성 이슈 락 고민](https://github.com/Lanvizu/TIL/blob/main/%EA%B8%B0%ED%83%80/%EB%8F%99%EC%8B%9C%EC%84%B1_%EC%9D%B4%EC%8A%88.md)

  <details>
   <summary><h4>개선 과정 1 (Redis 분산락)</h4></summary>

   ### 개선 목적

   상품 상태 변경 로직에서 Redis 기반 락을 사용했지만, 트랜잭션 커밋 전에 락이 해제되며 **동시성 문제가 발생**했습니다.
   
   커밋이 완료되기 전에 락이 풀리면, 다른 쓰레드가 동일 리소스를 변경할 수 있어 **데이터 정합성에 문제**가 생긴다고 판단했습니다.

   이를 해결하기 위해 `TransactionSynchronizationManager`를 도입하여, 트랜잭션 커밋 이후에만 락을 해제하도록 개선했습니다.
   
   ### 기존 문제점

   ![Image](https://github.com/user-attachments/assets/4990dc6a-f1f6-4c8e-8f21-9ed057f059ff)
   
   #### 트랜잭션 이전 락 해제
 
   * `try-finally` 블록에서 비즈니스 로직 실행 후 **락을 즉시 해제**.
   * 하지만 트랜잭션 커밋은 메서드 반환 이후 수행되므로, **락 해제가 너무 이르게 발생**.
   * 결과적으로 다른 트랜잭션이 **락을 선점하고 커밋되지 않은 데이터를 읽거나 변경**할 위험 존재.
   
   ### 개선 작업

   | 개선 항목           | 조치 내용                                                   |
   | --------------- | ------------------------------------------------------- |
   | **락 해제 시점 조정**  | `TransactionSynchronizationManager`의 `afterCommit()` 사용 |
   | **트랜잭션 유무 확인**  | 트랜잭션 미존재 시 즉시 락 해제, 존재 시 커밋 후 해제                        |
   | **예외 상황 처리 보완** | 런타임 예외 발생 시에도 안전하게 락 해제                                 |
   
   ### 개선 결과

   ![Image](https://github.com/user-attachments/assets/3dfd4122-68ed-45b8-904f-5048299ff87a)
 
 </details>

<details>
   <summary><h4>개선 과정 2 (ReentrantLock)</h4></summary>

   ### 개선 목적   

   현재 프로젝트는 단일 AWS 서버를 통해서 배포가 진행되므로 Redis 분산락은 오버 엔지니어링이라고 판단했습니다.

   따라서 메모리 상에서 동작하며, 동일 JVM 내에서는 매우 빠르고 안정적인 락을 제공하는 ReentrantLock으로 개선했습니다.

   추가로 트랜잭션과 락의 범위를 수정하여 락을 획득 후, 트랜잭션을 시작하도록 설정해 동시성을 제어했습니다.
   
 </details>

---

### 이메일 인증 요청 비동기 처리

- 이메일 인증 요청을 동기 방식으로 처리하며 요청 당 평균 24초 이상 소요되고, DB 커넥션이 장시간 점유되어 전체 요청의 45%에서 타임아웃이 발생했습니다.

- 이메일 전송은 응답에 필수적인 작업이 아니므로, @Async를 활용해 별도 스레드에서 비동기 처리하도록 개선했습니다.

- 결과 평균 응답 시간이 약 805ms로 단축되었고, 에러율 0%, TPS 24배 증가 등 성능이 크게 향상되었습니다.

### 개선 전 (동기)
<img width="1340" height="98" alt="image" src="https://github.com/user-attachments/assets/d30c5d64-856e-490a-af2a-4c253c3d4da5" />

### 개선 후 (비동기)
<img width="1337" height="97" alt="image" src="https://github.com/user-attachments/assets/a8478ee8-2f91-4011-8f2a-31b5318be05c" />

- **평균 응답 시간**:	24,338ms ->	805ms (-96.7%)
- **TPS**: 2.8/sec ->	67.4/sec	(24배)
- **오류율**: 45%	-> 0%
