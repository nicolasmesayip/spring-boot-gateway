package com.nicolasmesa.springboot.shoppingcart;

import com.nicolasmesa.springboot.common.model.ApiResponse;
import com.nicolasmesa.springboot.shoppingcart.dto.CartDto;
import com.nicolasmesa.springboot.shoppingcart.dto.CartItemDto;
import com.nicolasmesa.springboot.shoppingcart.dto.ProductPricingDto;
import com.nicolasmesa.springboot.shoppingcart.entity.Cart;
import com.nicolasmesa.springboot.shoppingcart.entity.CartItem;
import com.nicolasmesa.springboot.shoppingcart.enums.CartStatus;
import com.nicolasmesa.springboot.shoppingcart.exception.CartItemAlreadyExists;
import com.nicolasmesa.springboot.shoppingcart.exception.CartItemNotFound;
import com.nicolasmesa.springboot.shoppingcart.exception.CartNotFound;
import com.nicolasmesa.springboot.shoppingcart.mapper.CartMapper;
import com.nicolasmesa.springboot.shoppingcart.mapper.CartMapperImpl;
import com.nicolasmesa.springboot.shoppingcart.repository.CartRepository;
import com.nicolasmesa.springboot.shoppingcart.service.CartService;
import com.nicolasmesa.springboot.shoppingcart.service.CartServiceImpl;
import com.nicolasmesa.springboot.shoppingcart.service.ProductPricingService;
import feign.FeignException;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeTry;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class CartServiceTest extends CartGenerator {

    private CartRepository cartRepository;
    private CartMapper cartMapper;
    private ProductPricingService productPricingService;
    private CartService cartService;

    @BeforeTry
    void setup() {
        cartRepository = Mockito.mock(CartRepository.class);
        cartMapper = new CartMapperImpl();
        productPricingService = Mockito.mock(ProductPricingService.class);
        cartService = new CartServiceImpl(cartRepository, cartMapper, productPricingService);
    }

    @Property
    public void getCart(@ForAll("genCartDto") CartDto cartDto) {
        String emailAddress = cartDto.userId();
        Cart cart = cartMapper.toEntity(cartDto);
        setOnCreationValues(cart, 1L);

        Mockito.when(cartRepository.findByUserIdAndStatus(emailAddress, CartStatus.ACTIVE)).thenReturn(Optional.of(cart));
        CartDto result = cartService.getCart(emailAddress);

        verifyCart(cartDto, result);
    }

    @Property
    public void failedGettingCart(@ForAll("genCartDto") CartDto cartDto) {
        String emailAddress = cartDto.userId();

        Mockito.when(cartRepository.findByUserIdAndStatus(emailAddress, CartStatus.ACTIVE)).thenReturn(Optional.empty());
        assertThrows(CartNotFound.class, () -> {
            cartService.getCart(emailAddress);
        });
    }

    @Property
    public void addItem(@ForAll("genCartDto") CartDto cartDto, @ForAll("genProductPricingDto") ProductPricingDto pricingDto) {
        String emailAddress = cartDto.userId();

        Cart cart = cartMapper.toEntity(cartDto);
        CartItemDto cartItemDto = new CartItemDto("test-slug-0", 5);
        CartItem cartItem = cartMapper.toEntity(cartItemDto);

        cartItem.setUnitPrice(pricingDto.price());
        cart.setItems(new ArrayList<>());

        Mockito.when(cartRepository.findByUserIdAndStatus(emailAddress, CartStatus.ACTIVE)).thenReturn(Optional.of(cart));
        Mockito.when(productPricingService.getProductPricing(cartItemDto.productSlug())).thenReturn(new ApiResponse<ProductPricingDto>(true, pricingDto, null));
        Mockito.when(cartRepository.save(cart)).thenReturn(cart);
        CartDto result = cartService.addItem(emailAddress, cartItemDto);

        assertEquals(result.items(), List.of(cartItemDto));
    }

    @Property
    public void addItemShouldCreateCart(@ForAll("genProductPricingDto") ProductPricingDto pricingDto) {
        String emailAddress = genEmailAddress.sample();
        CartItemDto cartItemDto = new CartItemDto("test-slug-0", 5);
        CartItem cartItem = cartMapper.toEntity(cartItemDto);

        Cart savedCart = new Cart();
        savedCart.setStatus(CartStatus.ACTIVE);
        savedCart.setUserId(emailAddress);
        savedCart.setItems(List.of(cartItem));

        cartItem.setUnitPrice(pricingDto.price());

        Mockito.when(cartRepository.findByUserIdAndStatus(emailAddress, CartStatus.ACTIVE)).thenReturn(Optional.empty());
        Mockito.when(productPricingService.getProductPricing(cartItemDto.productSlug())).thenReturn(new ApiResponse<ProductPricingDto>(true, pricingDto, null));
        Mockito.when(cartRepository.save(any(Cart.class))).thenReturn(savedCart);
        CartDto result = cartService.addItem(emailAddress, cartItemDto);

        assertEquals(result.items(), List.of(cartItemDto));
    }

    @Property
    public void failedAddingItemWhenAlreadyAdded(@ForAll("genCartDto") CartDto cartDto, @ForAll("genProductPricingDto") ProductPricingDto pricingDto) {
        String emailAddress = cartDto.userId();

        Cart cart = cartMapper.toEntity(cartDto);
        CartItemDto cartItemDto = new CartItemDto("test-slug-0", 5);
        CartItem cartItem = cartMapper.toEntity(cartItemDto);

        cartItem.setUnitPrice(pricingDto.price());
        cart.setItems(List.of(cartItem));

        Mockito.when(cartRepository.findByUserIdAndStatus(emailAddress, CartStatus.ACTIVE)).thenReturn(Optional.of(cart));
        assertThrows(CartItemAlreadyExists.class, () -> {
            cartService.addItem(emailAddress, cartItemDto);
        });
    }

    @Property
    public void failedAddingInvalidItem(@ForAll("genCartDto") CartDto cartDto) {
        String emailAddress = cartDto.userId();

        Cart cart = cartMapper.toEntity(cartDto);
        CartItemDto cartItemDto = new CartItemDto("test-slug-0", 5);

        cart.setItems(new ArrayList<>());

        Mockito.when(cartRepository.findByUserIdAndStatus(emailAddress, CartStatus.ACTIVE)).thenReturn(Optional.of(cart));
        Mockito.when(productPricingService.getProductPricing(cartItemDto.productSlug())).thenThrow(FeignException.BadRequest.class);

        assertThrows(FeignException.FeignClientException.BadRequest.class, () -> {
            cartService.addItem(emailAddress, cartItemDto);
        });
    }

    @Property
    public void removeItem(@ForAll("genCartDto") CartDto cartDto) {
        String emailAddress = cartDto.userId();

        Cart cart = cartMapper.toEntity(cartDto);
        CartItemDto cartItemToRemove = cartDto.items().get(0);

        Mockito.when(cartRepository.findByUserIdAndStatus(emailAddress, CartStatus.ACTIVE)).thenReturn(Optional.of(cart));
        cartService.removeItem(emailAddress, cartItemToRemove.productSlug());

        Mockito.verify(cartRepository, Mockito.times(1)).save(cart);
    }

    @Property
    public void failedRemovingItem(@ForAll("genCartDto") CartDto cartDto) {
        String emailAddress = cartDto.userId();

        Cart cart = cartMapper.toEntity(cartDto);
        CartItemDto cartItemToRemove = cartDto.items().get(0);

        Mockito.when(cartRepository.findByUserIdAndStatus(emailAddress, CartStatus.ACTIVE)).thenReturn(Optional.empty());
        assertThrows(CartNotFound.class, () -> {
            cartService.removeItem(emailAddress, cartItemToRemove.productSlug());
        });

        Mockito.verify(cartRepository, Mockito.times(0)).save(cart);
    }

    @Property
    public void updateItem(@ForAll("genCartDto") CartDto cartDto) {
        String emailAddress = cartDto.userId();

        Cart cart = cartMapper.toEntity(cartDto);
        CartItemDto cartItemToUpdate = cartDto.items().get(0);

        Mockito.when(cartRepository.findByUserIdAndStatus(emailAddress, CartStatus.ACTIVE)).thenReturn(Optional.of(cart));
        cartService.updateItem(emailAddress, cartItemToUpdate);

        Mockito.verify(cartRepository, Mockito.times(1)).save(cart);
    }

    @Property
    public void failedUpdatingItemCartDoesntExist(@ForAll("genCartDto") CartDto cartDto) {
        String emailAddress = cartDto.userId();

        Cart cart = cartMapper.toEntity(cartDto);
        CartItemDto cartItemToUpdate = cartDto.items().get(0);

        Mockito.when(cartRepository.findByUserIdAndStatus(emailAddress, CartStatus.ACTIVE)).thenReturn(Optional.empty());
        assertThrows(CartNotFound.class, () -> {
            cartService.updateItem(emailAddress, cartItemToUpdate);
        });

        Mockito.verify(cartRepository, Mockito.times(0)).save(cart);
    }

    @Property
    public void failedUpdatingInvalidItem(@ForAll("genCartDto") CartDto cartDto) {
        String emailAddress = cartDto.userId();

        Cart cart = cartMapper.toEntity(cartDto);
        CartItemDto cartItemToUpdate = new CartItemDto("test-slug-0", 5);

        Mockito.when(cartRepository.findByUserIdAndStatus(emailAddress, CartStatus.ACTIVE)).thenReturn(Optional.of(cart));
        assertThrows(CartItemNotFound.class, () -> {
            cartService.updateItem(emailAddress, cartItemToUpdate);
        });

        Mockito.verify(cartRepository, Mockito.times(0)).save(cart);
    }

    @Property
    public void clearCart(@ForAll("genCartDto") CartDto cartDto) {
        String emailAddress = cartDto.userId();

        Cart cart = cartMapper.toEntity(cartDto);
        assertNotEquals(0, cart.getItems().size());
        Mockito.when(cartRepository.findByUserIdAndStatus(emailAddress, CartStatus.ACTIVE)).thenReturn(Optional.of(cart));
        cartService.clearCart(emailAddress);
        assertEquals(0, cart.getItems().size());
    }

    @Property
    public void failedClearingCart(@ForAll("genCartDto") CartDto cartDto) {
        String emailAddress = cartDto.userId();

        Mockito.when(cartRepository.findByUserIdAndStatus(emailAddress, CartStatus.ACTIVE)).thenReturn(Optional.empty());
        assertThrows(CartNotFound.class, () -> {
            cartService.clearCart(emailAddress);
        });
    }

    public void verifyCart(CartDto expected, CartDto results) {
        assertEquals(expected.currency(), results.currency());
        assertEquals(expected.userId(), results.userId());
        assertEquals(expected.items(), results.items());
    }

    public void setOnCreationValues(Cart cart, Long id) {
        LocalDateTime now = LocalDateTime.now();

        cart.setId(id);
        cart.setCreatedAt(now);
        cart.setUpdatedAt(now);
    }
}
