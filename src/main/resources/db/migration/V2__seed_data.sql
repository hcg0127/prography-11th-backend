-- 관리자
MERGE INTO MEMBERS (login_id, password, name, role, status, phone, created_at, updated_at)
    KEY (login_id)
    VALUES ('admin', '$2a$10$64Na51g9VgEOwQt2ikZC1.bL.VAjNhVhxgh5dbl8pSXcEavg70Op2', '관리자', 'ADMIN', 'ACTIVE',
            '010-0000-0000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 기수
MERGE INTO COHORTS (generation, name, created_at, updated_at)
    KEY (generation)
    VALUES (10, '10기', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
           (11, '11기', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 파트
INSERT INTO PARTS (cohort_id, name, created_at, updated_at)
SELECT C.id, P.name, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM COHORTS C
         CROSS JOIN (VALUES ('SERVER'), ('WEB'), ('iOS'), ('ANDROID'), ('DESIGN')) AS P(name)
WHERE C.generation IN (10, 11)
  AND NOT EXISTS (SELECT 1 FROM PARTS WHERE cohort_id = C.id AND name = P.name);

-- 팀
INSERT INTO TEAMS (cohort_id, name, created_at, updated_at)
SELECT C.id, T.name, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM COHORTS C
         CROSS JOIN (VALUES ('Team A'), ('Team B'), ('Team C')) AS T(name)
WHERE C.generation = 11
  AND NOT EXISTS (SELECT 1 FROM TEAMS WHERE cohort_id = C.id AND name = T.name);

-- 기수 회원 (수정본)
INSERT INTO COHORT_MEMBERS (cohort_id, member_id, deposit, excuse_count, created_at, updated_at, PART_ID, TEAM_ID)
SELECT C.id,
       M.id,
       100000,
       0,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP,
       P.ID,
       T.ID
FROM COHORTS C,
     MEMBERS M,
     TEAMS T,
     PARTS P
WHERE C.generation = 11
  AND M.login_id = 'admin'
  -- 👇 [수정된 부분] 파트와 팀을 명확히 지정하고 기수와 연결합니다.
  AND P.cohort_id = C.id
  AND P.name = 'SERVER' -- 원하시는 파트명으로 변경 (예: SERVER)
  AND T.cohort_id = C.id
  AND T.name = 'Team A' -- 원하시는 팀명으로 변경 (예: Team A)
  -- 👆 여기까지 추가
  AND NOT EXISTS (SELECT 1 FROM COHORT_MEMBERS WHERE cohort_id = C.id AND member_id = M.id);

-- 보증금 이력
INSERT INTO DEPOSIT_HISTORIES (cohort_member_id, type, description, amount, balance_after, created_at, updated_at)
SELECT CM.id, 'INITIAL', '초기 보증금', 100000, 100000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM COHORT_MEMBERS CM
         JOIN MEMBERS M ON CM.member_id = M.id
WHERE M.login_id = 'admin'
  AND NOT EXISTS (SELECT 1 FROM DEPOSIT_HISTORIES WHERE cohort_member_id = CM.id AND type = 'INITIAL');