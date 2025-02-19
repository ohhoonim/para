package com.ohhoonim.para.api;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ohhoonim.component.response.Response;
import com.ohhoonim.component.response.ResponseCode;
import com.ohhoonim.para.Note;
import com.ohhoonim.para.Page;
import com.ohhoonim.para.Para;
import com.ohhoonim.para.Para.Project;
import com.ohhoonim.para.Para.Shelf;
import com.ohhoonim.para.service.ParaService;

@RestController
public class ParaController {

    private final ParaService paraService;

    public ParaController(ParaService paraService) {
        this.paraService = paraService;
    }

    @GetMapping("/para/{paraId}/notes")
    public List<Note> notes(
            @PathVariable("paraId") UUID paraId,
            @RequestParam("category") String category) {
        return paraService.notes(Para.getParaInstance(paraId, category));
    }

    @PostMapping("/para/{paraId}/registNote")
    public List<Note> registNote(
            @PathVariable("paraId") UUID paraId,
            @RequestBody ParaNoteReq paraReq) {
        return paraService.registNote(
                Para.getParaInstance(paraId, paraReq.category()),
                paraReq.noteId());
    }

    public record ParaNoteReq(
            String category,
            UUID noteId,
            String targetCategory) {
        public ParaNoteReq(String category, UUID noteId) {
            this(category, noteId, null);
        }
    }

    @PostMapping("/para/{paraId}/removeNote")
    public List<Note> removeNote(
            @PathVariable("paraId") UUID paraId,
            @RequestBody ParaNoteReq paraReq) {
        return paraService.removeNote(
                Para.getParaInstance(
                        paraId,
                        paraReq.category()),
                paraReq.noteId());
    }

    @PostMapping("/para/{paraId}/moveTo")
    public Para moveToPara(
            @PathVariable("paraId") UUID paraId,
            @RequestBody ParaNoteReq paraReq) {
        return paraService.moveToPara(
                Para.getParaInstance(paraId, paraReq.category()),
                Shelf.getParaType(paraReq.targetCategory()))
                .orElseThrow(() -> new RuntimeException("Para이동에 실패하였습니다."));
    }

    @GetMapping("/para/{paraId}")
    public Para getPara(
            @PathVariable("paraId") UUID paraId,
            @RequestParam("category") String category) {
        return paraService.getPara(Para.getParaInstance(paraId, category))
                .orElseThrow(() -> new RuntimeException("category를 찾을 수 없습니다."));
    }

    @PostMapping("/para/add")
    public Para addPara(@RequestBody ParaReq newPara) {
        return paraService.addPara(Para.getNewParaInstance(
                newPara.category(),
                newPara.title(),
                newPara.content()))
                .orElseThrow(() -> new RuntimeException("category를 추가하지 못했습니다."));
    }

    public record ParaReq(
            UUID paraId,
            String category,
            String title,
            String content) {
    }

    @PostMapping("/para/{paraId}/modify")
    public Para modifyPara(
            @PathVariable("paraId") UUID paraId,
            @RequestBody ParaNoteReq paraReq) {
        return paraService.modifyPara(
                Para.getParaInstance(paraId, paraReq.category()))
                .orElseThrow(() -> new RuntimeException("Para 수정에 실패하였습니다."));
    }

    @PostMapping("/para/{paraId}/remove")
    public Response removePara(
            @PathVariable("paraId") UUID paraId,
            @RequestBody ParaNoteReq paraReq) {
        paraService.removePara(Para.getParaInstance(paraId, paraReq.category));
        return new Response.Success(
                ResponseCode.SUCCESS,
                null);
    }

    @PostMapping("/para/searchShelves")
    public List<Shelf> findShelves(@RequestBody Search<ShelfReq> search) {
        // @RequestParam("searchString") String searchString,
        // @RequestParam("page") Page page) {
        return paraService.findShelves(search.para().title(), search.page());
    }

    public record ShelfReq (
            UUID paraId,
            String title,
            String content,
            String category) {
    }

    public record Search<T>(
            T para,
            Page page) {
    }

    @PostMapping("/para/searchProjects")
    public List<Project> findProjects(@RequestBody Search<Project> search) {
        // @RequestParam("searchString") String searchString,
        // @RequestParam("page") Page page) {
        return paraService.findProjects(search.para().title(), search.page());
    }

}
