package com.br.Lojas_SR.Controller;

import com.br.Lojas_SR.Entity.Item;
import com.br.Lojas_SR.Service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ItemService itemService;

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1L);
        item.setQuantidade(2);
        item.setPrecoUnitario(new BigDecimal("50.00"));
    }

    @Test
    void deveAdicionarItemComSucesso() throws Exception {
        ItemController.ItemRequest request = new ItemController.ItemRequest();
        request.setCarrinhoId(1L);
        request.setProdutoId(1L);
        request.setQuantidade(2);

        when(itemService.adicionar(anyLong(), anyLong(), anyInt())).thenReturn(item);

        mockMvc.perform(post("/api/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.quantidade").value(2));

        verify(itemService, times(1)).adicionar(anyLong(), anyLong(), anyInt());
    }

    @Test
    void deveListarItensPorCarrinho() throws Exception {
        List<Item> itens = Arrays.asList(item);
        when(itemService.listarPorCarrinho(1L)).thenReturn(itens);

        mockMvc.perform(get("/api/itens/carrinho/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(itemService, times(1)).listarPorCarrinho(1L);
    }

    @Test
    void deveBuscarItemPorId() throws Exception {
        when(itemService.buscarPorId(1L)).thenReturn(item);

        mockMvc.perform(get("/api/itens/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(itemService, times(1)).buscarPorId(1L);
    }

    @Test
    void deveAtualizarQuantidade() throws Exception {
        ItemController.AtualizarQuantidadeRequest request = new ItemController.AtualizarQuantidadeRequest();
        request.setQuantidade(5);

        when(itemService.atualizarQuantidade(anyLong(), anyInt())).thenReturn(item);

        mockMvc.perform(put("/api/itens/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(itemService, times(1)).atualizarQuantidade(anyLong(), anyInt());
    }

    @Test
    void deveRemoverItem() throws Exception {
        doNothing().when(itemService).remover(1L);

        mockMvc.perform(delete("/api/itens/1"))
                .andExpect(status().isNoContent());

        verify(itemService, times(1)).remover(1L);
    }
}