package com.michael.Service

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*

// @State 注解定义了配置的存储方式
@State(
    name = "com.example.Service.MyPluginSettingsState", // 唯一的名称
    storages = [Storage("myLLMPluginSettings.xml")] // 保存的文件名
)
data class MyPluginSettingsState(
    // 在这里定义你的非敏感配置项
    var modelType: String = "qwen-max", // 默认模型
    var endpointUrl: String = "https://dashscope.aliyuncs.com/compatible-mode/v1"
) : PersistentStateComponent<MyPluginSettingsState> {

    override fun getState(): MyPluginSettingsState {
        return this
    }

    override fun loadState(state: MyPluginSettingsState) {
        // 当从文件中加载配置时，将状态合并到当前对象
        this.modelType = state.modelType
        this.endpointUrl = state.endpointUrl
    }

    companion object {
        // 提供一个方便的静态方法来获取服务实例
        val instance: MyPluginSettingsState
            get() = ApplicationManager.getApplication().getService(MyPluginSettingsState::class.java)
    }
}