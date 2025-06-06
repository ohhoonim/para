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
import com.ohhoonim.para.Page;
import com.ohhoonim.para.Para;
import com.ohhoonim.para.Para.Project;
import com.ohhoonim.para.Para.Shelf.Area;
import com.ohhoonim.para.api.ParaController.ParaNoteReq;
import com.ohhoonim.para.api.ParaController.ParaReq;
import com.ohhoonim.para.api.ParaController.Search;
import com.ohhoonim.para.service.ParaService;

@WebMvcTest(ParaController.class)
@WithMockUser
public class ParaControllerTest {

    @Autowired
    MockMvcTester mockMvcTester;

    @MockitoBean
    ParaService paraService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void makeUuid() {
        System.out.println(UUID.randomUUID());
    }

    @Test
    public void notes() {
        var paraId = "83a713bf-64fe-4ef5-854b-f30682f7cbd4";
        mockMvcTester.get()
                .uri("/para/" + paraId + "/notes")
                .param("category", "project")
                .contentType(MediaType.APPLICATION_JSON)
                .assertThat().apply(print())
                .hasStatusOk().bodyJson()
                .extractingPath("$.code")
                .isEqualTo("SUCCESS");
    }

    @Test
    public void registNote() throws JsonProcessingException {
        var paraReq = new ParaNoteReq("project", UUID.randomUUID());
        var body = objectMapper.writeValueAsString(paraReq);
        var paraId = "83a713bf-64fe-4ef5-854b-f30682f7cbd4";
        mockMvcTester.post().with(csrf())
                .uri("/para/" + paraId + "/registNote")
                .param("category", "project")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .assertThat().apply(print())
                .hasStatusOk().bodyJson()
                .extractingPath("$.code")
                .isEqualTo("SUCCESS");
    }

    @Test
    public void removeNote() throws JsonProcessingException {
        var paraReq = new ParaNoteReq("project", UUID.randomUUID());
        var body = objectMapper.writeValueAsString(paraReq);
        var paraId = "83a713bf-64fe-4ef5-854b-f30682f7cbd4";
        mockMvcTester.post().with(csrf())
                .uri("/para/" + paraId + "/removeNote")
                .param("category", "area")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .assertThat().apply(print())
                .hasStatusOk().bodyJson()
                .extractingPath("$.code")
                .isEqualTo("SUCCESS");

    }

    @Test
    @WithMockUser
    public void moveToPara() throws JsonProcessingException {
        var paraReq = new ParaNoteReq("area", UUID.randomUUID(), "archive");
        var body = objectMapper.writeValueAsString(paraReq);
        var paraId = "83a713bf-64fe-4ef5-854b-f30682f7cbd4";

        when(paraService.moveToPara(any(), any()))
                .thenReturn(Optional.of(
                        Para.of(UUID.fromString(paraId), Area.class)));

        mockMvcTester.post().with(csrf())
                .uri("/para/" + paraId + "/moveTo")
                .param("category", "area")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .assertThat().apply(print())
                .hasStatusOk().bodyJson()
                .extractingPath("$.code")
                .isEqualTo("SUCCESS");
    }

    // @GetMapping("/para/{paraId}")
    @Test
    public void getPara() {
        var paraId = UUID.randomUUID();
        when(paraService.getPara(any()))
                .thenReturn(Optional.of(Para.of(paraId, Area.class)));
        mockMvcTester.get()
                .uri("/para/" + paraId)
                .param("category", "area")
                .contentType(MediaType.APPLICATION_JSON)
                .assertThat().apply(print())
                .hasStatusOk().bodyJson()
                .extractingPath("$.data.paraId")
                .isEqualTo(paraId.toString());
    }

    @Test
    public void addPara() throws JsonProcessingException {
        var newPara = new ParaReq(null, "area", "economic", "micro ecomonimc");
        when(paraService.addPara(any()))
                .thenReturn(Optional.of(Para.of(UUID.randomUUID(), Area.class)));
        mockMvcTester.post().with(csrf())
                .uri("/para/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPara))
                .assertThat().bodyJson()
                .extractingPath("$.code")
                .isEqualTo("SUCCESS");
    }

    @Test
    public void modifyPara() throws JsonProcessingException {
        var paraId = UUID.randomUUID();
        var para = new ParaReq(paraId, "area", "economic", "micro ecomonimc");
        when(paraService.modifyPara(any()))
                .thenReturn(Optional.of(Para.of(UUID.randomUUID(), Area.class)));
        mockMvcTester.post().with(csrf())
                .uri("/para/" + paraId + "/modify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(para))
                .assertThat().apply(print()).bodyJson()
                .extractingPath("$.code")
                .isEqualTo("SUCCESS");

    }

    @Test
    public void removePara() throws JsonProcessingException {
        var paraId = UUID.randomUUID();
        var para = new ParaReq(paraId, null, null, null);
        mockMvcTester.post().with(csrf())
                .uri("/para/" + paraId + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(para))
                .assertThat().apply(print()).bodyJson()
                .extractingPath("$.code")
                .isEqualTo("SUCCESS");
        verify(paraService, times(1)).removePara(any());
    }

    @Test
    public void findProject() throws JsonProcessingException {
        var project = Para.of(UUID.randomUUID(), Project.class);
        var search = new Search<Project> (project, new Page());
        mockMvcTester.post().with(csrf())
            .uri("/para/searchProjects")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(search))
            .assertThat().apply(print()).bodyJson()
            .extractingPath("$.code")
            .isEqualTo("SUCCESS");
            
    }

    @Test
    public void findShelves() throws JsonProcessingException {
        var area = Para.of(UUID.randomUUID(), Area.class);
        var search = new Search<Area> (area, new Page());
        mockMvcTester.post().with(csrf())
            .uri("/para/searchShelves")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(search))
            .assertThat().apply(print()).bodyJson()
            .extractingPath("$.code")
            .isEqualTo("SUCCESS");

    }

}
