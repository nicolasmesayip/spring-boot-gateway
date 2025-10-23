package com.nicolasmesa.springboot.productservices.categories;

import com.nicolasmesa.springboot.common.exceptions.InvalidSlugException;
import com.nicolasmesa.springboot.productservices.categories.controller.CategoryController;
import com.nicolasmesa.springboot.productservices.categories.dto.CategoryDto;
import com.nicolasmesa.springboot.productservices.categories.dto.UpdateCategoryDescriptionDto;
import com.nicolasmesa.springboot.productservices.categories.entity.Category;
import com.nicolasmesa.springboot.productservices.categories.exception.CategoryAlreadyExistsException;
import com.nicolasmesa.springboot.productservices.categories.exception.CategoryExceptionHandler;
import com.nicolasmesa.springboot.productservices.categories.exception.CategoryNotFoundException;
import com.nicolasmesa.springboot.productservices.categories.mapper.CategoryMapper;
import com.nicolasmesa.springboot.productservices.categories.mapper.CategoryMapperImpl;
import com.nicolasmesa.springboot.productservices.categories.service.CategoryService;
import com.nicolasmesa.springboot.productservices.testcommon.RequestBuilder;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeTry;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest extends CategoryGenerator {
    private MockMvc mockMvc;
    private CategoryMapper categoryMapper;
    private CategoryService categoryService;
    private CategoryControllerVerification categoryControllerVerification;

    @BeforeTry
    void setup() {
        categoryService = Mockito.mock(CategoryService.class);
        categoryMapper = new CategoryMapperImpl();
        CategoryController controller = new CategoryController(categoryMapper, categoryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new CategoryExceptionHandler()).build();
        categoryControllerVerification = new CategoryControllerVerification();
    }

    @Property(tries = 5)
    public void getAllCategories(@ForAll("genListOfCategories") List<Category> listOfCategories) throws Exception {
        Mockito.when(categoryService.getAllCategories()).thenReturn(listOfCategories);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/categories/"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        categoryControllerVerification.verifyData(resultActions, listOfCategories);
    }

    @Property(tries = 5)
    public void getAllCategoriesWhenNoCategories() throws Exception {
        Mockito.when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());

        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/categories/"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        categoryControllerVerification.verifyNoContent(resultActions);
    }

    @Property(tries = 5)
    public void getCategoryBySlug(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        Category category = categoryMapper.toEntity(dto);

        Mockito.when(categoryService.getCategoryBySlug(dto.slug())).thenReturn(category);
        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/categories/{slug}", dto.slug()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        categoryControllerVerification.verifyData(resultActions, category);
    }

    @Property(tries = 5)
    public void attemptCategoryBySlug(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        Mockito.when(categoryService.getCategoryBySlug(dto.slug())).thenThrow(new CategoryNotFoundException(dto.slug()));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/categories/{slug}", dto.slug()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        categoryControllerVerification.verifyErrors(resultActions, List.of("Category not found with name: " + dto.slug()));
    }

    @Property(tries = 5)
    public void createCategory(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        Category category = categoryMapper.toEntity(dto);
        Mockito.when(categoryService.createCategory(category)).thenReturn(category);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/categories/").body(dto))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        categoryControllerVerification.verifyData(resultActions, category);
    }

    @Property(tries = 5)
    public void failedCreatingCategoryInvalidData(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        CategoryDto invalidDto = new CategoryDto("", "", "", dto.isActive());

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/categories/").body(invalidDto))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        categoryControllerVerification.verifyErrors(resultActions, List.of("The Category name is required", "The Category description is required"));
    }

    @Property(tries = 5)
    public void failedCreatingDuplicatedCategory(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        Category category = categoryMapper.toEntity(dto);
        Mockito.when(categoryService.createCategory(category)).thenThrow(new CategoryAlreadyExistsException(dto.slug()));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/categories/").body(dto))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        categoryControllerVerification.verifyErrors(resultActions, List.of("Category already exists: " + dto.slug()));
    }

    @Property(tries = 5)
    public void failedCreatingCategoryInvalidSlug(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        CategoryDto invalidDto = new CategoryDto(dto.name(), dto.slug().toUpperCase(), dto.description(), dto.isActive());

        Category category = categoryMapper.toEntity(invalidDto);
        Mockito.when(categoryService.createCategory(category)).thenThrow(new InvalidSlugException(dto.slug()));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/categories/").body(invalidDto))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        categoryControllerVerification.verifyErrors(resultActions, List.of("Slug '" + invalidDto.slug() + "' is not valid. Use only lowercase letters, numbers, and hyphens."));
    }

    @Property(tries = 5)
    public void updateDescription(@ForAll("genCategoryDto") CategoryDto dto, @ForAll("genDescription") String newDescription) throws Exception {
        Category category = categoryMapper.toEntity(dto);
        category.setDescription(newDescription);

        Mockito.when(categoryService.updateDescription(dto.slug(), newDescription)).thenReturn(category);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.patch("/api/categories/description/{slug}", dto.slug()).body(new UpdateCategoryDescriptionDto(newDescription)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        categoryControllerVerification.verifyData(resultActions, category);
    }

    @Property(tries = 5)
    public void failedToUpdateDescription(@ForAll("genCategoryDto") CategoryDto dto, @ForAll("genDescription") String newDescription) throws Exception {
        Mockito.when(categoryService.updateDescription(dto.slug(), newDescription)).thenThrow(new CategoryNotFoundException(dto.slug()));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.patch("/api/categories/description/{slug}", dto.slug()).body(new UpdateCategoryDescriptionDto(newDescription)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        categoryControllerVerification.verifyErrors(resultActions, List.of("Category not found with name: " + dto.slug()));
    }

    @Property(tries = 5)
    public void activateCategory(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        Category category = categoryMapper.toEntity(dto);
        category.setIsActive(true);

        Mockito.when(categoryService.changeStatus(dto.slug(), true)).thenReturn(category);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.patch("/api/categories/activate/{slug}", dto.slug()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        categoryControllerVerification.verifyData(resultActions, category);
    }

    @Property(tries = 5)
    public void failedToActivateCategory(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        Mockito.when(categoryService.changeStatus(dto.slug(), true)).thenThrow(new CategoryNotFoundException(dto.slug()));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.patch("/api/categories/activate/{slug}", dto.slug()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        categoryControllerVerification.verifyErrors(resultActions, List.of("Category not found with name: " + dto.slug()));
    }

    @Property(tries = 5)
    public void deactivateCategory(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        Category category = categoryMapper.toEntity(dto);
        category.setIsActive(false);

        Mockito.when(categoryService.changeStatus(dto.slug(), false)).thenReturn(category);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.patch("/api/categories/deactivate/{slug}", dto.slug()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        categoryControllerVerification.verifyData(resultActions, category);
    }

    @Property(tries = 5)
    public void failedToDeactivateCategory(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        Mockito.when(categoryService.changeStatus(dto.slug(), false)).thenThrow(new CategoryNotFoundException(dto.slug()));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.patch("/api/categories/deactivate/{slug}", dto.slug()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        categoryControllerVerification.verifyErrors(resultActions, List.of("Category not found with name: " + dto.slug()));
    }
}
