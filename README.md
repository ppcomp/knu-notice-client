# 착한 선배 - 강원대 공지사항 알리미
강원대 내부의 수 많은 사이트들의 공지사항을 모아서 한눈에 보여주는 안드로이드 애플리케이션

다운로드 - [GooglePlayStore](https://play.google.com/store/apps/details?id=com.ppcomp.knu)

## 기능
- 원하는 교내 사이트 공지사항 확인 가능 (구독 리스트)
- 설정해둔 키워드가 들어간 공지사항 확인 가능 (키워드 리스트)
- 구독한 사이트에 새로운 글이 올라오면 푸시알림
- 설정해둔 키워드가 들어간 새로운 글이 올라오면 푸시알림
- 공지사항 북마크 기능

## 개발환경
- `Android Studio` v 3.x.x
- `Kotlin` v 1.3.72

## 사용한 주요 라이브러리
- `firebase`
- `retrofit2`
- `kakao sdk`
- `roomDB`
- `paging`

## API Version
- `minSdkVersion` : API Level 16 (Android 4.1 Jelly Bean)
- `targetSdkVersion` : API Level 29 (Android 10.0 Q)

## 서버
- `Crawling Server` - [GitHub](https://github.com/ppcomp/knu-notice-server)
- `Firebase` - 푸시알림, 미들서버