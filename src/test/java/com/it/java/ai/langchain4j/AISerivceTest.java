package com.it.java.ai.langchain4j;

import com.it.java.ai.langchain4j.assistant.Assistant;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AISerivceTest {

    @Autowired
    private QwenChatModel qwenChatModel;
    @Test
    public void test(){
        Assistant assistant = AiServices.create(Assistant.class, qwenChatModel);
        System.out.println(assistant.chat("你是谁"));
    }

    @Autowired
    private Assistant assistant;
    @Test
    public void test2(){
        System.out.println(assistant.chat("你是谁"));
    }


}
