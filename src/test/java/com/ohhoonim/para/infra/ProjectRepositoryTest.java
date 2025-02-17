package com.ohhoonim.para.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.ohhoonim.para.Note;
import com.ohhoonim.para.Page;
import com.ohhoonim.para.Para;
import com.ohhoonim.para.Para.Project;
import com.ohhoonim.para.Status;

@Import({ ProjectRepository.class, NoteRepository.class })
@JdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Testcontainers
public class ProjectRepositoryTest {

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:17.2-alpine"));

    @Autowired
    ProjectRepository projectRepository;

    @Test
    @DisplayName("프로젝트 기본 관리")
    public void basicProjectTest() {
        var newParaId = UUID.randomUUID();
        var newProject = new Para.Project(null,
                "spring for begining",
                "spring course",
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                Status.Backlog);

        projectRepository.addProject(newProject, newParaId);

        var addedProject = projectRepository.getProject(newParaId);
        assertThat(addedProject.isPresent()).isTrue();

        var modifiedProject = new Para.Project(newParaId,
                "spring for junior",
                newProject.content(),
                newProject.startDate(),
                newProject.endDate(),
                Status.Ready);
        projectRepository.modifyProject(modifiedProject);
        var modifiedResult = projectRepository.getProject(newParaId);
        assertThat(modifiedResult.isPresent()).isTrue();
        assertThat(modifiedResult.get()).isInstanceOf(Project.class);
        assertThat(((Project) modifiedResult.get()).status()).isEqualTo(Status.Ready);

        projectRepository.removeProject(new Project(newParaId));
        var removedProject = projectRepository.getProject(newParaId);
        assertThat(removedProject.isEmpty()).isTrue();
    }

    @Autowired
    NoteRepository noteRepository;

    @Test
    public void registNoteTest() {
        assertThrows(Exception.class, () -> {
            projectRepository.registNote(UUID.randomUUID(), new Project(UUID.randomUUID()));
        });
    }

    @Test
    @DisplayName("프로젝트에 속한 노트 관리")
    public void noteInProjectTest() {
        var noteId = UUID.randomUUID();
        var note = new Note(null, "spring data core", "cores");
        noteRepository.addNote(note, noteId);

        var newParaId = UUID.randomUUID();
        var project = new Project(null, "Spring Data", null, null, null, null);
        projectRepository.addProject(project, newParaId);

        projectRepository.registNote(noteId, new Project(newParaId));
        var notesInProject = projectRepository.notes(new Project(newParaId));

        assertThat(notesInProject.size()).isEqualTo(1);

        // note 기준으로 project 목록 가져오기
        var projects = projectRepository.findProjectInNote(noteId);
        assertThat(projects.size()).isEqualTo(1);

        projectRepository.removeNote(noteId, new Project(newParaId));
        var emptyNotes = projectRepository.notes(new Project(newParaId));

        assertThat(emptyNotes.size()).isEqualTo(0);
    }

    @Test
    public void searchProjectsTest() {
        var paraId1 = UUID.randomUUID();
        var paraId2 = UUID.randomUUID();

        var project = new Project(null, "Spring Data", null, null, null, null);
        projectRepository.addProject(project, paraId1);
        var project2 = new Project(null, "Spring Data", null, null, null, null);
        projectRepository.addProject(project2, paraId2);

        var results = projectRepository.findProjects("Dat", new Page());
        assertThat(results.size()).isEqualTo(2);

    }
}
