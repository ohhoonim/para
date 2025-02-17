package com.ohhoonim.para;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ohhoonim.para.Para.Project;
import com.ohhoonim.para.Para.Shelf.Archive;
import com.ohhoonim.para.Para.Shelf.Area;
import com.ohhoonim.para.Para.Shelf.Resource;
import com.ohhoonim.para.port.ProjectPort;
import com.ohhoonim.para.port.ShelfPort;
import com.ohhoonim.para.service.ParaService;

@ExtendWith(MockitoExtension.class)
public class ParaTest {

    @InjectMocks
    ParaService paraService;

    @Mock
    ProjectPort projectPort;

    @Mock
    ShelfPort shelfPort;

    @Test
    public void projectParaTest() {
        // project 등록
        var projectId = UUID.randomUUID();
        var project = new Para.Project(
                projectId,
                "Youtube 영상제작",
                "no coontents",
                LocalDate.now(),
                LocalDate.now().plusMonths(6),
                Status.Backlog);

        when(projectPort.getProject(any())).thenReturn(Optional.of(project));

        var newProject = new Para.Project(
                null,
                "Youtube 영상제작",
                "no coontents",
                LocalDate.now(),
                LocalDate.now().plusMonths(6),
                Status.Backlog);
        Optional<Para> resultPara = paraService.addPara(newProject);

        assertThat(resultPara.get().paraId()).isEqualTo(projectId);
        assertThat(resultPara.get()).isInstanceOf(Project.class);
        assertThat(((Project) resultPara.get()).status()).isEqualTo(Status.Backlog);
        verify(projectPort, times(1)).addProject(any(), any());

        // note 추가 
        List<Note> notesInProject = List.of(
                new Note(UUID.randomUUID(), null, null),
                new Note(UUID.randomUUID(), null, null),
                new Note(UUID.randomUUID(), null, null));
        when(projectPort.notes(any())).thenReturn(notesInProject);

        var currentPara = Para.of(projectId, Project.class);
        var targetNoteId = UUID.randomUUID();
        var notes = paraService.registNote(currentPara, targetNoteId);

        assertThat(notes.size()).isEqualTo(3);
        verify(projectPort, times(1)).registNote(any(), any());

    }

    @Test
    public void paraOfTest() {
        var paraId = UUID.randomUUID();
        var project = Para.of(paraId, Project.class);

        assertThat(project.paraId()).isEqualTo(paraId);
        assertThat(project).isInstanceOf(Project.class);

        var area = Para.of(paraId, Area.class);
        assertThat(area).isInstanceOf(Area.class);

        var resource = Para.of(paraId, Resource.class);
        assertThat(resource).isInstanceOf(Resource.class);

        var archive = Para.of(paraId, Archive.class);
        assertThat(archive).isInstanceOf(Archive.class);
    }

    @Test
    public void shelfAreaParaTest() {
        // area 등록
        when(shelfPort.getShelf(any())).thenReturn(Optional.of(Para.of(UUID.randomUUID(), Area.class)));

        var area = new Area(null, "youtube 제작", "pass");
        var para = paraService.addPara(area);

        assertThat(para.get()).isInstanceOf(Area.class);
        verify(shelfPort, times(1)).addShelf(any(), any());

        // note 추가
        List<Note> areaNotes = List.of(
                new Note(UUID.randomUUID(), "1장", null),
                new Note(UUID.randomUUID(), "2장", null));
        when(shelfPort.notes(any())).thenReturn(areaNotes);

        Area areaNote = Para.of(UUID.randomUUID(), Area.class);
        var results = paraService.registNote(areaNote, UUID.randomUUID());
        assertThat(results.size()).isEqualTo(2);
        verify(shelfPort, times(1)).registNote(any(), any());
    }

    @Test
    public void shelfResourceParaTest() {
        // reource 등록, 테스트 간편성을 고려하여 세부 항목은 생략(Para.of)
        when(shelfPort.getShelf(any()))
                .thenReturn(Optional.of(Para.of(UUID.randomUUID(), Resource.class)));

        var resource = new Resource(null, "Javascript", "pass");
        var para = paraService.addPara(resource);

        assertThat(para.get()).isInstanceOf(Resource.class);
        assertThat(para.get().paraId()).isNotNull();
        verify(shelfPort, times(1)).addShelf(any(), any());

        // note추가 생략
    }

    @Test
    @DisplayName("Area에서 Resource로 이동, Archiving에서도 활용 가능")
    public void moveShelfTest() {
        // shelf 이동
        var resource = Para.of(UUID.randomUUID(), Resource.class);
        when(shelfPort.getShelf(any())).thenReturn(Optional.of(resource));

        var area = Para.of(UUID.randomUUID(), Area.class);
        var result = paraService.moveToPara(area, Resource.class);

        verify(shelfPort, times(1)).moveToPara(any(), any());
        assertThat(result.get()).isInstanceOf(Resource.class);

    }

    @Test
    public void archivingParaTest() {
        // archive 등록 
        var archiveResult = Para.of(UUID.randomUUID(), Archive.class);
        when(shelfPort.getShelf(any())).thenReturn(Optional.of(archiveResult));

        var archive = new Archive(null, "youtube 제작", "pass");
        Optional<Para> para = paraService.addPara(archive);

        assertThat(para.get()).isInstanceOf(Archive.class);
        verify(shelfPort, times(1)).addShelf(any(), any());

        // archiving : moveToShelfTest 참고   
    }
}

/*
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


 */

/*

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
 */