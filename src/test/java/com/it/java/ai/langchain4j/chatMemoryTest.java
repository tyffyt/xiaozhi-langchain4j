package com.it.java.ai.langchain4j;

import com.it.java.ai.langchain4j.assistant.MemoryChatAssistant;
import com.it.java.ai.langchain4j.assistant.SeparateChatAssistant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class chatMemoryTest {
    @Autowired
    private MemoryChatAssistant memoryChatAssistant;
    @Test
    public void test() {
        System.out.println(memoryChatAssistant.chat("你好，我叫张三"));
        System.out.println(memoryChatAssistant.chat("我是谁？"));

    }

    @Autowired
    private SeparateChatAssistant separateChatAssistant;
    @Test
    public void test2() {
        System.out.println(separateChatAssistant.chat(1,"你好，我叫张三"));
        System.out.println(separateChatAssistant.chat(2,"我是谁？"));

    }

}
