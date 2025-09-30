

![구글 (1)](https://github.com/user-attachments/assets/dd908832-99fa-49c7-85c5-a1af94855423)


<br>


<h3 align="center">투명한 유치원 정보의 새로운 기준</h3>
<p align="center">예비교사부터 현직 교사까지 생생한 후기와 따뜻한 조언이 있는 원바원</p>

<br>
<br>


<p align="right">
  <a href="https://play.google.com/store/apps/details?id=com.kindergarten.onebyone.one_by_one&pcampaignid=web_share">
    <img src="https://github.com/user-attachments/assets/2e478536-83c7-4137-b3d6-e304ca5d9f75" alt="원바원 Google Play" width="200"/>
  </a>
  <a href="https://apps.apple.com/kr/app/%EC%9B%90%EB%B0%94%EC%9B%90/id6744432824">
    <img src="https://github.com/user-attachments/assets/cd21f8de-8678-4d4b-8750-6f4f45a4b117" alt="원바원 App Store" width="200"/>
  </a>
</p>

<br>
<br>

## 사용 기술 스택
| 제목            | 내용              |
|---------------|-----------------|
| Java          | 백엔드 개발 언어       |
| Spring Boot   | 서버 애플리케이션 프레임워크 |
| JPA           | ORM             |
| MySQL         | 데이터베이스          |
| Firebase      | 푸시 알림 활용        |
| Docker        | 컨테이너 및 배포 이미지 생성 |
| Github Action | CI/CD 자동화 구성    |
| Swagger       | API 명세서         |
| Nginx         | HTTPS 설정        |
| Promtail      | 로그 수집           |
| Loki          | 로그 저장           |
| Grafana       | 모니터링            |

## 아키텍처 
![아키텍처 사진](https://github.com/user-attachments/assets/559c0bbe-e914-462b-b3da-0fd6e78d13eb)

## 📁 프로젝트 구조

```
src/main/java/com/onebyone/kindergarten/
│
├── domain/         
│   ├── communityComments/              # 댓글 관리
│   ├── communityPosts/                 # 게시글 관리
│   └── inquries/                       # 문의 관리
│   └── kindergartenInternshipReview/   # 인턴 리뷰 관리
│   └── kindergartenWorkHistory/        # 근무 경력 관리
│   └── kindergartenWorkReview/         # 근무 리뷰 관리
│   └── kindergartens/                  # 유치원 관리
│   └── notice/                         # 공지사항 관리
│   └── pushNotification/               # 푸시 알림 관리
│   └── reports/                        # 신고 관리
│   └── termAgreementHistory/           # 약관 관리
│   └── user/                           # 유저 관리
│   └── userBlock/                      # 차단 관리
│   └── userFavoriteKindergartens/      # 즐겨찾기 관리
│   └── userNotification/               # 유저 알림 관리
│
└── global/
    ├── batch                           # 배치 관리
    ├── common                          # 공통 관리
    ├── config                          # 설정 관리
    ├── docs                            # 문서 관리
    ├── enums                           # 열거형 타입 관리
    ├── exception                       # 예외 관리
    ├── facade                          # Facade 계층 관리
    ├── feignClient                     # FeignClient 관리
    ├── interceptor                     # 인터셉터 관리
    ├── jwt                             # Jwt 관리
    └── provider                        # 프로바이더 관리
```

<br/>

## ![타이틀 아이콘3](https://github.com/user-attachments/assets/0f1d39fc-f6ed-43b4-809e-97f33e104da2) 개발팀 소개

<table align="center">
    <tr align="center">
        <td><img src="https://avatars.githubusercontent.com/u/168513336?v=4" alt="프로필" width="100" /></td>
        <td><img src="https://avatars.githubusercontent.com/u/89853084?v=4" alt="프로필" width="100" /></td>
        <td><img src="https://avatars.githubusercontent.com/u/86885227?v=4" alt="프로필" width="100" /></td>
        <td><img src="https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcSIgvQ-fnIHtz8P6ROFWhrtybflnPZUO0Rx0QyKeyZktl7WWmIO" alt="프로필" width="100" /></td>
    </tr>
    <tr align="center">
        <td><a href="https://github.com/0zuth">김영주</a></td>
        <td><a href="https://github.com/Pinkippo">한승완</a></td>
        <td><a href="https://github.com/wngns1101">이주훈</a></td>
        <td>박가영</td>
    </tr>
      <tr align="center" >
        <td>FE</td>
        <td>APP, BE, FE</td>
        <td>BE</td>
        <td>DESIGN</td>
    </tr>
</table>

<br>
