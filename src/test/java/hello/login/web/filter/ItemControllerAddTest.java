package hello.login.web.filter;

import hello.login.config.WebConfig;
import hello.login.domain.item.Item;
import hello.login.domain.item.ItemRepository;
import hello.login.web.item.ItemController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemController.class})
@Import(value = {WebConfig.class, ItemRepository.class})
@AutoConfigureMockMvc
public class ItemControllerAddTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void itemsAccessNotAllowTest() throws Exception {
        ResultActions perform = mvc.perform(get("/items"));

        perform.andDo(print())
                .andExpect(redirectedUrl("/login?redirectURL=/items"));
    }

    @Test
    void itemsAddAccessNotAllowTest() throws Exception {
        //given
        Item saveItem = new Item("item1", 1000, 10);
        saveItem.setId(1L);
        //when
        ResultActions perform = mvc.perform(post("/items/add")
                .param("itemName", "item1")
                .param("price", "1000")
                .param("quantity", "10")
                .param("id", "1")
        );
        //then
        perform.andDo(print())
                .andExpect(redirectedUrl("/login?redirectURL=/items/add"));
    }
}
