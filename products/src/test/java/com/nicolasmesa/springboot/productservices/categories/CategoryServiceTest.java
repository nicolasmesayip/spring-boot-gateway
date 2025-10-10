package com.nicolasmesa.springboot.productservices.categories;

import com.nicolasmesa.springboot.common.exceptions.SlugAlreadyExistsException;
import com.nicolasmesa.springboot.productservices.categories.entity.Category;
import com.nicolasmesa.springboot.productservices.categories.exception.CategoryAlreadyExistsException;
import com.nicolasmesa.springboot.productservices.categories.exception.CategoryNotFoundException;
import com.nicolasmesa.springboot.productservices.categories.repository.CategoryRepository;
import com.nicolasmesa.springboot.productservices.categories.service.CategoryService;
import com.nicolasmesa.springboot.productservices.categories.service.CategoryServiceImpl;
import com.nicolasmesa.springboot.productservices.common.repository.SlugRepository;
import com.nicolasmesa.springboot.productservices.common.service.SlugService;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeTry;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CategoryServiceTest extends CategoryGenerator {

    private SlugRepository slugRepository;
    private SlugService slugService;
    private CategoryRepository categoryRepository;
    private CategoryService categoryService;

    Long categoryId = 1L;
    LocalDateTime creationDateTime = LocalDateTime.now();

    @BeforeTry
    void setup() {
        categoryRepository = Mockito.mock(CategoryRepository.class);
        slugRepository = Mockito.mock(SlugRepository.class);
        slugService = Mockito.mock(SlugService.class);
        categoryService = new CategoryServiceImpl(categoryRepository, slugService);
    }

    @Property
    public void createProduct(@ForAll("genCategory") Category category) {
        Mockito.when(categoryRepository.findByName(category.getName())).thenReturn(Optional.empty());
        Mockito.when(slugService.verifySlug(category.getName(), category.getSlug(), slugRepository)).thenReturn(category.getSlug());
        Mockito.when(categoryRepository.save(category)).thenAnswer(invocation -> {
            Category c = invocation.getArgument(0);
            c.setId(categoryId);
            c.setCreatedAt(creationDateTime);
            c.setUpdatedAt(creationDateTime);
            return c;
        });

        Category result = categoryService.createCategory(category);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(category);
        verifyCategory(category, result);
    }

    @Property
    public void creatingDuplicatedCategory(@ForAll("genCategory") Category category) {
        Mockito.when(categoryRepository.findByName(category.getName())).thenReturn(Optional.of(category));
        assertThrows(CategoryAlreadyExistsException.class, () -> {
            categoryService.createCategory(category);
        });

        Mockito.verify(categoryRepository, Mockito.times(0)).save(category);
        Mockito.verifyNoInteractions(slugService);
        Mockito.verifyNoInteractions(slugRepository);
    }

    @Property
    public void creatingDuplicatedSlug(@ForAll("genCategory") Category category) {
        categoryService = new CategoryServiceImpl(categoryRepository, new SlugService());

        Mockito.when(categoryRepository.findByName(category.getName())).thenReturn(Optional.empty());
        Mockito.when(categoryRepository.existsBySlug(category.getSlug())).thenReturn(true);

        assertThrows(SlugAlreadyExistsException.class, () -> {
            categoryService.createCategory(category);
        });

        Mockito.verify(categoryRepository, Mockito.times(0)).save(category);
        Mockito.verifyNoInteractions(slugRepository);
    }

    @Property
    public void updatingDescription(@ForAll("genCategory") Category category, @ForAll("genDescription") String newDescription) {
        String originalDescription = category.getDescription();
        category.setId(categoryId);
        category.setCreatedAt(creationDateTime);
        category.setUpdatedAt(creationDateTime);

        Mockito.when(categoryRepository.findBySlug(category.getSlug())).thenReturn(Optional.of(category));
        category.setDescription(newDescription);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.updateDescription(category.getSlug(), newDescription);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(category);

        assertNotEquals(originalDescription, result.getDescription());
        verifyCategory(category, result);
    }

    @Property
    public void failedUpdatingDescription(@ForAll("genCategory") Category category, @ForAll("genDescription") String newDescription) {
        Mockito.when(categoryRepository.findBySlug(category.getSlug())).thenReturn(Optional.empty());
        assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.updateDescription(category.getSlug(), newDescription);
        });

        Mockito.verify(categoryRepository, Mockito.times(0)).save(category);
    }

    @Property
    public void updatingStatus(@ForAll("genCategory") Category category) {
        Boolean originalStatus = category.getIsActive();
        Boolean newStatus = !category.getIsActive();

        category.setId(categoryId);
        category.setCreatedAt(creationDateTime);
        category.setUpdatedAt(creationDateTime);

        Mockito.when(categoryRepository.findBySlug(category.getSlug())).thenReturn(Optional.of(category));
        category.setIsActive(newStatus);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.changeStatus(category.getSlug(), newStatus);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(category);

        assertNotEquals(originalStatus, result.getIsActive());
        verifyCategory(category, result);
    }

    @Property
    public void failedChangingStatus(@ForAll("genCategory") Category category) {
        Boolean newStatus = !category.getIsActive();

        Mockito.when(categoryRepository.findBySlug(category.getSlug())).thenReturn(Optional.empty());
        assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.changeStatus(category.getSlug(), newStatus);
        });

        Mockito.verify(categoryRepository, Mockito.times(0)).save(category);
    }

    @Property
    public void getAllCategories(@ForAll("genListOfCategories") List<Category> categories) {
        Mockito.when(categoryRepository.findAll()).thenReturn(categories);
        List<Category> results = categoryService.getAllCategories();

        for (int i = 0; i < categories.size(); i++) {
            assertEquals(categories.get(i).getName(), results.get(i).getName());
            assertEquals(categories.get(i).getSlug(), results.get(i).getSlug());
            assertEquals(categories.get(i).getDescription(), results.get(i).getDescription());
            assertEquals(categories.get(i).getIsActive(), results.get(i).getIsActive());
        }
    }

    @Property
    public void getCategoryBySlug(@ForAll("genCategory") Category category) {
        category.setId(categoryId);
        category.setCreatedAt(creationDateTime);
        category.setUpdatedAt(creationDateTime);

        Mockito.when(categoryRepository.findBySlug(category.getSlug())).thenReturn(Optional.of(category));
        Category expected = categoryService.getCategoryBySlug(category.getSlug());

        verifyCategory(expected, category);
    }

    @Property
    public void failedGettingCategoryBySlug(@ForAll("genCategory") Category category) {
        Mockito.when(categoryRepository.findBySlug(category.getSlug())).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.getCategoryBySlug(category.getSlug());
        });
    }

    public void verifyCategory(Category expected, Category result) {
        assertEquals(categoryId, result.getId());
        assertEquals(expected.getName(), result.getName());
        assertEquals(expected.getSlug(), result.getSlug());
        assertEquals(expected.getDescription(), result.getDescription());
        assertEquals(expected.getIsActive(), result.getIsActive());
        assertEquals(creationDateTime, result.getCreatedAt());
        assertEquals(creationDateTime, result.getUpdatedAt());
    }
}
