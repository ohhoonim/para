package com.ohhoonim.para.api;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ohhoonim.component.response.Response;
import com.ohhoonim.component.response.Response.Success;
import com.ohhoonim.component.response.ResponseCode;
import com.ohhoonim.para.Note;
import com.ohhoonim.para.Page;
import com.ohhoonim.para.Para;
import com.ohhoonim.para.Tag;
import com.ohhoonim.para.service.NoteService;
import com.ohhoonim.para.service.TagService;

@RestController
public class NoteController {

    private final NoteService noteService;
    private final TagService tagService;

    public NoteController(
            NoteService noteService, TagService tagService) {
        this.noteService = noteService;
        this.tagService = tagService;
    }

    @PostMapping("/note/list")
    public List<Note> findNote(@RequestBody NoteRequest noteReq) {
        // @RequestParam("searchString") String searchString,
        // @RequestParam("page") Page page) {
        return noteService.findNote(noteReq.searchString(),
                noteReq.page());
    }

    public record NoteRequest(
            String searchString,
            Page page) {
    }

    // controller에서는 optional을 제거해주는 것이 좋다 
    @GetMapping("/note/{noteId}")
    // public Optional<Note> getNote(@PathVariable("noteId") UUID noteId) {
    public Note getNote(@PathVariable("noteId") UUID noteId) {
        return noteService.getNote(noteId)
                .orElseThrow(() -> new RuntimeException("note가 존재하지 않습니다"));
    }

    @PostMapping("/note/addNote")
    public Note addNote(@RequestBody Note newNote) {
        return noteService.addNote(newNote).orElseThrow(
                () -> new RuntimeException("저장되지 않았습니다."));
    }

    @PostMapping("/note/modifyNote")
    public Note modifyNote(@RequestBody Note modifiedNote) {
        return noteService.modifyNote(modifiedNote)
            .orElseThrow(() -> new RuntimeException("수정되지 않았습니다"));
    }

    @PostMapping("/note/{noteId}/removeNote")
    public Response removeNote(@PathVariable UUID noteId) {
        noteService.removeNote(noteId);
        return new Success(ResponseCode.SUCCESS, noteId);
    }

    @GetMapping("/note/{noteId}/tags")
    public Set<Tag> tags(@PathVariable("noteId") UUID noteId) {
        return noteService.tags(noteId);
    }

    @PostMapping("/note/{noteId}/registTag")
    public Set<Tag> registTag(@PathVariable("noteId") UUID noteId,
            @RequestBody Tag tag) {
        return noteService.registTag(noteId, tag);
    }

    @PostMapping("/note/{noteId}/removeTag")
    public Set<Tag> removeTag(@PathVariable("noteId") UUID noteId,
            @RequestBody Tag tag) {
        return noteService.removeTag(noteId, tag);
    }

    @GetMapping("/note/{noteId}/paras")
    public Set<Para> paras(@PathVariable("noteId") UUID noteId) {
        return noteService.paras(noteId);
    }

    @PostMapping("/note/{noteId}/registPara")
    public Response registPara(@PathVariable("noteId") UUID noteId,
            @RequestBody ParaReq para) {
        noteService.registPara(noteId, 
                Para.getParaInstance(para.paraId(), para.category()));
        return new Success(ResponseCode.SUCCESS, noteId);
    }

    @PostMapping("/note/{noteId}/removePara")
    public Response removePara(@PathVariable("noteId") UUID noteId,
            @RequestBody ParaReq para) {
        noteService.removePara(noteId, 
            Para.getParaInstance(para.paraId(), para.category()));
        return new Success(ResponseCode.SUCCESS, noteId);
    }

    public record ParaReq(
        UUID paraId,
        String category
    ) {}

    @PostMapping("/note/searchTags")
    public Set<Tag> findTagsLimit20PerPage(
            @RequestBody NoteRequest noteReq) {
            // @RequestParam("tag") String tag,
            // @RequestParam("page") Page page) {
        return tagService.findTagsLimit20PerPage(
                noteReq.searchString(),
                noteReq.page());
    }

}
