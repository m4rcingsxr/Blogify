package com.blogify.controller;


import com.blogify.entity.Customer;
import com.blogify.payload.CommentDto;
import com.blogify.payload.ResponsePage;
import com.blogify.service.CommentService;
import com.blogify.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.blogify.util.CommentTestUtil.generateDummyCommentDto;
import static com.blogify.util.CustomerTestUtil.generateDummyCustomer;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class CommentControllerTest {

    private static final String BASE_URL = "/api/v1/comments";
    private static final long COMMENT_ID = 1L;

    @MockBean
    private CommentService commentService;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CommentDto commentDto;
    private Customer customer;

    @BeforeEach
    void setUp() {
        commentDto = generateDummyCommentDto();
        customer = generateDummyCustomer();
        customer.setEmail("test@gmail.com");
        customer.setId(1L);
    }

    @Test
    @WithMockUser
    void whenFindAll_thenReturnListOfComments() throws Exception {
        CommentDto commentDto2 = generateDummyCommentDto();

        ResponsePage<CommentDto> responsePage = new ResponsePage<>();
        responsePage.setContent(List.of(commentDto, commentDto2));
        responsePage.setPage(0);
        responsePage.setPageSize(2);
        responsePage.setTotalElements(2L);
        responsePage.setTotalPages(1);

        when(commentService.findAll(anyInt(), any(Sort.class))).thenReturn(responsePage);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].fullName").value(commentDto.getFullName()))
                .andExpect(jsonPath("$.content[0].content").value(commentDto.getContent()))
                .andExpect(jsonPath("$.content[1].fullName").value(commentDto2.getFullName()))
                .andExpect(jsonPath("$.content[1].content").value(commentDto2.getContent()));

        verify(commentService, times(1)).findAll(anyInt(), any(Sort.class));
    }

    @Test
    @WithMockUser
    void whenFindAllWithPagination_thenReturnPaginatedListOfComments() throws Exception {
        ResponsePage<CommentDto> responsePage = new ResponsePage<>();
        responsePage.setContent(List.of(commentDto));
        responsePage.setPage(1);
        responsePage.setPageSize(1);
        responsePage.setTotalElements(2L);
        responsePage.setTotalPages(2);

        when(commentService.findAll(anyInt(), any(Sort.class))).thenReturn(responsePage);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL).param("page", "1").param("size", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fullName").value(commentDto.getFullName()))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.pageSize").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2));

        verify(commentService, times(1)).findAll(anyInt(), any(Sort.class));
    }

    @Test
    @WithMockUser
    void whenFindAllWithInvalidSort_thenReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL).param("sort", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void whenFindAllEmpty_thenReturnEmptyList() throws Exception {
        ResponsePage<CommentDto> responsePage = new ResponsePage<>();
        responsePage.setContent(List.of());
        responsePage.setPage(0);
        responsePage.setPageSize(2);
        responsePage.setTotalElements(0L);
        responsePage.setTotalPages(0);

        when(commentService.findAll(anyInt(), any(Sort.class))).thenReturn(responsePage);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));

        verify(commentService, times(1)).findAll(anyInt(), any(Sort.class));
    }

    @Test
    @WithMockUser
    void givenCommentId_whenFindById_thenReturnComment() throws Exception {
        when(commentService.findById(COMMENT_ID)).thenReturn(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/" + COMMENT_ID))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value(commentDto.getFullName()))
                .andExpect(jsonPath("$.content").value(commentDto.getContent()));

        verify(commentService, times(1)).findById(COMMENT_ID);
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void givenValidComment_whenCreate_thenReturnCreatedComment() throws Exception {
        commentDto.setArticleId(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(customer.getEmail());
        when(customerService.findByEmail(customer.getEmail())).thenReturn(customer);

        when(commentService.create(any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fullName").value(commentDto.getFullName()))
                .andExpect(jsonPath("$.content").value(commentDto.getContent()));

        verify(commentService, times(1)).create(any(CommentDto.class));
    }

    @Test
    @WithMockUser(roles = "EDITOR", username = "test@gmail.com")
    void givenValidComment_whenUpdate_thenReturnUpdatedComment() throws Exception {
        commentDto.setArticleId(1L);

        customer.setFirstName(commentDto.getFullName().split(" ")[0]);
        customer.setLastName(commentDto.getFullName().split(" ")[1]);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(customer.getEmail());
        when(customerService.findByEmail(customer.getEmail())).thenReturn(customer);

        when(commentService.update(eq(COMMENT_ID), any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + COMMENT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fullName").value(commentDto.getFullName()))
                .andExpect(jsonPath("$.content").value(commentDto.getContent()));

        verify(commentService, times(1)).update(COMMENT_ID, commentDto);
    }

    @Test
    @WithMockUser(roles = "EDITOR")
    void givenCommentId_whenDelete_thenReturnStatusOk() throws Exception {
        doNothing().when(commentService).deleteById(COMMENT_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + COMMENT_ID))
                .andExpect(status().isOk());

        verify(commentService, times(1)).deleteById(COMMENT_ID);
    }
}
