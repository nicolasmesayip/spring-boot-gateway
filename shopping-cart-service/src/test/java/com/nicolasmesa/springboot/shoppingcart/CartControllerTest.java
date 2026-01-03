package com.nicolasmesa.springboot.shoppingcart;

import com.nicolasmesa.springboot.shoppingcart.controller.CartController;
import com.nicolasmesa.springboot.shoppingcart.dto.CartDto;
import com.nicolasmesa.springboot.shoppingcart.dto.CartItemDto;
import com.nicolasmesa.springboot.shoppingcart.exception.CartExceptionHandler;
import com.nicolasmesa.springboot.shoppingcart.exception.CartItemAlreadyExists;
import com.nicolasmesa.springboot.shoppingcart.exception.CartItemNotFound;
import com.nicolasmesa.springboot.shoppingcart.exception.CartNotFound;
import com.nicolasmesa.springboot.shoppingcart.service.CartService;
import com.nicolasmesa.springboot.testcommon.RequestBuilder;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeTry;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

public class CartControllerTest extends CartGenerator {
    private MockMvc mockMvc;
    private CartService cartService;
    private CartControllerVerification cartControllerVerification;

    @BeforeTry
    void setup() {
        cartService = Mockito.mock(CartService.class);
        CartController cartController = new CartController(cartService);
        cartControllerVerification = new CartControllerVerification();
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).setControllerAdvice(CartExceptionHandler.class).build();
    }

    @Property
    public void getCart(@ForAll("genCartDto") CartDto cartDto) throws Exception {
        Mockito.when(cartService.getCart(cartDto.userId())).thenReturn(cartDto);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/cart")
                        .header("X-GATEWAY-EMAIL", cartDto.userId()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        cartControllerVerification.verifyData(resultActions, cartDto);
    }

    @Property
    public void failedGettingCart(@ForAll("genCartDto") CartDto cartDto) throws Exception {
        Mockito.when(cartService.getCart(cartDto.userId())).thenThrow(new CartNotFound(cartDto.userId()));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/cart")
                .header("X-GATEWAY-EMAIL", cartDto.userId()));

        cartControllerVerification.verifyErrors(resultActions, List.of("Cart not found for user: " + cartDto.userId()));
    }

    @Property
    public void clearCart(@ForAll("genCartDto") CartDto cartDto) throws Exception {
        Mockito.doNothing().when(cartService).clearCart(cartDto.userId());

        ResultActions resultActions = mockMvc.perform(RequestBuilder.delete("/api/cart")
                .header("X-GATEWAY-EMAIL", cartDto.userId()));

        cartControllerVerification.verifyNoContent(resultActions);
    }

    @Property
    public void failedClearingCart(@ForAll("genCartDto") CartDto cartDto) throws Exception {
        Mockito.doThrow(new CartNotFound(cartDto.userId())).when(cartService).clearCart(cartDto.userId());

        ResultActions resultActions = mockMvc.perform(RequestBuilder.delete("/api/cart")
                .header("X-GATEWAY-EMAIL", cartDto.userId()));

        cartControllerVerification.verifyErrors(resultActions, List.of("Cart not found for user: " + cartDto.userId()));
    }

    @Property
    public void addItem(@ForAll("genCartDto") CartDto cartDto, @ForAll("genCartItemDto") CartItemDto cartItemDto) throws Exception {
        Mockito.when(cartService.addItem(cartDto.userId(), cartItemDto)).thenReturn(cartDto);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/cart/items")
                        .header("X-GATEWAY-EMAIL", cartDto.userId()).body(cartItemDto))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        cartControllerVerification.verifyData(resultActions, cartDto);
    }

    @Property
    public void failedAddingItem(@ForAll("genCartDto") CartDto cartDto, @ForAll("genCartItemDto") CartItemDto cartItemDto) throws Exception {
        Mockito.when(cartService.addItem(cartDto.userId(), cartItemDto)).thenThrow(new CartNotFound(cartDto.userId()));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/cart/items")
                        .header("X-GATEWAY-EMAIL", cartDto.userId()).body(cartItemDto))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        cartControllerVerification.verifyErrors(resultActions, List.of("Cart not found for user: " + cartDto.userId()));
    }

    @Property
    public void failedAddingItemAlreadyAdded(@ForAll("genCartDto") CartDto cartDto, @ForAll("genCartItemDto") CartItemDto cartItemDto) throws Exception {
        Mockito.when(cartService.addItem(cartDto.userId(), cartItemDto)).thenThrow(new CartItemAlreadyExists(cartItemDto.productSlug()));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/cart/items")
                        .header("X-GATEWAY-EMAIL", cartDto.userId()).body(cartItemDto))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        cartControllerVerification.verifyErrors(resultActions, List.of("Cart Item already exists with product slug: " + cartItemDto.productSlug()));
    }

    @Property
    public void failedAddingItemNotFound(@ForAll("genCartDto") CartDto cartDto, @ForAll("genCartItemDto") CartItemDto cartItemDto) throws Exception {
        Mockito.when(cartService.addItem(cartDto.userId(), cartItemDto)).thenThrow(new CartItemNotFound(cartItemDto.productSlug()));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/cart/items")
                        .header("X-GATEWAY-EMAIL", cartDto.userId()).body(cartItemDto))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        cartControllerVerification.verifyErrors(resultActions, List.of("Cart Item not found with product slug: " + cartItemDto.productSlug()));
    }

    @Property
    public void updateItem(@ForAll("genCartDto") CartDto cartDto) throws Exception {
        CartItemDto cartItemDto = cartDto.items().get(0);
        CartItemDto updatedCartItem = new CartItemDto(cartItemDto.productSlug(), cartItemDto.quantity() + 5);

        Mockito.doNothing().when(cartService).updateItem(cartDto.userId(), updatedCartItem);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.put("/api/cart/items/{productSlug}", cartItemDto.productSlug())
                .header("X-GATEWAY-EMAIL", cartDto.userId())
                .body(cartItemDto).body(updatedCartItem.quantity()));

        cartControllerVerification.verifyNoContent(resultActions);
    }

    @Property
    public void failedUpdatingItem(@ForAll("genCartDto") CartDto cartDto) throws Exception {
        CartItemDto cartItemDto = cartDto.items().get(0);
        CartItemDto updatedCartItem = new CartItemDto(cartItemDto.productSlug(), cartItemDto.quantity() + 5);

        Mockito.doThrow(new CartNotFound(cartDto.userId())).when(cartService).updateItem(cartDto.userId(), updatedCartItem);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.put("/api/cart/items/{productSlug}", cartItemDto.productSlug())
                .header("X-GATEWAY-EMAIL", cartDto.userId())
                .body(cartItemDto).body(updatedCartItem.quantity()));

        cartControllerVerification.verifyErrors(resultActions, List.of("Cart not found for user: " + cartDto.userId()));
    }

    @Property
    public void removeItem(@ForAll("genCartDto") CartDto cartDto) throws Exception {
        CartItemDto cartItemDto = cartDto.items().get(0);

        Mockito.doNothing().when(cartService).removeItem(cartDto.userId(), cartItemDto.productSlug());

        ResultActions resultActions = mockMvc.perform(RequestBuilder.delete("/api/cart/items/{productSlug}", cartItemDto.productSlug())
                .header("X-GATEWAY-EMAIL", cartDto.userId())
                .body(cartItemDto.quantity()));

        cartControllerVerification.verifyNoContent(resultActions);
    }

    @Property
    public void failedRemovingItem(@ForAll("genCartDto") CartDto cartDto) throws Exception {
        CartItemDto cartItemDto = cartDto.items().get(0);

        Mockito.doThrow(new CartNotFound(cartDto.userId())).when(cartService).removeItem(cartDto.userId(), cartItemDto.productSlug());

        ResultActions resultActions = mockMvc.perform(RequestBuilder.delete("/api/cart/items/{productSlug}", cartItemDto.productSlug())
                .header("X-GATEWAY-EMAIL", cartDto.userId())
                .body(cartItemDto.quantity()));

        cartControllerVerification.verifyErrors(resultActions, List.of("Cart not found for user: " + cartDto.userId()));

    }
}
