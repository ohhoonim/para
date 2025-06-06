# PARA 분류법

PARA 분류법에 대한 백엔드 구현체입니다.

## 디지털노트 분류 흐름도

```plantuml
@startuml
skinparam monochrome reverse
title 신규노트 작
start
:create new note;
:input title, contents<
if (none classify) then
else
    fork
        :add project to note|
    fork again
        :add shelf to note|
    fork again
        :add tag|
    endfork
endif
:save note/
stop
@enduml
```


## 디지털노트 상태표

 ```plantuml
 @startuml
 skinparam monochrome reverse
 
 title PARA 분류하기(state)
 
 state Inbox : 분류할 노트
 
 [*] --> Inbox : note
 
 state PARA {
 state hasDeadline <<entryPoint>>
 state isJob <<entryPoint>>
 state withInterest <<entryPoint>>
 state Project : 기한 내 완성해야 할 노트 목록
 state Area : 메인 업무. 회사업무 등
 state Resource : 관심있는 주제들. 취미활동 등
 state Archive : 보관할 Project, Area, Resource
 
 hasDeadline --> Project : 프로젝트로 분류
 Project --> [*] : 폐기
 
 isJob --> Area : Area로 분류
 Area --> Resource : 관심사 변경
 Area --> Archive : 보관
 
 withInterest --> Resource : Resource로 분류
 Resource --> Area : 관심사 변경
 Resource --> Archive : 보관
 Archive --> Area : 활성화
 Archive --> Resource : 활성화
 Archive --> [*] : 폐기
 }
 
 Inbox --> hasDeadline
 Inbox --> isJob
 Inbox --> withInterest
 
 PARA --> [*] : Note 삭제
 
 @enduml
 ```

## Deadline 이 있는 Project

```plantuml
@startuml
skinparam monochrome reverse
!pragma useVerticalIf on

title para usecase (activity)

start
:new Para;
if (hasDeadline) then (yes)
    :project 등록;
else if (no deadline)  then (shelf)
    :Shelf 등록;
end if
stop
@enduml
```

## PARA 논리 모델

```plantuml
@startuml
skinparam monochrome reverse

class Note

rectangle Para {
    class Project
    class Area
    class Resource
    class Archive
}

Note [noteId] ..> "paras [0.*]" Para : > classify

@enduml
```

## PARA 구현 모델
- 분류방법을 카테고리화 하는 것이 좋겠다고 판단
- Data Object Programming(DOP) 방식으로 모델을 구성
- java의 record를 활용
- category 필드는 infra/api 구현시 도출 되었음

```plantuml
@startuml
skinparam monochrome reverse

class Note {
    noteId: UUID
    title: String
    content: String
}

class Tag {
    tagId: Long
    tag: String
}

interface NoteUsecase <inner>

Note -left- NoteUsecase
class NoteService <<Service>> 

interface NotePort
interface TagPort
NoteService .down.|> NoteUsecase
NoteService .up.> NotePort: <dependency>
NoteService .up.> TagPort: <dependency>

Note "1" .down.> "tags [0..*]" Tag

interface Para <sealed> {
    paraId(): UUID
    title(): String
    content(): String
    category(): String
}
interface ParaUsecase <inner>

Para -right- ParaUsecase
class ParaService <<Service>> 

interface ProjectPort
interface ShelfPort

ParaUsecase <|.up. ParaService
ParaService .up.> ProjectPort
ParaService .up.> ShelfPort

class Project implements Para {
    startDate: LocalDate
    endDate: LocalDate
    category: String {"project"}
}

interface Shelf <sealed> extends Para

class Area implements Shelf  {
    category: String {"area"}
}
class Resource implements Shelf {
    category: String {"resource"}
}
class Archive implements Shelf {
    category: String {"resource"}
}

Note [noteId] ..right..> "paras [0..*]" Para : > classify 
Para [paraId] ..left..> "notes [0..*]" Note : > regist note 


@enduml
```

## Spring Security 프로세스  

```plantuml
@startuml
skinparam monochrome reverse

rectangle request as request
rectangle Filter as filter
rectangle AuthenticationManager as manager
rectangle AuthenticationProvider as provider
rectangle UserDetailsService as service
rectangle PasswordEncoder as encoder
rectangle SecurityContext as context

request --> filter : 1. 요청가로챔
filter --> manager : 2. 인증관련 위임
manager --> provider : 3. provider 이용
provider --> service : 4. 사용자 탐색
provider ..> encoder : 4. 암호 검증
provider --> manager : 5. 인증결과
manager --> filter
filter --> context : 6. Authentication 저장

@enduml
```

## Spring Security 도에인 language

```plantuml
@startuml
skinparam monochrome reverse

interface Authentication {
    principal
    credentials
    authorities
    getDetails(): Object
    isAuthenticated(): boolean
    setAuthenticated(boolean): void
}

interface UserDetailsService {
    loadUserByUsername(String): UserDetails
}
interface UserDetails {
    authorities: Collection<? extends GrantedAuthority>
    password: String
    username: String
    isAccountNonExpired: boolean {true}
    isAccountNonLocked: boolean {true}
    isCredentialsNonExpired: boolan {true}
    isEnabled: boolean {true}

}

interface GrantedAuthority {
    authority: String
}

interface AuthorizationManager {
    authorize(Supplier<Authentication>, Object<T>): AuthorizationResult
    verify(Supplier<Authentication>, Object<T>): void
}

interface AuthorizationResult {
    isGranted: boolan
}
UserDetailsService ..> UserDetails
UserDetails ..> GrantedAuthority
GrantedAuthority ..> AuthorizationManager
AuthorizationManager ..> AuthorizationResult

@enduml
```













