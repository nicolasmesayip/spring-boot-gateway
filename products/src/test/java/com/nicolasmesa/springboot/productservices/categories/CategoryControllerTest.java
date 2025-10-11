package com.nicolasmesa.springboot.productservices.categories;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeTry;
import org.hamcrest.Matchers;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
    private ObjectMapper objectMapper;

    @BeforeTry
    void setup() {
        // Build the controller manually — no Spring context
        categoryService = Mockito.mock(CategoryService.class);
        categoryMapper = new CategoryMapperImpl();
        CategoryController controller = new CategoryController(categoryMapper, categoryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new CategoryExceptionHandler()).build();
        objectMapper = new ObjectMapper();
    }

    @Property(tries = 5)
    public void getAllCategories(@ForAll("genListOfCategories") List<Category> listOfCategories) throws Exception {
        Mockito.when(categoryService.getAllCategories()).thenReturn(listOfCategories);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/"));
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist());

        for (int i = 0; i < listOfCategories.size(); i++) {
            Category category = listOfCategories.get(i);

            resultActions
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].name").value(category.getName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].slug").value(category.getSlug()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].description").value(category.getDescription()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].isActive").value(category.getIsActive()));
        }
    }

    @Property(tries = 5)
    public void getAllCategoriesWhenNoCategories() throws Exception {
        Mockito.when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/"));
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @Property(tries = 5)
    public void getCategoryBySlug(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        Category category = categoryMapper.toEntity(dto);

        Mockito.when(categoryService.getCategoryBySlug(dto.slug())).thenReturn(category);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/{slug}", dto.slug()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verifyCategory(resultActions, category);
    }

    @Property(tries = 5)
    public void attemptCategoryBySlug(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        Mockito.when(categoryService.getCategoryBySlug(dto.slug())).thenThrow(new CategoryNotFoundException(dto.slug()));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/{slug}", dto.slug()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        List<String> errors = List.of("Category not found with name: " + dto.slug());
        verifyErrors(resultActions, errors);
    }

    @Property(tries = 5)
    public void createCategory(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        Category category = categoryMapper.toEntity(dto);
        Mockito.when(categoryService.createCategory(category)).thenReturn(category);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/categories/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        verifyCategory(resultActions, category);
    }

    @Property(tries = 5)
    public void failedCreatingCategoryInvalidData(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        CategoryDto invalidDto = new CategoryDto("", "", "", dto.isActive());

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/categories/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        List<String> errors = List.of("The Category name is required", "The Category description is required");
        verifyErrors(resultActions, errors);
    }

    @Property(tries = 5)
    public void failedCreatingDuplicatedCategory(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        Category category = categoryMapper.toEntity(dto);
        Mockito.when(categoryService.createCategory(category)).thenThrow(new CategoryAlreadyExistsException(dto.slug()));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/categories/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        List<String> errors = List.of("Category already exists: " + dto.slug());
        verifyErrors(resultActions, errors);
    }

    @Property(tries = 5)
    public void failedCreatingCategoryInvalidSlug(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        CategoryDto invalidDto = new CategoryDto(dto.name(), dto.slug().toUpperCase(), dto.description(), dto.isActive());

        Category category = categoryMapper.toEntity(invalidDto);
        Mockito.when(categoryService.createCategory(category)).thenThrow(new InvalidSlugException(dto.slug()));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/categories/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        List<String> errors = List.of("Slug '" + invalidDto.slug() + "' is not valid. Use only lowercase letters, numbers, and hyphens.");
        verifyErrors(resultActions, errors);
    }

    @Property(tries = 5)
    public void updateDescription(@ForAll("genCategoryDto") CategoryDto dto, @ForAll("genDescription") String newDescription) throws Exception {
        Category category = categoryMapper.toEntity(dto);
        category.setDescription(newDescription);

        Mockito.when(categoryService.updateDescription(dto.slug(), newDescription)).thenReturn(category);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch("/api/categories/description/{slug}", dto.slug())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateCategoryDescriptionDto(newDescription))))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verifyCategory(resultActions, category);
    }

    @Property(tries = 5)
    public void failedToUpdateDescription(@ForAll("genCategoryDto") CategoryDto dto, @ForAll("genDescription") String newDescription) throws Exception {
        Mockito.when(categoryService.updateDescription(dto.slug(), newDescription)).thenThrow(new CategoryNotFoundException(dto.slug()));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch("/api/categories/description/{slug}", dto.slug())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateCategoryDescriptionDto(newDescription))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        List<String> errors = List.of("Category not found with name: " + dto.slug());
        verifyErrors(resultActions, errors);
    }

    @Property(tries = 5)
    public void activateCategory(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        Category category = categoryMapper.toEntity(dto);
        category.setIsActive(true);

        Mockito.when(categoryService.changeStatus(dto.slug(), true)).thenReturn(category);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch("/api/categories/activate/{slug}", dto.slug()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verifyCategory(resultActions, category);
    }

    @Property(tries = 5)
    public void failedToActivateCategory(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        Mockito.when(categoryService.changeStatus(dto.slug(), true)).thenThrow(new CategoryNotFoundException(dto.slug()));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch("/api/categories/activate/{slug}", dto.slug()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        List<String> errors = List.of("Category not found with name: " + dto.slug());
        verifyErrors(resultActions, errors);
    }

    @Property(tries = 5)
    public void deactivateCategory(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        Category category = categoryMapper.toEntity(dto);
        category.setIsActive(false);

        Mockito.when(categoryService.changeStatus(dto.slug(), false)).thenReturn(category);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch("/api/categories/deactivate/{slug}", dto.slug()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verifyCategory(resultActions, category);
    }

    @Property(tries = 5)
    public void failedToDeactivateCategory(@ForAll("genCategoryDto") CategoryDto dto) throws Exception {
        Mockito.when(categoryService.changeStatus(dto.slug(), false)).thenThrow(new CategoryNotFoundException(dto.slug()));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch("/api/categories/deactivate/{slug}", dto.slug()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        List<String> errors = List.of("Category not found with name: " + dto.slug());
        verifyErrors(resultActions, errors);
    }

    public void verifyCategory(ResultActions resultActions, Category category) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(category.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.slug").value(category.getSlug()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").value(category.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.isActive").value(category.getIsActive()));
    }

    public void verifyErrors(ResultActions resultActions, List<String> errors) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").value(Matchers.hasSize(errors.size())));

        for (String error : errors) {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.errors").value(Matchers.hasItem(error)));
        }
    }
}
