package com.michael.Service;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.project.Project;
import com.michael.MyUtils.DialogUtil;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.TokenCountEstimator;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

public class IdentifyByLLM {

    interface Assistant {
        //        @UserMessage("Extract all the values of <infill>s into a String List object.")
        Results chat(String userMessage);
    }
//    private static final String API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1";
    public static String API_URL = "";
//    private static final String API_URL = "https://www.DMXapi.com/v1/";
//    private static final String ALIYUN_API_KEY = System.getenv("ALIYUN_API_KEY");
    public static String ALIYUN_API_KEY = "";
//    private static final String ALIYUN_API_KEY = System.getenv("OPENAI_API_KEY");
    //        private static final String MODEL_NAME = "deepseek-r1:70b";
//    private static final String MODEL_NAME = "llama3.1:8b";
//    private static final String MODEL_NAME = "qwen2.5-coder:7b";
//    private static final String MODEL_NAME = "deepseek-v3";
    public static String MODEL_NAME = "";
//    private static final String MODEL_NAME = "qwen-coder-turbo";
//    private static final String MODEL_NAME = "qwen2.5-coder-32b-instruct";
//    private static final String MODEL_NAME = "qwen2.5-coder-3b-instruct";
//    private static final String MODEL_NAME = "qwen2.5-coder-14b-instruct";
//    private static final String MODEL_NAME = "gpt-4o-mini-2024-07-18";
//    private static final String MODEL_NAME = "qwen-max";
//        private static final String MODEL_NAME = "qwen2.5-coder:32b";
    private static final double TEMPERATURE = 0.0f;
    private static final double PRESENCE_PENALTY = 0.0f;
    public ChatLanguageModel model = null;
    private static Assistant assistant = null;
    public TokenCountEstimator tokenEstimator = null;
    public IdentifyByLLM(Project project){
        API_URL = MyPluginSettingsState.Companion.getInstance().getEndpointUrl();
        ALIYUN_API_KEY = PasswordSafe.getInstance().getPassword(new CredentialAttributes("MyLLMPluginApiServiceKey", "userApiKey"));
        MODEL_NAME = getModelName();
        try{
            model = OpenAiChatModel.builder()
                    .apiKey(ALIYUN_API_KEY)
                    .baseUrl(API_URL)
                    .modelName(MODEL_NAME)
                    .temperature(TEMPERATURE)
                    .presencePenalty(PRESENCE_PENALTY)

                    /*
                        make sure them settings are configured if you need structured outputs
                     */
//                .responseFormat("json_object")
                    .strictJsonSchema(false)
//                .logRequests(true)
//                .logResponses(true)
                    /*
                    make sure these settings are configured if you wanna use tool calls
                     */
//                .strictTools(true)
                    .build();
            if (model != null) {
                tokenEstimator = (TokenCountEstimator) model;
            } else {
                System.out.println("The model does not support direct token estimation.");
            }
            assistant = AiServices.builder(Assistant.class)
//                .hallucinatedToolNameStrategy()
                    .chatLanguageModel(model)
//                .tools(new ContextProvider())
                    .systemMessageProvider((message) -> "You are a helpful code assistant.")
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                    .build();
        }
        catch (IllegalArgumentException e){
        }
    }
    private static String getModelName() {
        String modelType = MyPluginSettingsState.Companion.getInstance().getModelType();

        if(modelType.startsWith("qwen2.5")){
            return modelType + "-instruct";
        }
        else{
            return modelType;
        }
    }
    public Results getResponse(String prompt) {
//        System.out.println(prompt);

        try{
            return assistant.chat(prompt);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
//        return assistant.chat(prompt);
    }
    public static void main(String [] args){
//        String question = "What is the square root of the sum of the numbers of letters in the words \"hello\" and \"world\"?";
//        IdentifyByLLM cc = new IdentifyByLLM();
//        System.out.println(cc.model.chat(question));
//        ClassLoader classLoader = CodeCompletion.class.getClassLoader();
//        InputStream stream = classLoader.getResourceAsStream("Prompt.txt");
//        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
//        String command;
//        Stream<String> lines = reader.lines();
//        command = lines.collect(Collectors.toSet()).stream().collect(Collectors.joining());
//        CodeCompletion codeCompletion = new CodeCompletion();
//        int tokenCount = codeCompletion.tokenEstimator.estimateTokenCount(command);
//        System.out.println(tokenCount);

    }
}
