package com.ohhoonim.para.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ohhoonim.para.Note;
import com.ohhoonim.para.Page;

public interface NotePort {

    void addNote(Note newNote, UUID newNoteId);

    Optional<Note> getNote(UUID noteId);

    void modifyNote(Note modifiedNote);

    void removeNote(UUID noteId);

    List<Note> findNote(String searchString, Page page);
    
}
