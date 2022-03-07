# NoteCraft

> 마인크래프트 악보작성 플러그인

> 개발 버전 : Spigot 1.18.1

> jar 파일 다운로드 : (티스토리 링크 추가예정)

***

## 악보기호

c, d, e, f, g, a, b, : 음계 (도, 레, 미, 파, 솔, 라, 시)
<br>
1, 2, 4, 8, 16, 32 . . . : 박자
> c16 => 16분음표 도
<br>

r : 쉼표
> r4 => 4분쉼표
<br>

. : 반박자 추가
> a8. => 점8분음표 라
> 
> f.2 => 점2분음표 파
<br>

\# : 샵
> c#16 => 16분음표 도샵
> 
> a8# => 8분음표 라샵
<br>

$ : 플랫
> d$4 => 4분음표 레플렛
> 
> g4.$ => 점4분음표 솔플렛
<br>

% : 내츄럴
> d2% => 2분음표 레
> 
> f32%. => 점32분음표 파
<br>

< : 1 옥타브 내림
<br>

\> : 1 옥타브 올림
<br>

\+ : 동시음표
> f#+b8. => 점8분음표 레와 파샵을 동시에 연주
> 
> c+d+e+f+g+a+b2 => 2분음표 도레미파솔라시도를 동시에 연주
<br>

v : 기본 음량 (0 ~ 10)
> v5 => 볼륨을 5로 조절
<br>

t : 기본 템포 (1~200)
> t75 => 템포를 75로 조절
<br>

i : 기본 박자
> i8 c f i4 c f => 8분음표 도, 파를 연주 하고 4분음표 도, 파를 연주
<br>

u{ } : 기본 악기
> s{minecraft.block.note_block.bell} => 해당 악기로 연주
<br>

s{ } : 기본 샵
> s{f} => 악보의 모든 '파'를 샵으로 처리
> 
> s{fa} => 악보의 모든 '파', '라'를 샵으로 처리
<br>

p{ } : 기본 플랫
> p{d} => 악보의 모든 '레'를 플렛으로 처리
> 
> p{bg} => 악보의 모든 '레', '솔'을 플렛으로 처리
<br>

***

## 악보작성 예시

마인크래프트 소리블럭의 특성 상 모든 범위의 음을 표현할 수는 없습니다.

해당 사진과 같이 좁은 범위의 음만 연주 할 수 있습니다.

![건반](https://user-images.githubusercontent.com/60739875/156932308-b310c91a-a8fd-47dc-a9a0-71d879dd48b1.png)
<br>
<br>
작성방법은 메이플스토리2와 매우 유사합니다.
악보를 작성 할 때는 기호 간 띄어쓰기나 공백문자(엔터)를 신경쓰지 않아도 됩니다.
본인이 편한대로 구분해서 작성하시면 됩니다.

예시1) 군대 기상나팔 악보
```
v10 t135 s{a}
f4 d4 d8 d8 d4 d8 f8 d8 < a8 > d8 f16 d16 < a8 > d8 f8 d8
< a8 a16 a16 a4 > d8 d16 d16 d4 d8 f8 d8 < a8 > d8 f16
d16 < a8 > d8 f8 d8 d4 < a8 a16 a16 a4 > d8 d8 d4 d8 f8
d8 < a8 > d8 f16 d16 < a8 > d8 f8 d8 d4 < a8 a16 a16
a4 r2 > f8 f16 d16 < a8 > d8 < a16 > d16 < a16 > d16
f1 < a8 a8 > d8 d8 f8 f16 d16 < a8 > d8 < a16 > d16
< a16 > d16 f5
```

***

## 명령어

< \> = 필수, [ ] = 선택

/notecraft play <fileName1\> [fileName2] . . .
> 자기자신의 위치에 음악을 재생합니다.
  
/notecraft playLocation <x\> <y\> <z\> <world\> <fileName1\> [fileName2] . . .
> 입력한 좌표에 음악을 재생합니다.
  
/notecraft playPlayer <playerName\> <fileName1\> [fileName2] . . .
> 해당 플레이어의 위치에 음악을 재생합니다.
  
/notecraft stop <ID\>
> 재생중인 음악을 중지합니다.
  
/notecraft clear
> 재생중인 모든 음악을 중지합니다.
  
/notecraft list
> 재생 가능한 남은 음악 수를 표시합니다.

***

## config.yml

WhiteList
> 명령어를 사용하게 할 유저를 추가합니다. ex) WhiteList : {user1,user2}

Limit Simultaneous Play
> 동시에 재생 가능한 최대 악보 수 (여러 악보를 동시에 재생해도 하나의 음악으로 처리됩니다.) ( 1 ~ )

Limit Total Playing
> 총 재생 가능한 음악 수 ( 1 ~  )

Warning Alert
> true  : 악보에서 잘못된 부분을 알립니다.

> false : 악보에서 잘못된 부분을 알리지 않습니다.

OP Only Command
> true  : 서버관리자만 명령어를 사용할 수 있습니다.

> false : 모든 유저가 명령어를 사용할 수 있습니다.

Ignore Wrong Note
> true : 악보 해독 중, 잘못된 부분을 무시하고 넘어갑니다.

> false : 악보 해독 중, 잘못된 부분을 무시하지 않고 멈춥니다.

***
