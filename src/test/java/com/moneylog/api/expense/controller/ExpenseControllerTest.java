package com.moneylog.api.expense.controller;

import static com.moneylog.api.exception.domain.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneylog.api.category.domain.Category;
import com.moneylog.api.category.repository.CategoryRepository;
import com.moneylog.api.exception.domain.CustomException;
import com.moneylog.api.expense.domain.Expense;
import com.moneylog.api.expense.repository.ExpenseRepository;
import com.moneylog.api.member.domain.Member;
import com.moneylog.api.member.domain.Role;
import com.moneylog.api.member.repository.MemberRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class ExpenseControllerTest {

    private final String requestUri = "/api/expenses";
    private final String email1 = "test1@test.com";
    private final String email2 = "test2@test.com";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Expense expense;
    private Member member;

    @BeforeEach
    void setUp() {
        saveTestData();
    }

    @Test
    @WithUserDetails(value = email1, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("지출을 등록할 수 있다.")
    void createExpense() throws Exception {
        //given
        Map<String, Object> params = new HashMap<>();
        params.put("expendedAt", "2023-11-01T12:30");
        params.put("expenseAmount", 10000L);
        params.put("categoryId", 1L);
        params.put("memo", "");
        params.put("isExcludeTotal", true);

        //when then
        mockMvc.perform(post(requestUri)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails(value = email1, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("잘못된 지출일시로 지출을 등록할 수 없다.")
    void createExpense_invalidExpendedAt() throws Exception {
        //given
        Map<String, Object> params = new HashMap<>();
        params.put("expendedAt", "2023-11-01T12:99");
        params.put("expenseAmount", 10000L);
        params.put("categoryId", 1L);
        params.put("memo", "");
        params.put("isExcludeTotal", true);

        //when then
        mockMvc.perform(post(requestUri)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(value = email1, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("지출일시 없이 지출을 등록할 수 없다.")
    void createExpense_emptyExpendedAt() throws Exception {
        //given
        Map<String, Object> params = new HashMap<>();
        params.put("expenseAmount", 10000L);
        params.put("categoryId", 1L);
        params.put("memo", "");
        params.put("isExcludeTotal", true);

        //when then
        mockMvc.perform(post(requestUri)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(EXPENSE_EXPENDEDAT_EMPTY.getMessage()));
    }

    @Test
    @WithUserDetails(value = email1, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("지출액 없이 지출을 등록할 수 없다.")
    void createExpense_emptyExpenseAmount() throws Exception {
        //given
        Map<String, Object> params = new HashMap<>();
        params.put("expendedAt", "2023-11-01T12:00");
        params.put("categoryId", 1L);
        params.put("memo", "");
        params.put("isExcludeTotal", true);

        //when then
        mockMvc.perform(post(requestUri)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(EXPENSE_EXPENSEAMOUNT_EMPTY.getMessage()));
    }

    @Test
    @WithUserDetails(value = email1, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("잘못된 지출액으로 지출을 등록할 수 없다.")
    void createExpense_invalidExpenseAmount() throws Exception {
        //given
        Map<String, Object> params = new HashMap<>();
        params.put("expendedAt", "2023-11-01T12:00");
        params.put("expenseAmount", -1L);
        params.put("categoryId", 1L);
        params.put("memo", "");
        params.put("isExcludeTotal", true);

        //when then
        mockMvc.perform(post(requestUri)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(EXPENSE_EXPENSEAMOUNT_MINUS.getMessage()));
    }

    @Test
    @WithUserDetails(value = email1, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("카테고리 없이 지출을 등록할 수 없다.")
    void createExpense_emptyCategory() throws Exception {
        //given
        Map<String, Object> params = new HashMap<>();
        params.put("expendedAt", "2023-11-01T12:00");
        params.put("expenseAmount", 10000L);
        params.put("memo", "");
        params.put("isExcludeTotal", true);

        //when then
        mockMvc.perform(post(requestUri)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(CATEGORY_EMPTY.getMessage()));
    }

    @Test
    @WithUserDetails(value = email1, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("합계제외여부 없이 지출을 등록할 수 없다.")
    void createExpense_emptyIsExcludeTotal() throws Exception {
        //given
        Map<String, Object> params = new HashMap<>();
        params.put("expendedAt", "2023-11-01T12:00");
        params.put("expenseAmount", 10000L);
        params.put("categoryId", 1L);
        params.put("memo", "");

        //when then
        mockMvc.perform(post(requestUri)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(EXPENSE_ISEXCLUDETOTAL_EMPTY.getMessage()));
    }

    @Test
    @WithUserDetails(value = email1, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("지출을 수정할 수 있다.")
    void updateExpense() throws Exception {
        //given
        Map<String, Object> params = new HashMap<>();
        params.put("expendedAt", "2023-11-02T13:30");
        params.put("expenseAmount", 20000L);
        params.put("categoryId", 2L);
        params.put("memo", "changed");
        params.put("isExcludeTotal", true);

        //when then
        mockMvc.perform(patch(requestUri + "/{expenseId}", expense.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(expense).isNotNull();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime expendedAt = LocalDateTime.parse((String) params.get("expendedAt"), dateTimeFormatter);
        assertThat(expense.getExpendedAt()).isEqualTo(expendedAt);
        assertThat(expense.getExpenseAmount()).isEqualTo(params.get("expenseAmount"));
        assertThat(expense.getCategory().getId()).isEqualTo(params.get("categoryId"));
        assertThat(expense.getMemo()).isEqualTo(params.get("memo"));
        assertThat(expense.getIsExcludeTotal()).isEqualTo(params.get("isExcludeTotal"));
    }

    @Test
    @WithUserDetails(value = email2, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("권한이 없으면 지출을 수정할 수 없다.")
    void updateExpense_unAuth() throws Exception {
        //when then
        mockMvc.perform(patch(requestUri + "/{expenseId}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new HashMap<>()))
                )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(COMMON_ACCESS_DENIED.getMessage()));
    }

    @Test
    @WithUserDetails(value = email1, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("없는 지출을 수정할 수 없다.")
    void updateExpense_notExistsExpense() throws Exception {
        //when then
        mockMvc.perform(patch(requestUri + "/{expenseId}", 99L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new HashMap<>()))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(EXPENSE_NOT_EXISTS.getMessage()));
    }

    @Test
    @WithUserDetails(value = email1, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("잘못된 지출일시로 지출을 수정할 수 없다.")
    void updateExpense_invalidExpenseAt() throws Exception {
        //given
        Map<String, Object> params = new HashMap<>();
        params.put("expendedAt", "2023-11-02T13:99");

        //when then
        mockMvc.perform(patch(requestUri + "/{expenseId}", expense.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(value = email1, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("잘못된 지출액으로 지출을 수정할 수 없다.")
    void updateExpense_invalidExpenseAmount() throws Exception {
        //given
        Map<String, Object> params = new HashMap<>();
        params.put("expenseAmount", -1L);

        //when then
        mockMvc.perform(patch(requestUri + "/{expenseId}", expense.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(value = email1, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("지출을 상세 조회할 수 있다.")
    void getExpense() throws Exception {
        //when then
        mockMvc.perform(get(requestUri + "/{expenseId}", expense.getId())
                        .accept(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenseId").value(expense.getId()));
    }

    @Test
    @WithUserDetails(value = email2, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("권한이 없으면 지출을 상세 조회할 수 없다.")
    void getExpense_unAuth() throws Exception {
        //when then
        mockMvc.perform(get(requestUri + "/{expenseId}", expense.getId())
                        .accept(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(COMMON_ACCESS_DENIED.getMessage()));
    }

    @Test
    @WithUserDetails(value = email1, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("없는 지출을 상세 조회할 수 없다.")
    void getExpense_notExistsExpense() throws Exception {
        //when then
        mockMvc.perform(get(requestUri + "/{expenseId}", 99L)
                        .accept(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(EXPENSE_NOT_EXISTS.getMessage()));
    }

    @Test
    @WithUserDetails(value = email1, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("지출 목록을 조회할 수 있다.")
    void getExpenses_noFilter() throws Exception {
        //given
        List<Category> categories = categoryRepository.findAll();
        saveExpense(LocalDateTime.of(2023, 10, 10, 12, 0, 0), 10000L, categories.get(1), false);//invalid expendedStartAt
        saveExpense(LocalDateTime.of(2023, 11, 10, 13, 0, 0), 2999L, categories.get(1), false);//invalid expenseAmountMin
        saveExpense(LocalDateTime.of(2023, 11, 20, 12, 0, 0), 100001L, categories.get(1), false);//invalid expenseAmountMax
        saveExpense(LocalDateTime.of(2023, 12, 30, 12, 0, 0), 10000L, categories.get(1), false);//invalid sexpendedEndAt
        saveExpense(LocalDateTime.of(2023, 11, 10, 14, 0, 0), 10000L, categories.get(1), true);
        saveExpense(LocalDateTime.of(2023, 11, 10, 12, 0, 0), 100000L, categories.get(1), false);
        saveExpense(LocalDateTime.of(2023, 11, 15, 12, 0, 0), 10000L, categories.get(1), false);
        saveExpense(LocalDateTime.of(2023, 11, 21, 12, 0, 0), 10000L, categories.get(1), false);
        saveExpense(LocalDateTime.of(2023, 11, 30, 12, 0, 0), 3000L, categories.get(1), false);

        Map<String, Object> params = new HashMap<>();
        params.put("expendedStartAt", "2023-11-01T00:00");
        params.put("expendedEndAt", "2023-11-30T23:59");
        params.put("expenseAmountMin", 3000L);
        params.put("expenseAmountMax", 100000L);
        params.put("categoryId", categories.get(1).getId());

        //when then
        mockMvc.perform(get(requestUri)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(123000))
                .andExpect(jsonPath("$.expenses.length()").value(5))
                .andExpect(jsonPath("$.categoryAmount.length()").value(1));
    }

    @Test
    @WithUserDetails(value = email1, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("지출을 삭제할 수 있다.")
    void deleteExpense() throws Exception {
        //when then
        mockMvc.perform(delete(requestUri + "/{expenseId}", expense.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = email2, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("권한이 없으면 지출을 삭제할 수 없다.")
    void deleteExpense_unAuth() throws Exception {
        //when then
        mockMvc.perform(delete(requestUri + "/{expenseId}", expense.getId()))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(COMMON_ACCESS_DENIED.getMessage()));
    }

    private void saveTestData() {
        member = memberRepository.save(Member.builder()
                .email(email1)
                .password("12345678")
                .role(Role.ROLE_USER)
                .build());
        memberRepository.save(Member.builder()
                .email(email2)
                .password("12345678")
                .role(Role.ROLE_USER)
                .build());

        Category category = categoryRepository.findById(1L)
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_EXISTS));

        expense = saveExpense(LocalDateTime.of(2023, 11, 1, 12, 0, 0), 10000L, category, false);
    }

    private Expense saveExpense(LocalDateTime expendedAt, Long expenseAmount, Category category, Boolean isExcludeTotal) {
        return expenseRepository.save(Expense.builder()
                .member(member)
                .expendedAt(expendedAt)
                .expenseAmount(expenseAmount)
                .category(category)
                .memo("")
                .isExcludeTotal(isExcludeTotal)
                .build());
    }
}
