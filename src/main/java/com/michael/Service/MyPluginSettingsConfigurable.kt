package com.michael.Service

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class MyPluginSettingsConfigurable : Configurable {

    private lateinit var mainPanel: JPanel
    private val modelTypeComboBox = ComboBox(arrayOf("qwen2.5-coder-14b", "qwen2.5-coder-32b", "deepseek-v3", "qwen-max"))
    private val apiKeyField = JBPasswordField()
    private val endpointUrlField = JBTextField("https://dashscope.aliyuncs.com/compatible-mode/v1")

    // 用于安全存储 API Key 的凭据属性
    private val credentialAttributes = CredentialAttributes(
        // ServiceName 应该是唯一的，通常用插件ID
        "MyLLMPluginApiServiceKey",
        // UserName 字段可以留空或用一个通用标识符
        "userApiKey"
    )

    override fun getDisplayName(): String {
        return "SMNIR" // 设置页面在左侧列表中显示的名字
    }
    private fun <T> setComboBoxWidthToLongestItemPrecise(comboBox: ComboBox<T>) {
        val model = comboBox.model
        if (model.size == 0) return

        val fontMetrics = comboBox.getFontMetrics(comboBox.font)

        val longestItem = (0 until model.size)
            .map { model.getElementAt(it) }
            .maxByOrNull { fontMetrics.stringWidth(it.toString()) }

        if (longestItem != null) {
            comboBox.prototypeDisplayValue = longestItem
        }
    }
    override fun createComponent(): JComponent {
        setComboBoxWidthToLongestItemPrecise(modelTypeComboBox)
        // 使用 FormBuilder 快速构建一个整齐的表单
        mainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("LLM Model:"), modelTypeComboBox, 1, false)
            .addLabeledComponent(JBLabel("API Key:"), apiKeyField, 1, false)
            .addLabeledComponent(JBLabel("Endpoint URL (Optional):"), endpointUrlField, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel

        return mainPanel
    }

    /**
     * 检查用户是否修改了设置
     */
    override fun isModified(): Boolean {
        val settings = MyPluginSettingsState.instance
        val storedApiKey = PasswordSafe.instance.getPassword(credentialAttributes)

        // 检查非敏感数据和 API Key 是否有变动
        return modelTypeComboBox.selectedItem != settings.modelType ||
                endpointUrlField.text != settings.endpointUrl ||
                String(apiKeyField.password) != storedApiKey
    }

    /**
     * 用户点击 "Apply" 或 "OK" 时调用
     */
    override fun apply() {
        val settings = MyPluginSettingsState.instance

        // 保存非敏感数据
        settings.modelType = modelTypeComboBox.selectedItem as String
        settings.endpointUrl = endpointUrlField.text

        // 使用 PasswordSafe 安全地保存 API Key
        val credentials = Credentials("userApiKey", String(apiKeyField.password))
        PasswordSafe.instance.set(credentialAttributes, credentials)
    }

    /**
     * 用户点击 "Reset" 或打开设置页面时调用，用于将 UI 重置为已保存的状态
     */
    override fun reset() {
        val settings = MyPluginSettingsState.instance

        // 加载非敏感数据到 UI
        modelTypeComboBox.selectedItem = settings.modelType
        endpointUrlField.text = settings.endpointUrl

        // 从 PasswordSafe 加载 API Key 到 UI
        val storedApiKey = PasswordSafe.instance.getPassword(credentialAttributes)
        apiKeyField.text = storedApiKey
    }

    /**
     * 关闭设置页面时释放资源
     */
    override fun disposeUIResources() {
        // 如果有需要，在这里释放资源
    }
}