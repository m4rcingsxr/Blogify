package com.blogify.controller;


import com.blogify.entity.Customer;
import com.blogify.payload.CommentDto;
import com.blogify.service.CommentService;
import com.blogify.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    private static final String BASE_URL = "/api/comments";
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

        when(commentService.findAll()).thenReturn(List.of(commentDto, commentDto2));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].fullName").value(commentDto.getFullName()))
                .andExpect(jsonPath("$[0].content").value(commentDto.getContent()));

        verify(commentService, times(1)).findAll();
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
    @WithMockUser(username = "test@gmail.com")
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
    @WithMockUser
    void givenCommentId_whenDelete_thenReturnStatusOk() throws Exception {
        doNothing().when(commentService).deleteById(COMMENT_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + COMMENT_ID))
                .andExpect(status().isOk());

        verify(commentService, times(1)).deleteById(COMMENT_ID);
    }
}
