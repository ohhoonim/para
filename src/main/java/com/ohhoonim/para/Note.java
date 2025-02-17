package com.ohhoonim.para;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public record Note(
        UUID noteId,
        String title,
        String content,
        Set<Tag> tags,
        List<Para> paras) {
    public Note {
        if (title == null || title.isEmpty()) {
            title = "new note";
        }
    }

    public Note(UUID noteId, String title, String content) {
        this(noteId, title, content, null, null);
    }

    public Note(Note note, Set<Tag> tags) {
        this(note.noteId(), note.title(), note.content(), tags, null);
    }

    public Note(Note note, Set<Tag> tags, List<Para> paras) {
        this(note.noteId(), note.title(), note.content(), tags, paras);
    }

    public Note(UUID noteId, String title) {
        this(noteId, title, null, null, null);
    }

    public interface Usecase {
        public List<Note> findNote(String searchString, Page page);

        public Optional<Note> getNote(UUID noteId);

        public Optional<Note> addNote(Note newNote);

        public Optional<Note> modifyNote(Note modifiedNote);

        public void removeNote(UUID noteId);

        public Set<Tag> tags(UUID noteId);

        public Set<Tag> registTag(UUID noteId, Tag tag);

        public Set<Tag> removeTag(UUID noteId, Tag tag);

        public Set<Para> paras(UUID noteId);

        public void registPara(UUID noteId, Para para);

        public void removePara(UUID noteId, Para para);

    }
}