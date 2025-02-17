package com.ohhoonim.para.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.ohhoonim.para.Note;
import com.ohhoonim.para.Tag;

@Testcontainers
@JdbcTest
@Import(NoteRepository.class)
public class NoteRepositoryTest {

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:17.2-alpine"));

    @Autowired
    NoteRepository noteRepository;

    @Test
    public void addNoteTest() {
        var newNote = new Note(null, "yesterday did", "nothging");
        var newNoteId = UUID.randomUUID();
        noteRepository.addNote(newNote, newNoteId);

        var savedNote = noteRepository.getNote(newNoteId);
        assertThat(savedNote.isPresent()).isTrue();
        assertThat(savedNote.get().noteId()).isEqualTo(newNoteId);
    }

    @Test
    public void modifyNoteTest() {

        UUID noteId = UUID.randomUUID();
        var originNote = new Note(null, "origin titile", "contennts");
        noteRepository.addNote(originNote, noteId);

        var modifiedNote = new Note(noteId, "modified title", "contrents");
        noteRepository.modifyNote(modifiedNote);

        var currentNote = noteRepository.getNote(noteId);
        assertThat(currentNote.get().title()).isEqualTo("modified title");
    }

    @Test
    public void removeNoteTest() {
        var noteId = UUID.randomUUID();

        noteRepository.addNote(new Note(null, "titile", "contents"), noteId);
        noteRepository.removeNote(noteId);
        var note = noteRepository.getNote(noteId);

        assertThat(note.isEmpty()).isTrue();
    }

    @Test
    public void tagTest() {
        var tagJava = new Tag(null, "java");
        noteRepository.addTag(tagJava);

        var tagJavascript = new Tag(null, "javascript");
        noteRepository.addTag(tagJavascript);

        var tagSpring = new Tag(null, "spring");
        var addedTagSpring = noteRepository.addTag(tagSpring);

        assertThat(addedTagSpring.tag()).isEqualTo("spring");
        assertThat(addedTagSpring.tagId()).isEqualTo(3);

    }

    @Test
    public void addTagInNoteTest() {
        // 노트가 존재하지 않거나 등록된 태그가 아니면 에러
        assertThrows(Exception.class, () -> {
            noteRepository.addTagInNote(UUID.randomUUID(), new Tag(3l, "spring"));
        });
    }

    @Test
    public void addTagInNoteTest2() {
        // 노트는 존재하나 등록되지 않은 태그는 태그를 등록 후 노트에 태그 추가
        var noteId = UUID.randomUUID();
        noteRepository.addNote(new Note(null, "react basic", "nono"), noteId);
        var newedNote = noteRepository.getNote(noteId);
        assertThat(newedNote.get().noteId()).isEqualTo(noteId);

        noteRepository.addTagInNote(noteId, new Tag(null, "react"));

        var tagsInNote = noteRepository.tagsInNote(noteId);
        assertThat(tagsInNote.size()).isEqualTo(1);
        assertThat(tagsInNote.stream().findFirst().get().tag()).isEqualTo("react");
    }

}
