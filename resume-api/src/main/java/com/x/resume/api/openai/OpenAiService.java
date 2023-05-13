package com.x.resume.api.openai;

import com.unfbx.chatgpt.entity.completions.CompletionResponse;
import com.x.resume.common.model.Result;
import com.x.resume.model.domain.user.UserDO;

/**
 * OpenAi 服务接口
 *
 * @author runxiu.zhao
 * @date 2023-02-20 16:00:00
 */
public interface OpenAiService {

    /**
     *
     * @param question
     * @return Result
     */
    Result<CompletionResponse> getCompletions(String question);
}
