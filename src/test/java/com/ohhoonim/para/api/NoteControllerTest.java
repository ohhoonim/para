package com.ohhoonim.para.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohhoonim.para.Note;
import com.ohhoonim.para.Page;
import com.ohhoonim.para.Tag;
import com.ohhoonim.para.api.NoteController.NoteRequest;
import com.ohhoonim.para.api.NoteController.ParaReq;
import com.ohhoonim.para.service.NoteService;
import com.ohhoonim.para.service.TagService;

@WebMvcTest(NoteController.class)
@WithMockUser
public class NoteControllerTest {

    @Autowired
    MockMvcTester mockMvcTester;

    @MockitoBean
    NoteService noteService;

    @MockitoBean
    TagService tagService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void findNote() throws JsonProcessingException {
        var noteReq = new NoteRequest("", new Page());
        var reqData = objectMapper.writeValueAsString(noteReq);
        mockMvcTester.post().with(csrf())
                .uri("/note/list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqData) // post method는 여기에 담아서 넘긴다
                .assertThat()
                .apply(print()) 
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.code") // JsonPath로 동작 시작
                .isEqualTo("SUCCESS");
    }

    @Test
    public void makeUuid() {
        System.out.println(UUID.randomUUID());
    }

    @Test
    public void getNote() {
        mockMvcTester.get()
                .uri("/note/765cf4ba-e8ee-4e3d-9358-f47fa5c9fe41")
                .contentType(MediaType.APPLICATION_JSON)
                .assertThat()
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.message")
                .isEqualTo("note가 존재하지 않습니다");
    }

    @Test
    public void addNote() throws JsonProcessingException {
        var noteId = UUID.randomUUID();
        var stubNote = new Note(noteId, "economic", "micoro");
        when(noteService.addNote(any())).thenReturn(Optional.of(stubNote));

        var note = new Note(null, "economic", "micro");
        var newNoteString = objectMapper.writeValueAsString(note);
        mockMvcTester.post().with(csrf())
                .uri("/note/addNote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newNoteString)
                .assertThat()
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.data.noteId")
                .isEqualTo(noteId.toString());
    }

    @Test
    public void modifyNote() throws JsonProcessingException {
        var noteId = UUID.randomUUID();
        var stubNote = new Note(noteId, "economic", "micoro");
        when(noteService.modifyNote(any())).thenReturn(Optional.of(stubNote));

        var note = new Note(null, "economic", "micro");
        var newNoteString = objectMapper.writeValueAsString(note);
        mockMvcTester.post().with(csrf())
                .uri("/note/modifyNote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newNoteString)
                .assertThat()
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.data.noteId")
                .isEqualTo(noteId.toString());

    }

    @Test
    public void removeNote() throws JsonProcessingException {
        var noteId = UUID.randomUUID().toString();
        mockMvcTester.post().with(csrf())
                .uri("/note/" + noteId.toString() + "/removeNote")
                .contentType(MediaType.APPLICATION_JSON)
                .assertThat()
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("SUCCESS");

        verify(noteService, times(1)).removeNote(any());
    }

    @Test
    public void tags() {
        var noteId = UUID.randomUUID().toString();
        mockMvcTester.get().with(csrf())
                .uri("/note/" + noteId + "/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .assertThat()
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("SUCCESS");
    }

    @Test
    public void registTag() throws JsonProcessingException {
        var newTag = new Tag(null, "java");
        var noteId = UUID.randomUUID().toString();
        mockMvcTester.post().with(csrf())
                .uri("/note/" + noteId + "/registTag")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTag))
                .assertThat()
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("SUCCESS");

    }

    @Test
    public void removeTag() throws JsonProcessingException {
        var targetTag = new Tag(1L, "java");
        var noteId = UUID.randomUUID().toString();
        mockMvcTester.post().with(csrf())
                .uri("/note/" + noteId + "/removeTag")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(targetTag))
                .assertThat()
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("SUCCESS");

    }

    @Test
    public void paras() {
        var noteId = UUID.randomUUID().toString();
        mockMvcTester.get()
                .uri("/note/" + noteId + "/paras")
                .contentType(MediaType.APPLICATION_JSON)
                .assertThat()
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("SUCCESS");
    }

    @Test
    public void registPara() throws JsonProcessingException {
        var para = new ParaReq(UUID.randomUUID(), "project");
        var noteId = UUID.randomUUID().toString();
        mockMvcTester.post().with(csrf())
                .uri("/note/" + noteId + "/registPara")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(para))
                .assertThat()
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("SUCCESS");
    }

    @Test
    public void removePara() throws JsonProcessingException {
        var para = new ParaReq(UUID.randomUUID(), "project");
        var noteId = UUID.randomUUID().toString();
        mockMvcTester.post().with(csrf())
                .uri("/note/" + noteId + "/removePara")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(para))
                .assertThat()
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("SUCCESS");
    }

    @Test
    public void findTagsLimit20PerPageTest() throws JsonProcessingException {
        var noteReq = new NoteRequest(
                "",
                new Page());
        mockMvcTester.post().with(csrf())
                .uri("/note/searchTags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteReq))
                .assertThat()
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.code")
                .isEqualTo("SUCCESS");
    }

}
