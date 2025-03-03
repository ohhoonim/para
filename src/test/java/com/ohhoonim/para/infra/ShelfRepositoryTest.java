package com.ohhoonim.para.infra;

import static org.assertj.core.api.Assertions.assertThat;

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
import com.ohhoonim.para.Para.Shelf.Area;
import com.ohhoonim.para.Para.Shelf.Resource;;

@Testcontainers
@JdbcTest
@Import({ ShelfRepository.class, NoteRepository.class })
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ShelfRepositoryTest {

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:17.2-alpine"));

    @Autowired
    ShelfRepository shelfRepository;

    @Test
    @DisplayName("shelf 관리 - area, resource, archive")
    public void basicShelfTest() {
        var area = new Area(null, "java", "grammar java");
        var paraId = UUID.randomUUID();
        shelfRepository.addShelf(area, paraId);
        var shelf = shelfRepository.getShelf(paraId);
        assertThat(shelf.isPresent()).isTrue();
        assertThat(shelf.get()).isInstanceOf(Area.class);

        var modifiedShelf = new Area(shelf.get().paraId(), "java 2", "design pattern");
        shelfRepository.modifyShelf(modifiedShelf);

        var modifiedResult = shelfRepository.getShelf(paraId);
        assertThat(modifiedResult.get().title()).isEqualTo("java 2");
        // shelf 이동 
        shelfRepository.moveToPara(modifiedResult.get(), Resource.class);
        var movedResult = shelfRepository.getShelf(paraId);
        assertThat(movedResult.get()).isInstanceOf(Resource.class);

        // shelf 삭제
        shelfRepository.removeShelf((Resource) movedResult.get());
        var deletedResult = shelfRepository.getShelf(paraId);
        assertThat(deletedResult.isPresent()).isFalse();
    }

    @Autowired
    NoteRepository noteRepository;

    @Test
    public void findShelfInNote() {
        var noteId = UUID.randomUUID();
        var newNote = new Note(null, "variable in java", "constants");
        noteRepository.addNote(newNote, noteId);

        var area = new Area(null, "java", "grammar java");
        var areaId = UUID.randomUUID();
        shelfRepository.addShelf(area, areaId);

        var resource = new Resource(null, "javascript", "grammar javascript");
        var resourceId = UUID.randomUUID();
        shelfRepository.addShelf(resource, resourceId);

        shelfRepository.registNote(noteId, new Area(areaId));
        shelfRepository.registNote(noteId, new Resource(resourceId));
        // note내 shelf 목록 조회
        var results = shelfRepository.findShelfInNote(noteId);

        assertThat(results.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("shelf내 노트 관리")
    public void notesInShelfTest() {

        var area = new Area(null, "java", "grammar java");
        var areaId = UUID.randomUUID();
        shelfRepository.addShelf(area, areaId);

        var noteId = UUID.randomUUID();
        var newNote = new Note(null, "variable in java", "constants");
        noteRepository.addNote(newNote, noteId);

        var noteId2 = UUID.randomUUID();
        var newNote2 = new Note(null, "method in java", "methods");
        noteRepository.addNote(newNote2, noteId2);

        shelfRepository.registNote(noteId, new Area(areaId));
        shelfRepository.registNote(noteId2, new Area(areaId));

        var notes = shelfRepository.notes(new Area(areaId));
        assertThat(notes.size()).isEqualTo(2);
        assertThat(notes.getFirst().title()).contains("java");

        shelfRepository.removeNote(noteId2, new Area(areaId));

        var removedNotes = shelfRepository.notes(new Area(areaId));
        assertThat(removedNotes.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("shelf 검색")
    public void searchShelfTest() {
        var area = new Area(null, "java", "grammar java");
        var areaId = UUID.randomUUID();
        shelfRepository.addShelf(area, areaId);

        var resource = new Resource(null, "javascript", "grammar javascript");
        var resourceId = UUID.randomUUID();
        shelfRepository.addShelf(resource, resourceId);

        var shelves = shelfRepository.findShelves("javascr", new Page());
        assertThat(shelves.size()).isEqualTo(1);
    }

    
}
