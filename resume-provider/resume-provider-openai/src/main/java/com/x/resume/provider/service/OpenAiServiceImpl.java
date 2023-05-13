package com.x.resume.provider.service;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.completions.CompletionResponse;
import com.x.resume.api.openai.OpenAiService;
import com.x.resume.common.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

public class OpenAiServiceImpl implements OpenAiService {

    private static final Logger LOG = LoggerFactory.getLogger(OpenAiServiceImpl.class);

    private static final String LOG_PREFIX = "OpenAI服务:";

    @Resource
    private OpenAiClient openAiClient;

    @Override
    public Result<CompletionResponse> getCompletions(String question) {
        CompletionResponse completions = openAiClient.completions(question);
        return Result.success(completions);
    }
}
