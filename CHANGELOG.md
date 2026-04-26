# [1.7.0](https://github.com/kjyy08/luigi-log-server/compare/v1.6.1...v1.7.0) (2026-04-26)


### Features

* **auth:** API Key 도메인과 keys 엔드포인트 정리 ([1fe0d8c](https://github.com/kjyy08/luigi-log-server/commit/1fe0d8cb246373a509bf2adfd36246daf62121e8))

## [1.6.1](https://github.com/kjyy08/luigi-log-server/compare/v1.6.0...v1.6.1) (2026-04-25)


### Bug Fixes

* **web:** 접근 거부 예외 응답 처리 추가 ([a323850](https://github.com/kjyy08/luigi-log-server/commit/a32385014afdae3530edf5d7106a437fb32bd4fa))

# [1.6.0](https://github.com/kjyy08/luigi-log-server/compare/v1.5.0...v1.6.0) (2026-04-25)


### Features

* **auth:** Hermes skill API key 발행 MVP 추가 ([7d9a38e](https://github.com/kjyy08/luigi-log-server/commit/7d9a38ebe33491de46cc79e2912a38e189498171))

# [1.5.0](https://github.com/kjyy08/luigi-log-server/compare/v1.4.1...v1.5.0) (2026-04-25)


### Features

* **post:** 조회수 중복 증가 방지 ([7dc3384](https://github.com/kjyy08/luigi-log-server/commit/7dc3384f71918dcd07329c1534e338895d99dedd))

## [1.4.1](https://github.com/kjyy08/luigi-log-server/compare/v1.4.0...v1.4.1) (2026-04-24)


### Bug Fixes

* **post:** avoid null binding in post search query ([#80](https://github.com/kjyy08/luigi-log-server/issues/80)) ([45dffe2](https://github.com/kjyy08/luigi-log-server/commit/45dffe212c254415b8ec9f50557a6d12013c43b4))

# [1.4.0](https://github.com/kjyy08/luigi-log-server/compare/v1.3.1...v1.4.0) (2026-04-24)


### Features

* **post:** add post discovery APIs ([#79](https://github.com/kjyy08/luigi-log-server/issues/79)) ([0bc175f](https://github.com/kjyy08/luigi-log-server/commit/0bc175fd8f301963fe1ba202cdf3a46e35ecef60))

## [1.3.1](https://github.com/kjyy08/luigi-log-server/compare/v1.3.0...v1.3.1) (2026-04-24)


### Bug Fixes

* mutate domain models in place ([1132b96](https://github.com/kjyy08/luigi-log-server/commit/1132b968d087dd3a633a01c8a174c5aae8985216))

# [1.3.0](https://github.com/kjyy08/luigi-log-server/compare/v1.2.1...v1.3.0) (2026-01-12)


### Features

* OAuth 로그인 시 이메일 nullable 지원 ([#77](https://github.com/kjyy08/luigi-log-server/issues/77)) ([bac2946](https://github.com/kjyy08/luigi-log-server/commit/bac2946637886fafcd2f4d2c9739d5fad98484c4))

## [1.2.1](https://github.com/kjyy08/luigi-log-server/compare/v1.2.0...v1.2.1) (2026-01-09)


### Bug Fixes

* CORS 설정에 와일드카드 서브도메인 지원 추가 ([b16d9cb](https://github.com/kjyy08/luigi-log-server/commit/b16d9cb51d82f42c8a8105cdf2ed75fb20fb9c04))

# [1.2.0](https://github.com/kjyy08/luigi-log-server/compare/v1.1.0...v1.2.0) (2026-01-09)


### Features

* CORS 허용 도메인 업데이트 ([183ede9](https://github.com/kjyy08/luigi-log-server/commit/183ede91330916d47586c1d308c1960a9ec7a3e4))

# [1.1.0](https://github.com/kjyy08/luigi-log-server/compare/v1.0.1...v1.1.0) (2026-01-09)


### Features

* Prod 환경 설정 수정 ([151c971](https://github.com/kjyy08/luigi-log-server/commit/151c971d623620802253954c0491167f5fc8affa))

## [1.0.1](https://github.com/kjyy08/luigi-log-server/compare/v1.0.0...v1.0.1) (2026-01-09)


### Bug Fixes

* 댓글 API 설명문 개선 ([#76](https://github.com/kjyy08/luigi-log-server/issues/76)) ([8466971](https://github.com/kjyy08/luigi-log-server/commit/8466971e1a40c278e9481233a975ef39a0a96eff))

# 1.0.0 (2026-01-09)


### chore

* 모듈 구조 개선 및 패키지 경로 변경 ([#69](https://github.com/kjyy08/luigi-log-server/issues/69)) ([ec3d70b](https://github.com/kjyy08/luigi-log-server/commit/ec3d70b7898c7d9ec2116949a1f7b32c1ae41792))


### Features

* App 모듈 초기 구동 환경 설정 ([#19](https://github.com/kjyy08/luigi-log-server/issues/19)) ([1f03814](https://github.com/kjyy08/luigi-log-server/commit/1f038140f4d29a8c3b9b5dbc491270efad35f4d2)), closes [#9](https://github.com/kjyy08/luigi-log-server/issues/9) [#9](https://github.com/kjyy08/luigi-log-server/issues/9)
* Auth Credentials Adapter 모듈 구현 ([#39](https://github.com/kjyy08/luigi-log-server/issues/39)) ([d31db1a](https://github.com/kjyy08/luigi-log-server/commit/d31db1a0d4f401e32b6e2f21b6e45c657e447ac3)), closes [#31](https://github.com/kjyy08/luigi-log-server/issues/31) [#31](https://github.com/kjyy08/luigi-log-server/issues/31) [#31](https://github.com/kjyy08/luigi-log-server/issues/31) [#31](https://github.com/kjyy08/luigi-log-server/issues/31) [#31](https://github.com/kjyy08/luigi-log-server/issues/31) [#31](https://github.com/kjyy08/luigi-log-server/issues/31) [#31](https://github.com/kjyy08/luigi-log-server/issues/31)
* Auth Credentials Application 모듈 구현 ([#38](https://github.com/kjyy08/luigi-log-server/issues/38)) ([282b355](https://github.com/kjyy08/luigi-log-server/commit/282b355f85e392bab5d540a97f45eb0bec892113)), closes [#30](https://github.com/kjyy08/luigi-log-server/issues/30) [#30](https://github.com/kjyy08/luigi-log-server/issues/30) [#30](https://github.com/kjyy08/luigi-log-server/issues/30)
* Auth Credentials Domain 모듈 구현 ([#37](https://github.com/kjyy08/luigi-log-server/issues/37)) ([ca398c7](https://github.com/kjyy08/luigi-log-server/commit/ca398c7f48e899d8cd46d2dec52717ecbf513156)), closes [#29](https://github.com/kjyy08/luigi-log-server/issues/29) [#29](https://github.com/kjyy08/luigi-log-server/issues/29)
* Auth Token Adapter 모듈 구현 ([#42](https://github.com/kjyy08/luigi-log-server/issues/42)) ([db53dca](https://github.com/kjyy08/luigi-log-server/commit/db53dcac39c16d515b81756ea39a93cb928fb7d0)), closes [#36](https://github.com/kjyy08/luigi-log-server/issues/36) [#36](https://github.com/kjyy08/luigi-log-server/issues/36) [#36](https://github.com/kjyy08/luigi-log-server/issues/36) [#36](https://github.com/kjyy08/luigi-log-server/issues/36) [#36](https://github.com/kjyy08/luigi-log-server/issues/36) [#36](https://github.com/kjyy08/luigi-log-server/issues/36) [#36](https://github.com/kjyy08/luigi-log-server/issues/36) [#36](https://github.com/kjyy08/luigi-log-server/issues/36)
* Auth Token Application 모듈 구현 ([#41](https://github.com/kjyy08/luigi-log-server/issues/41)) ([f8c0ad8](https://github.com/kjyy08/luigi-log-server/commit/f8c0ad8672eb32076b3a90db10d58fd0f6027e62)), closes [#35](https://github.com/kjyy08/luigi-log-server/issues/35) [#35](https://github.com/kjyy08/luigi-log-server/issues/35) [#35](https://github.com/kjyy08/luigi-log-server/issues/35)
* Auth Token Domain 모듈 구현 ([#40](https://github.com/kjyy08/luigi-log-server/issues/40)) ([701c501](https://github.com/kjyy08/luigi-log-server/commit/701c501ac14ecc6459c02501e3a9a820ef3519df)), closes [#27](https://github.com/kjyy08/luigi-log-server/issues/27) [#27](https://github.com/kjyy08/luigi-log-server/issues/27)
* Content Comment Application 모듈 구현 ([#52](https://github.com/kjyy08/luigi-log-server/issues/52)) ([8612d8a](https://github.com/kjyy08/luigi-log-server/commit/8612d8a140f99261bf05d388dd1b81dcf3969c53)), closes [#49](https://github.com/kjyy08/luigi-log-server/issues/49) [#49](https://github.com/kjyy08/luigi-log-server/issues/49) [#49](https://github.com/kjyy08/luigi-log-server/issues/49) [#49](https://github.com/kjyy08/luigi-log-server/issues/49) [#49](https://github.com/kjyy08/luigi-log-server/issues/49)
* Content Comment Domain 모듈 구현 및 코드 리팩터링 ([#51](https://github.com/kjyy08/luigi-log-server/issues/51)) ([dbd956e](https://github.com/kjyy08/luigi-log-server/commit/dbd956e044b94d7ec0876ba6e7ff8ac75c91e801)), closes [#48](https://github.com/kjyy08/luigi-log-server/issues/48) [#48](https://github.com/kjyy08/luigi-log-server/issues/48) [#48](https://github.com/kjyy08/luigi-log-server/issues/48) [#48](https://github.com/kjyy08/luigi-log-server/issues/48) [#48](https://github.com/kjyy08/luigi-log-server/issues/48) [#48](https://github.com/kjyy08/luigi-log-server/issues/48) [#48](https://github.com/kjyy08/luigi-log-server/issues/48) [#48](https://github.com/kjyy08/luigi-log-server/issues/48) [#48](https://github.com/kjyy08/luigi-log-server/issues/48) [#48](https://github.com/kjyy08/luigi-log-server/issues/48) [#48](https://github.com/kjyy08/luigi-log-server/issues/48) [#48](https://github.com/kjyy08/luigi-log-server/issues/48) [#48](https://github.com/kjyy08/luigi-log-server/issues/48) [#48](https://github.com/kjyy08/luigi-log-server/issues/48)
* Content Post Adapter 모듈 구현 ([#53](https://github.com/kjyy08/luigi-log-server/issues/53)) ([eefd7a5](https://github.com/kjyy08/luigi-log-server/commit/eefd7a5444fca3294f76b91101a405fe5c1d55ab)), closes [#50](https://github.com/kjyy08/luigi-log-server/issues/50) [#50](https://github.com/kjyy08/luigi-log-server/issues/50) [#50](https://github.com/kjyy08/luigi-log-server/issues/50) [#50](https://github.com/kjyy08/luigi-log-server/issues/50) [#50](https://github.com/kjyy08/luigi-log-server/issues/50) [#50](https://github.com/kjyy08/luigi-log-server/issues/50) [#50](https://github.com/kjyy08/luigi-log-server/issues/50)
* Content Post API 응답에 글 작성자 정보 필드 추가 및 슬러그 길이 확장 ([#67](https://github.com/kjyy08/luigi-log-server/issues/67)) ([ccd4b59](https://github.com/kjyy08/luigi-log-server/commit/ccd4b59eb88d4ce37e89286b9065418b5221c049)), closes [#66](https://github.com/kjyy08/luigi-log-server/issues/66) [#66](https://github.com/kjyy08/luigi-log-server/issues/66) [#66](https://github.com/kjyy08/luigi-log-server/issues/66) [#66](https://github.com/kjyy08/luigi-log-server/issues/66) [#66](https://github.com/kjyy08/luigi-log-server/issues/66) [#66](https://github.com/kjyy08/luigi-log-server/issues/66) [#66](https://github.com/kjyy08/luigi-log-server/issues/66) [#66](https://github.com/kjyy08/luigi-log-server/issues/66) [#66](https://github.com/kjyy08/luigi-log-server/issues/66)
* libs/adapter 모듈 구현 ([#21](https://github.com/kjyy08/luigi-log-server/issues/21)) ([0b77de2](https://github.com/kjyy08/luigi-log-server/commit/0b77de2a535d9653991f6a368e167b37c73ef011)), closes [#11](https://github.com/kjyy08/luigi-log-server/issues/11) [#11](https://github.com/kjyy08/luigi-log-server/issues/11) [#11](https://github.com/kjyy08/luigi-log-server/issues/11) [#11](https://github.com/kjyy08/luigi-log-server/issues/11) [#11](https://github.com/kjyy08/luigi-log-server/issues/11) [#11](https://github.com/kjyy08/luigi-log-server/issues/11) [#11](https://github.com/kjyy08/luigi-log-server/issues/11) [#11](https://github.com/kjyy08/luigi-log-server/issues/11)
* libs/common 모듈 구현 ([#20](https://github.com/kjyy08/luigi-log-server/issues/20)) ([445305e](https://github.com/kjyy08/luigi-log-server/commit/445305e7ca5096e78444ae5feafdc5761387ed08)), closes [#10](https://github.com/kjyy08/luigi-log-server/issues/10) [#10](https://github.com/kjyy08/luigi-log-server/issues/10) [#10](https://github.com/kjyy08/luigi-log-server/issues/10) [#10](https://github.com/kjyy08/luigi-log-server/issues/10)
* Media Adapter 모듈 구현 ([#60](https://github.com/kjyy08/luigi-log-server/issues/60)) ([cc73a5f](https://github.com/kjyy08/luigi-log-server/commit/cc73a5feb5e5074a819cd58063f8b7a35f226b63)), closes [#57](https://github.com/kjyy08/luigi-log-server/issues/57) [#57](https://github.com/kjyy08/luigi-log-server/issues/57) [#57](https://github.com/kjyy08/luigi-log-server/issues/57) [#57](https://github.com/kjyy08/luigi-log-server/issues/57) [#57](https://github.com/kjyy08/luigi-log-server/issues/57) [#57](https://github.com/kjyy08/luigi-log-server/issues/57) [#57](https://github.com/kjyy08/luigi-log-server/issues/57) [#57](https://github.com/kjyy08/luigi-log-server/issues/57) [#57](https://github.com/kjyy08/luigi-log-server/issues/57) [#57](https://github.com/kjyy08/luigi-log-server/issues/57) [#57](https://github.com/kjyy08/luigi-log-server/issues/57) [#57](https://github.com/kjyy08/luigi-log-server/issues/57) [#57](https://github.com/kjyy08/luigi-log-server/issues/57) [#57](https://github.com/kjyy08/luigi-log-server/issues/57)
* Media Application 모듈 구현 ([#59](https://github.com/kjyy08/luigi-log-server/issues/59)) ([25f3bc5](https://github.com/kjyy08/luigi-log-server/commit/25f3bc59c3bd65abec351a68d98397f9f846e355)), closes [#56](https://github.com/kjyy08/luigi-log-server/issues/56) [#56](https://github.com/kjyy08/luigi-log-server/issues/56) [#56](https://github.com/kjyy08/luigi-log-server/issues/56) [#56](https://github.com/kjyy08/luigi-log-server/issues/56)
* Media Domain 모듈 구현 ([#58](https://github.com/kjyy08/luigi-log-server/issues/58)) ([cf442a5](https://github.com/kjyy08/luigi-log-server/commit/cf442a597dc21a34b4a5229d24a5b85100139b26)), closes [#55](https://github.com/kjyy08/luigi-log-server/issues/55) [#55](https://github.com/kjyy08/luigi-log-server/issues/55) [#55](https://github.com/kjyy08/luigi-log-server/issues/55) [#55](https://github.com/kjyy08/luigi-log-server/issues/55) [#55](https://github.com/kjyy08/luigi-log-server/issues/55)
* Member Adapter 모듈 구현 ([#34](https://github.com/kjyy08/luigi-log-server/issues/34)) ([da31b8f](https://github.com/kjyy08/luigi-log-server/commit/da31b8f75a14a6836615d013838ef87335a58b57)), closes [#26](https://github.com/kjyy08/luigi-log-server/issues/26) [#26](https://github.com/kjyy08/luigi-log-server/issues/26) [#26](https://github.com/kjyy08/luigi-log-server/issues/26) [#26](https://github.com/kjyy08/luigi-log-server/issues/26) [#26](https://github.com/kjyy08/luigi-log-server/issues/26) [#26](https://github.com/kjyy08/luigi-log-server/issues/26)
* Member Application 모듈 구현 ([#33](https://github.com/kjyy08/luigi-log-server/issues/33)) ([4626579](https://github.com/kjyy08/luigi-log-server/commit/462657921b16d6f08880e68b92513763e39f8ceb)), closes [#25](https://github.com/kjyy08/luigi-log-server/issues/25) [#25](https://github.com/kjyy08/luigi-log-server/issues/25) [#25](https://github.com/kjyy08/luigi-log-server/issues/25)
* Member Domain 모듈 구현 ([#32](https://github.com/kjyy08/luigi-log-server/issues/32)) ([85423b4](https://github.com/kjyy08/luigi-log-server/commit/85423b40d703e378d8c423003f9a232973ce38f8)), closes [#24](https://github.com/kjyy08/luigi-log-server/issues/24) [#24](https://github.com/kjyy08/luigi-log-server/issues/24) [#24](https://github.com/kjyy08/luigi-log-server/issues/24)
* 게시글 삭제 API 및 작성자 정보 필드 추가 ([#65](https://github.com/kjyy08/luigi-log-server/issues/65)) ([3bf7f6a](https://github.com/kjyy08/luigi-log-server/commit/3bf7f6aa4ce6edb29ff4a36c2c11f2244ab85fe4)), closes [#50](https://github.com/kjyy08/luigi-log-server/issues/50) [#50](https://github.com/kjyy08/luigi-log-server/issues/50) [#50](https://github.com/kjyy08/luigi-log-server/issues/50) [#50](https://github.com/kjyy08/luigi-log-server/issues/50) [#50](https://github.com/kjyy08/luigi-log-server/issues/50)
* 게시글 삭제 도메인 로직 추가 ([#63](https://github.com/kjyy08/luigi-log-server/issues/63)) ([4109dcf](https://github.com/kjyy08/luigi-log-server/commit/4109dcf9356171a3abcb0c28b4e3baeb8e03ed80)), closes [#25](https://github.com/kjyy08/luigi-log-server/issues/25) [#25](https://github.com/kjyy08/luigi-log-server/issues/25)
* 게시글 삭제 유스케이스 및 작성자 정보 조회 추가 ([#64](https://github.com/kjyy08/luigi-log-server/issues/64)) ([e737a69](https://github.com/kjyy08/luigi-log-server/commit/e737a69162bf0a6aab384525116e9d5922a18997)), closes [#25](https://github.com/kjyy08/luigi-log-server/issues/25) [#25](https://github.com/kjyy08/luigi-log-server/issues/25)
* 댓글 모듈 구현 ([#70](https://github.com/kjyy08/luigi-log-server/issues/70)) ([b32c0a3](https://github.com/kjyy08/luigi-log-server/commit/b32c0a35c1dc3ac9125fe3de5535225fad76c299)), closes [#48](https://github.com/kjyy08/luigi-log-server/issues/48) [#48](https://github.com/kjyy08/luigi-log-server/issues/48) [#48](https://github.com/kjyy08/luigi-log-server/issues/48) [#48](https://github.com/kjyy08/luigi-log-server/issues/48) [#49](https://github.com/kjyy08/luigi-log-server/issues/49) [#49](https://github.com/kjyy08/luigi-log-server/issues/49) [#50](https://github.com/kjyy08/luigi-log-server/issues/50) [#50](https://github.com/kjyy08/luigi-log-server/issues/50) [#50](https://github.com/kjyy08/luigi-log-server/issues/50) [#50](https://github.com/kjyy08/luigi-log-server/issues/50)
* 방명록 모듈 추가 ([#72](https://github.com/kjyy08/luigi-log-server/issues/72)) ([7968fa8](https://github.com/kjyy08/luigi-log-server/commit/7968fa831deb2135a3a06eea1b15bf807dcbf9c4))
* 프로필 조회 및 수정에 새 필드 추가 및 사용하지 않는 기술 스택 필드 제거 ([#68](https://github.com/kjyy08/luigi-log-server/issues/68)) ([c59121b](https://github.com/kjyy08/luigi-log-server/commit/c59121be6c50a273a0e285b806000b979cc38e23))
* 회원 가입 프로필 이미지 지원 및 다중 프로필 조회 기능 추가 ([#61](https://github.com/kjyy08/luigi-log-server/issues/61)) ([693a22e](https://github.com/kjyy08/luigi-log-server/commit/693a22e33701320c054d2f02aa56385ee1b50423)), closes [#25](https://github.com/kjyy08/luigi-log-server/issues/25) [#25](https://github.com/kjyy08/luigi-log-server/issues/25) [#25](https://github.com/kjyy08/luigi-log-server/issues/25) [#25](https://github.com/kjyy08/luigi-log-server/issues/25) [#25](https://github.com/kjyy08/luigi-log-server/issues/25)


### BREAKING CHANGES

* content 관련 기존 경로가 변경되어 참조 코드 수정 필요
